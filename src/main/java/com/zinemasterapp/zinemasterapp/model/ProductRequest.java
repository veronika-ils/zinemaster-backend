package com.zinemasterapp.zinemasterapp.model;


import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_requests")
public class ProductRequest {
    @Id
    private String id;

    private String userId;
    private LocalDate requestDate;
    private String status;
    private String processedBy;//novo vo baza treba da se dodadi

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = false)//site operacii na items ke se ferlektiraat i na narakcata
    private List<ProductRequestItem> items = new ArrayList<>();
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProductRequestItem> getItems() {
        return items;
    }

    public void setItems(List<ProductRequestItem> items) {
        this.items = items;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}