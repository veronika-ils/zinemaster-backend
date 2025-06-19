package com.zinemasterapp.zinemasterapp.repository;

import com.zinemasterapp.zinemasterapp.dto.ProductRequestItemDTO;
import com.zinemasterapp.zinemasterapp.model.ProductRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRequestItemRepository extends JpaRepository<ProductRequestItem, Long> {
    List<ProductRequestItem> findByRequestId(String requestId);
}
