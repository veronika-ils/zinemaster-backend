package com.zinemasterapp.zinemasterapp.repository;

import com.zinemasterapp.zinemasterapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
