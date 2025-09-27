package com.example.tridots.models;

public class ServiceProvider {
    private int sellerId;
    private String name;
    private String serviceCategoryName;
    private String description;
    private String contactNumber;
    private String emailAddress;
    private String address;
    private int experienceYears;
    private String qualifications;
    private String location;
    private String availabilityStatus;
    private float reviewsAverage;
    private int reviewCount;
    private String verificationStatus;
    private String profilePicture;
    private int userId;
    private String listerName;

    public ServiceProvider(int sellerId, String name, String serviceCategoryName, String description, String contactNumber, String emailAddress, String address, int experienceYears, String qualifications, String location, String availabilityStatus, float reviewsAverage, int reviewCount, String verificationStatus, String profilePicture) {
        this.sellerId = sellerId;
        this.name = name;
        this.serviceCategoryName = serviceCategoryName;
        this.description = description;
        this.contactNumber = contactNumber;
        this.emailAddress = emailAddress;
        this.address = address;
        this.experienceYears = experienceYears;
        this.qualifications = qualifications;
        this.location = location;
        this.availabilityStatus = availabilityStatus;
        this.reviewsAverage = reviewsAverage;
        this.reviewCount = reviewCount;
        this.verificationStatus = verificationStatus;
        this.profilePicture = profilePicture;
    }

    public int getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public String getServiceCategoryName() {
        return serviceCategoryName;
    }

    public String getDescription() {
        return description;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getAddress() {
        return address;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public String getQualifications() {
        return qualifications;
    }

    public String getLocation() {
        return location;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public float getReviewsAverage() {
        return reviewsAverage;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getListerName() {
        return listerName;
    }

    public void setListerName(String listerName) {
        this.listerName = listerName;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServiceCategoryName(String serviceCategoryName) {
        this.serviceCategoryName = serviceCategoryName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public void setReviewsAverage(float reviewsAverage) {
        this.reviewsAverage = reviewsAverage;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}