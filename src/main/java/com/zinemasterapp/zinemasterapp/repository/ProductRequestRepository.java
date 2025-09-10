package com.zinemasterapp.zinemasterapp.repository;

import com.zinemasterapp.zinemasterapp.model.ProductRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRequestRepository extends JpaRepository<ProductRequest, String> {
    List<ProductRequest> findByUserId(String userId);
}
