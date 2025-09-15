package com.zinemasterapp.zinemasterapp.repository;

import com.zinemasterapp.zinemasterapp.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByAccessableTrue(Sort sort);//nov kveri metod koj e avtomatski razbran poradi imeto

    List<Product> findByCategories_IdAndAccessableTrue(String categoryId);


}
