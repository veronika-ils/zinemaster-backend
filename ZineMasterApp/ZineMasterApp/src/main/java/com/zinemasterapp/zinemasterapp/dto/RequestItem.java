package com.zinemasterapp.zinemasterapp.dto;

public class RequestItem {

    private String productName;
    private int quantityRequested;

    public RequestItem(String productName, int quantityRequested) {
        this.productName = productName;
        this.quantityRequested = quantityRequested;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = quantityRequested;
    }
}
