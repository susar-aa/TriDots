package com.example.tridots.models;

public class SoldOrderItem {
    private int orderItemId;
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private String adImageUrl; // For product image

    // New fields for variant details
    private int variantId;
    private String variantName;
    private double variantPrice; // This might be redundant if unitPrice is already variant price
    private String variantImageUrl; // If variants have their own image

    // NEW FIELD FOR DELIVERY STATUS
    private String deliveryStatus;

    public SoldOrderItem(int orderItemId, int productId, String productName,
                         int quantity, double unitPrice, String adImageUrl,
                         int variantId, String variantName, double variantPrice, String variantImageUrl) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.adImageUrl = adImageUrl;
        this.variantId = variantId;
        this.variantName = variantName;
        this.variantPrice = variantPrice;
        this.variantImageUrl = variantImageUrl;
        // deliveryStatus will be set separately if needed, or default/added here
        // If you want to initialize it to "Pending" by default when creating the object, you can add it here.
        // For now, it will be null unless explicitly set via setDeliveryStatus.
    }

    // Getters and Setters (add for new fields)
    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getAdImageUrl() {
        return adImageUrl;
    }

    public void setAdImageUrl(String adImageUrl) {
        this.adImageUrl = adImageUrl;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public double getVariantPrice() {
        return variantPrice;
    }

    public void setVariantPrice(double variantPrice) {
        this.variantPrice = variantPrice;
    }

    public String getVariantImageUrl() {
        return variantImageUrl;
    }

    public void setVariantImageUrl(String variantImageUrl) {
        this.variantImageUrl = variantImageUrl;
    }

    // --- NEW GETTER AND SETTER FOR deliveryStatus ---
    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}