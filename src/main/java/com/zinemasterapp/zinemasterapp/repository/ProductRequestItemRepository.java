package com.zinemasterapp.zinemasterapp.repository;

import com.zinemasterapp.zinemasterapp.dto.ProductRequestItemDTO;
import com.zinemasterapp.zinemasterapp.model.ProductRequestItem;
import com.zinemasterapp.zinemasterapp.projections.DailyQty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRequestItemRepository extends JpaRepository<ProductRequestItem, Long> {
    List<ProductRequestItem> findByRequestId(String requestId);
    List<ProductRequestItem> findByProductId(String productId);

    @Query(value = """
  WITH bounds AS (
    SELECT p.id AS product_id,
           CAST(p.added_at AS date) AS start_day
    FROM products p
    WHERE p.id = :productId
  ),
  days AS (
    SELECT CAST(
             generate_series(
               (SELECT start_day FROM bounds),
               CURRENT_DATE,
               interval '1 day'
             )
           AS date) AS day
  ),
  daily AS (
    SELECT d.day,
           COALESCE(SUM(i.quantity_requested), 0) AS qty
    FROM days d
    LEFT JOIN product_request_items i
      ON i.product_id = (SELECT product_id FROM bounds)
     AND CAST(i.reserved_at AS date) = d.day
    GROUP BY d.day
    ORDER BY d.day
  )
  SELECT day, qty FROM daily
  """, nativeQuery = true)
    List<DailyQty> findDailyByProduct(@Param("productId") String productId);


}
