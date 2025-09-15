package com.zinemasterapp.zinemasterapp.dto;

import java.util.List;

public class ProductDetails {
    private String name;
    private int quantity;
    private String imageUrl;
    private List<String> categoryIds;
    private String id;
    private int reserved;
    private boolean accessable;
    private List<String> categoryNames; // optional, за да прикажеш имиња на категории

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public boolean isAccessable() {
        return accessable;
    }

    public void setAccessable(boolean accessable) {
        this.accessable = accessable;
    }

    public List<String> getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> catgoryIds) {
        this.categoryIds = catgoryIds;
    }
}
