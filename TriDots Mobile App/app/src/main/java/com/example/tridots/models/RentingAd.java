package com.example.tridots.models;

public class RentingAd {

    private int rentId;
    private String productName;
    private String brand;
    private String model;
    private String pricePerHour;
    private String pricePerDay;
    private String productImage;
    private String productDescription;
    private String productLocation;
    private String availabilityStatus;
    private float averageRating;
    private int reviewCount;
    private String keywords;
    private int listerId; // Add this field
    private String listerName; // Add this field

    // Constructor with all fields including listerId and listerName
    public RentingAd(int rentId, String productName, String brand, String model, String pricePerHour, String pricePerDay,
                     String productImage, String productDescription, String productLocation,
                     String availabilityStatus, float averageRating, int reviewCount, String keywords,
                     int listerId, String listerName) {
        this.rentId = rentId;
        this.productName = productName;
        this.brand = brand;
        this.model = model;
        this.pricePerHour = pricePerHour;
        this.pricePerDay = pricePerDay;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.productLocation = productLocation;
        this.availabilityStatus = availabilityStatus;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.keywords = keywords;
        this.listerId = listerId;
        this.listerName = listerName;
    }

    // Getter for rentId
    public int getRentId() {
        return rentId;
    }

    // Setter for rentId (optional)
    public void setRentId(int rentId) {
        this.rentId = rentId;
    }

    // Getter for listerId
    public int getListerId() {
        return listerId;
    }

    // Setter for listerId (optional)
    public void setListerId(int listerId) {
        this.listerId = listerId;
    }

    // Getter for listerName
    public String getListerName() {
        return listerName;
    }

    // Setter for listerName (optional)
    public void setListerName(String listerName) {
        this.listerName = listerName;
    }

    // Getter methods for existing fields
    public String getProductName() {
        return productName;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getPricePerHour() {
        return pricePerHour;
    }

    public String getPricePerDay() {
        return pricePerDay;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductLocation() {
        return productLocation;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getKeywords() {
        return keywords;
    }

    // Setter methods for existing fields (optional)
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPricePerHour(String pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setPricePerDay(String pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductLocation(String productLocation) {
        this.productLocation = productLocation;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}