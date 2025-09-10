package com.zinemasterapp.zinemasterapp.dto;

import com.zinemasterapp.zinemasterapp.model.ProductRequest;

public class RequestResponse {
    private String id;
    private String status;
    private String requestDate;
    private String username;
    private String processedBy;

    public RequestResponse(String id,String status,String requestDate,String username,String processedBy) {
        this.id = id;
        this.status = status;
        this.requestDate = requestDate;
        this.username = username;
        this.processedBy = processedBy;
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

    public String getStatus() {
        return status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getUsername() {
        return username;
    }
}
