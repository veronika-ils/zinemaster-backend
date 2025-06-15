package com.zinemasterapp.zinemasterapp.controller;


import com.zinemasterapp.zinemasterapp.model.Product;
import com.zinemasterapp.zinemasterapp.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

    }
}
