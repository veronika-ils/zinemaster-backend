package com.zinemasterapp.zinemasterapp.controller;

import com.zinemasterapp.zinemasterapp.dto.*;
import com.zinemasterapp.zinemasterapp.model.Product;
import com.zinemasterapp.zinemasterapp.model.ProductRequest;
import com.zinemasterapp.zinemasterapp.model.ProductRequestItem;
import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.ProductRepository;
import com.zinemasterapp.zinemasterapp.repository.ProductRequestItemRepository;
import com.zinemasterapp.zinemasterapp.repository.ProductRequestRepository;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")//so koj URL rabotime
@CrossOrigin(origins = "*")//mu dozvoluvame na Vue
public class ProductRequestController {

    private final ProductRequestRepository requestRepo;//dvete se jpa interfejsi za rabota so SQL
    private final ProductRequestItemRepository itemRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public ProductRequestController(ProductRequestRepository requestRepo, ProductRequestItemRepository itemRepo,UserRepository userRepo,ProductRepository productRepo) {
        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    @PostMapping//sto pravime ako imame POST "ovoj URL"
    public ResponseEntity<String> createRequest(@RequestBody CreatedRequest dto) {
        String requestId;
        do {
            requestId = "R" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (requestRepo.existsById(requestId));//za sek slucaj da nema isti ID

        ProductRequest request = new ProductRequest();//kreiranata naracka
        request.setId(requestId);
        request.setUserId(dto.getUserId());
        request.setRequestDate(LocalDate.now());
        request.setStatus("pending");//default e

        requestRepo.save(request);//go zacuvuvame vo baza

        for (ProductRequestItemDTO itemDTO : dto.getItems()) {
            Product product = productRepo.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Продуктот не е најден"));

            ProductRequestItem item = new ProductRequestItem();
            item.setRequest(request);
            item.setProduct(product);
            item.setQuantityRequested(itemDTO.getQuantityRequested());
            itemRepo.save(item);

            product.setReserved(product.getReserved() + itemDTO.getQuantityRequested());
            productRepo.save(product);
        }

        return ResponseEntity.ok(requestId);
    }


    //so userId ne so username,moze da se smeni so username mozda ke e polesno
    @GetMapping("/user/{userId}")//sto pravime ako imame GET "ovoj URL"
    public ResponseEntity<List<ProductRequest>> getRequestsForUser(@PathVariable String userId) {//samo za eden korisnik
        return ResponseEntity.ok(requestRepo.findByUserId(userId));

    }

    @GetMapping//List<RequestResponse>
    public ResponseEntity<?> getAllRequests() {
        List<ProductRequest> requests = requestRepo.findAll();

        for (ProductRequest req : requests) {
            if ("pending".equalsIgnoreCase(req.getStatus())) {
                List<ProductRequestItem> items = itemRepo.findByRequestId(req.getId());

                boolean hasInaccessibleProduct = items.stream().anyMatch(item -> {
                    Product product = item.getProduct();
                    return product == null || !product.isAccessable();
                });

                if (hasInaccessibleProduct) {
                    // site produkti od reserved gi vrakjame vo dostapni
                    for (ProductRequestItem item : items) {
                        Product product = item.getProduct();
                        if (product != null) {
                            int newReserved = product.getReserved() - item.getQuantityRequested();
                            product.setReserved(Math.max(newReserved, 0));
                            productRepo.save(product);
                        }
                    }

                    // promena na status
                    req.setStatus("unavailable");
                    requestRepo.save(req);
                }
            }
        }

        List<RequestResponse> responseList = requests.stream().map(req -> {
            User user = userRepo.findById(req.getUserId()).orElse(null);
            String username = user != null ? user.getUsername() : "непознат";

            String processedByUsername = null;
            if (req.getProcessedBy() != null) {
                User admin = userRepo.findById(req.getProcessedBy()).orElse(null);
                processedByUsername = admin != null ? admin.getUsername() : req.getProcessedBy();
            }

            return new RequestResponse(
                    req.getId(),
                    req.getStatus(),
                    req.getRequestDate().toString(),
                    username,
                    processedByUsername
            );
        }).toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDetails> getRequestById(@PathVariable String id) {
        ProductRequest request = requestRepo.findById(id).orElse(null);
        if (request == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userRepo.findById(request.getUserId()).orElse(null);
        String username = (user != null) ? user.getUsername() : "непознат";

        List<ProductRequestItem> items = itemRepo.findByRequestId(id);

        List<RequestItem> itemDTOs = items.stream().map(item -> {
            Product product = item.getProduct();
            String productName = (product != null) ? product.getName() : "непознат производ";
            return new RequestItem(productName, item.getQuantityRequested());
        }).toList();

        RequestDetails dto = new RequestDetails(
                request.getId(),
                request.getStatus(),
                request.getRequestDate().toString(),
                username,
                itemDTOs
        );

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/status")
    @CrossOrigin(origins = "http://localhost:8082")
    public ResponseEntity<Void> updateStatus(@PathVariable String id,
                                             @RequestParam String status,
                                             @RequestParam String adminId) {
        ProductRequest request = requestRepo.findById(id).orElse(null);
        if (request == null) return ResponseEntity.notFound().build();

        if (!status.equalsIgnoreCase("approved") && !status.equalsIgnoreCase("rejected")) {
            return ResponseEntity.badRequest().build();
        }

        List<ProductRequestItem> items = itemRepo.findByRequestId(id);

        for (ProductRequestItem item : items) {
            Product product = item.getProduct();
            if (product == null) continue;

            int newReserved = product.getReserved() - item.getQuantityRequested();
            product.setReserved(Math.max(newReserved, 0));

            if (status.equalsIgnoreCase("approved")) {
                product.setQuantity(product.getQuantity() - item.getQuantityRequested());
            }

            productRepo.save(product);
        }

        request.setStatus(status.toLowerCase());
        request.setProcessedBy(adminId);
        requestRepo.save(request);

        return ResponseEntity.ok().build();
    }
}
