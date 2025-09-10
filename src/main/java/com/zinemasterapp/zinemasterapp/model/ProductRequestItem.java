package com.zinemasterapp.zinemasterapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "product_request_items")
public class ProductRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantityRequested;

    @ManyToOne
    @JoinColumn(name = "request_id")
    @JsonIgnore//za da ne e vo beskonecen ciklus
    private ProductRequest request;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductRequest getRequest() {
        return request;
    }

    public void setRequest(ProductRequest request) {
        this.request = request;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public int getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = quantityRequested;
    }
}