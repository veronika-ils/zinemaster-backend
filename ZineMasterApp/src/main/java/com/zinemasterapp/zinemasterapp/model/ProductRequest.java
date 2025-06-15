package com.zinemasterapp.zinemasterapp.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "product_requests")
public class ProductRequest {
    @Id
    private String id;

    private String userId;
    private LocalDate requestDate;
    private String status;
    private String processedBy;//novo vo baza treba da se dodadi

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
