package com.zinemasterapp.zinemasterapp.controller;


import com.zinemasterapp.zinemasterapp.model.Category;
import com.zinemasterapp.zinemasterapp.repository.CategoryRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")//osnovna ruta
@CrossOrigin(origins = "http://localhost:8082")//frontend mi e tuka
public class CategoryController {

    private final CategoryRepository categoryRepository;//interfejsot za rabota so baza

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping//metod GET gi zema site
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
