package com.zinemasterapp.zinemasterapp.repository;

import com.zinemasterapp.zinemasterapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findByAccessibleTrue();
}
