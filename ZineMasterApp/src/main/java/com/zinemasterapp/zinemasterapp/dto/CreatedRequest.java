package com.zinemasterapp.zinemasterapp.dto;

import java.util.List;

public class CreatedRequest {

    private String userId;
    private List<ProductRequestItemDTO> items;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ProductRequestItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ProductRequestItemDTO> items) {
        this.items = items;
    }
}
