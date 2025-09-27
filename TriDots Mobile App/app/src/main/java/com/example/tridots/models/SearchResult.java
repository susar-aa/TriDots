package com.example.tridots.models;

public class SearchResult {
    private String type;
    // Add common fields that might appear across all result types
    private int id;
    private String name;
    private String title; // Could be product_name, vehicle_name, business_name, etc.
    private String description;
    private String imageUrl;
    private String location;
    private double amount;
    private String priceType;
    private String brand;
    private String model;
    private String service_category_name;
    private String contact_number;

    // Constructor
    public SearchResult(String type, int id, String title, String description, String imageUrl, String location, double amount, String priceType, String brand, String model, String service_category_name, String contact_number) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
        this.amount = amount;
        this.priceType = priceType;
        this.brand = brand;
        this.model = model;
        this.service_category_name = service_category_name;
        this.contact_number = contact_number;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getService_category_name() {
        return service_category_name;
    }

    public void setService_category_name(String service_category_name) {
        this.service_category_name = service_category_name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}