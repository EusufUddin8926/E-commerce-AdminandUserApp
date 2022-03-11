package com.example.e_commerce_store.Model;

public class ProductModel {

    String productId, productName, productPrice, productIcon, timeStamp;

    public ProductModel() {
    }

    public ProductModel(String productId, String productName, String productPrice, String productIcon, String timestamp) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productIcon = productIcon;
        this.timeStamp = timestamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getTimestamp() {
        return timeStamp;
    }

    public void setTimestamp(String timestamp) {
        this.timeStamp = timestamp;
    }
}
