package com.zinemasterapp.zinemasterapp.controller;


import com.zinemasterapp.zinemasterapp.model.Category;
import com.zinemasterapp.zinemasterapp.model.Product;
import com.zinemasterapp.zinemasterapp.repository.CategoryRepository;
import com.zinemasterapp.zinemasterapp.repository.ProductRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/categories")//osnovna ruta
@CrossOrigin(origins = "http://localhost:8082")//frontend mi e tuka,ako go nema ova blokirani ke se site baranja od 8082
public class CategoryController {

    private final CategoryRepository categoryRepository;//interfejsot za rabota so baza
    private final ProductRepository productRepository;

    public CategoryController(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;//isto kako @Autowired
        this.productRepository = productRepository;
    }

    @GetMapping//metod GET gi zema site
    public List<Category> getAllCategories() {
        return categoryRepository.findByAccessibleTrue();
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Category name is required.");
        }

        String uniqueId;
        do {//vejke iam vo baza id so pocnuvaat so C i zaradi toa vaka e i ova
            uniqueId = "C" + System.currentTimeMillis() + String.format("%03d", new Random().nextInt(1000));//za sek slucaj da nemoze da se sluci isto id da ima
        } while (categoryRepository.existsById(uniqueId));

        category.setId(uniqueId);
        category.setAccessible(true);

        categoryRepository.save(category);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")//logicko brisenje e i ova
    public ResponseEntity<?> DeleteCategory(@PathVariable String id, @RequestParam boolean deleteProducts) {//ova poslednoto e posle /id
        Optional<Category> categoryOpt = categoryRepository.findById(id);//polesno e vaka da se koristi -> smeni i kaj ProductController
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category category = categoryOpt.get();


        List<Product> products = productRepository.findByCategories_IdAndAccessableTrue(id);

        for (Product p : products) {
            if (deleteProducts) {
                Set<Category> productCategories = p.getCategories();
                if (productCategories.size() == 1 && productCategories.iterator().next().getId().equals(id)) {
                    p.setAccessable(false);
                }else {

                    productCategories.removeIf(cat -> cat.getId().equals(id));
                }
            } else {
                p.getCategories().removeIf(cat -> cat.getId().equals(id));
            }
        }

        productRepository.saveAll(products);

        category.setAccessible(false);
        categoryRepository.save(category);

        return ResponseEntity.ok().build();
    }



}
