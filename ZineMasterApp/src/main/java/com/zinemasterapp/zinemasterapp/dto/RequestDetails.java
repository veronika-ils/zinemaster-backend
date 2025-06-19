package com.zinemasterapp.zinemasterapp.dto;

import java.util.List;

public class RequestDetails {

    private String id;
    private String status;
    private String requestDate;
    private String username;
    private List<RequestItem> items;

    public RequestDetails(String id, String status, String requestDate, String username, List<RequestItem> items) {
        this.id = id;
        this.status = status;
        this.requestDate = requestDate;
        this.username = username;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<RequestItem> getItems() {
        return items;
    }

    public void setItems(List<RequestItem> items) {
        this.items = items;
    }
}
