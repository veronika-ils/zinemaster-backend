package com.zinemasterapp.zinemasterapp.model;


import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    private String id;

    private String name;
    private int quantity;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "reserved")
    private int reserved;

    @Column(nullable = false,name = "accessable")
    private boolean accessable = true;

    public boolean isAccessable() {
        return accessable;
    }

    public void setAccessable(boolean accessable) {
        this.accessable = accessable;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
