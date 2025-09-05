package com.zinemasterapp.zinemasterapp.controller;


import com.zinemasterapp.zinemasterapp.dto.ProductDetails;
import com.zinemasterapp.zinemasterapp.model.Category;
import com.zinemasterapp.zinemasterapp.model.Product;
import com.zinemasterapp.zinemasterapp.model.ProductRequest;
import com.zinemasterapp.zinemasterapp.model.ProductRequestItem;
import com.zinemasterapp.zinemasterapp.repository.CategoryRepository;
import com.zinemasterapp.zinemasterapp.repository.ProductRepository;
import com.zinemasterapp.zinemasterapp.repository.ProductRequestItemRepository;
import com.zinemasterapp.zinemasterapp.repository.ProductRequestRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")//ke moze da GET/POST od ovde
@CrossOrigin(origins = "http://localhost:8082")//sega dozvoluvame od tuka da zemem(ovde e Vue)
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRequestItemRepository productRequestItemRepository;
    private final ProductRequestRepository productRequestRepository;

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository, ProductRequestItemRepository productRequestItemRepository, ProductRequestRepository productRequestRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productRequestItemRepository = productRequestItemRepository;
        this.productRequestRepository = productRequestRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findByAccessableTrue(Sort.by(Sort.Direction.ASC, "id"));

    }



    @DeleteMapping("/{id}")//logicko brishenje
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {//samo pravime da nemoze da se gleda vejke accessable = false
        Optional<Product> productOpt = productRepository.findById(id);//polesno e vaka da se koristi -> smeni i kaj ProductController
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOpt.get();
//        Product product = productRepository.findById(id).orElse(null);
//        if (product == null) {
//            return ResponseEntity.notFound().build();
//        }
        product.setAccessable(false);
        productRepository.save(product);
        return ResponseEntity.ok("Marked as inaccessible");

    }


    @PostMapping("/upload")
    public ResponseEntity<Product> addProductWithImage(//poubavo mozemi e so poseben kontroler za uploads?
            @RequestParam String name,
            @RequestParam int quantity,
            @RequestParam(required = false, defaultValue = "true") boolean accessable,
            @RequestParam(required = false, defaultValue = "0") int reserved,
            @RequestParam List<String> categoryIds,
            @RequestParam MultipartFile image)
    {
        try {

            String imagePath = saveImageLocally(image);
            Product product = new Product();
            product.setName(name);
            product.setQuantity(quantity);
            product.setReserved(reserved);
            product.setAccessable(accessable);
            product.setImageUrl(imagePath);

            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
            product.setCategories(categories);

            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    private String saveImageLocally(MultipartFile file) throws Exception {//MultipartFile e od Spring i pomaga da vidam za samiot file content,size ..
        String folder = "src/main/resources/static/uploads/";
        String filename = UUID.randomUUID().toString().replace("-", "").substring(0, 8) + "_" + file.getOriginalFilename();//pod koe ime da ja zacuvame
        Path path = Paths.get(folder + filename);
        Files.createDirectories(path.getParent());//za sek slucaj da ne go neame folderot
        Files.write(path, file.getBytes());//so ova se zacuvuva
        return "/uploads/" + filename;
    }

    @PutMapping("/{id}/add-quantity")
    public ResponseEntity<?> addQuantity(@PathVariable String id, @RequestBody Map<String, Integer> payload) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOpt.get();
        int quantityToAdd = payload.getOrDefault("quantityToAdd", 0);
        if (quantityToAdd <= 0) {
            return ResponseEntity.badRequest().body("Invalid quantity");
        }

        product.setQuantity(product.getQuantity() + quantityToAdd);
        productRepository.save(product);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/reservations-by-month")
    public Map<String, Integer> getReservationsByMonth(@PathVariable String id) {
        List<ProductRequestItem> items = productRequestItemRepository.findByProductId(id);//site naracki so toj proizvod

        Map<String, Integer> reservationsPerMonth = new TreeMap<>();//prazna mapa

        for (ProductRequestItem item : items) {//za baranje od baranjata
            ProductRequest request = item.getRequest();
            if (request != null && request.getRequestDate() != null) {//dali postoi narackata?
                LocalDate date = request.getRequestDate();
                String month = date.getMonth().toString() + " " + date.getYear();
                reservationsPerMonth.put(month, reservationsPerMonth.getOrDefault(month, 0) + item.getQuantityRequested());
            }
        }

        return reservationsPerMonth;//JULY : 22
        //AUGUST : 23

    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }








}
