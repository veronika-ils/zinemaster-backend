package com.zinemasterapp.zinemasterapp.model;


import jakarta.persistence.*;

@Entity
@Table(name = "product_request_items")
public class ProductRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String productId;
    private int quantityRequested;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = quantityRequested;
    }
}
