package com.zinemasterapp.zinemasterapp.controller;


import com.zinemasterapp.zinemasterapp.model.Product;
import com.zinemasterapp.zinemasterapp.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")//ke moze da GET/POST od ovde
@CrossOrigin(origins = "http://localhost:8082")//sega dozvoluvame od tuka da zemem(ovde e Vue)
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findByAccessableTrue(Sort.by(Sort.Direction.ASC, "id"));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {//samo pravime da nemoze da se gleda vejke accessable = false
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = optionalProduct.get();
        product.setAccessable(false);
        productRepository.save(product);

        return ResponseEntity.ok("Marked as inaccessible");
    }
}
