package com.example.tridots.models;

import java.io.Serializable;
import java.math.BigDecimal; // Import BigDecimal if delivery_fee can be decimal

/**
 * Model class representing an Address.
 * Implements Serializable to allow passing between Activities via Intents.
 */
public class Address implements Serializable {
    private int addressId;
    private int userId;
    private String addressTitle;
    private String fullAddress;
    private String streetNumber; // Optional
    private String streetName;   // Optional
    private String city;
    private String district;     // Optional
    private String province;     // Optional
    private String postalCode;
    private String country;      // Optional
    private boolean isDefault;
    private String updatedAt;    // Optional
    private BigDecimal delivery_fee; // Optional: This might be a default fee for an address type, or for displaying.

    public Address() {
        // Default constructor
    }

    // --- Getters and Setters for Address properties ---

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(BigDecimal delivery_fee) {
        this.delivery_fee = delivery_fee;
    }
}
