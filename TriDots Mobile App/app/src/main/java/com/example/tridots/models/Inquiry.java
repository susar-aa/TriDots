package com.example.tridots.models;

public class Inquiry {
    private int inquiryId;
    private int userId;
    private String adType;
    private int adId;
    private int adOwnerId;
    private String inquirerName;
    private String inquirerEmail;
    private String inquirerPhone;
    private String inquiryMessage;
    private String inquiryDate;
    private String status;
    private String sellerReply;
    private double estimatedCost;
    private String adName;
    private String adImageUrl;

    public Inquiry() {
    }

    public Inquiry(int inquiryId, int userId, String adType, int adId, int adOwnerId, String inquirerName, String inquirerEmail, String inquirerPhone, String inquiryMessage, String inquiryDate, String status, String sellerReply, double estimatedCost, String adName, String adImageUrl) {
        this.inquiryId = inquiryId;
        this.userId = userId;
        this.adType = adType;
        this.adId = adId;
        this.adOwnerId = adOwnerId;
        this.inquirerName = inquirerName;
        this.inquirerEmail = inquirerEmail;
        this.inquirerPhone = inquirerPhone;
        this.inquiryMessage = inquiryMessage;
        this.inquiryDate = inquiryDate;
        this.status = status;
        this.sellerReply = sellerReply;
        this.estimatedCost = estimatedCost;
        this.adName = adName;
        this.adImageUrl = adImageUrl;
    }

    public Inquiry(int inquiryId, int userId, String adType, int adId, String inquirerName, String inquiryMessage, String inquiryDate, String status) {
        this.inquiryId = inquiryId;
        this.userId = userId;
        this.adType = adType;
        this.adId = adId;
        this.inquirerName = inquirerName;
        this.inquiryMessage = inquiryMessage;
        this.inquiryDate = inquiryDate;
        this.status = status;
    }

    public int getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(int inquiryId) {
        this.inquiryId = inquiryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public int getAdOwnerId() {
        return adOwnerId;
    }

    public void setAdOwnerId(int adOwnerId) {
        this.adOwnerId = adOwnerId;
    }

    public String getInquirerName() {
        return inquirerName;
    }

    public void setInquirerName(String inquirerName) {
        this.inquirerName = inquirerName;
    }

    public String getInquirerEmail() {
        return inquirerEmail;
    }

    public void setInquirerEmail(String inquirerEmail) {
        this.inquirerEmail = inquirerEmail;
    }

    public String getInquirerPhone() {
        return inquirerPhone;
    }

    public void setInquirerPhone(String inquirerPhone) {
        this.inquirerPhone = inquirerPhone;
    }

    public String getInquiryMessage() {
        return inquiryMessage;
    }

    public void setInquiryMessage(String inquiryMessage) {
        this.inquiryMessage = inquiryMessage;
    }

    public String getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(String inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSellerReply() {
        return sellerReply;
    }

    public void setSellerReply(String sellerReply) {
        this.sellerReply = sellerReply;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdImageUrl() {
        return adImageUrl;
    }

    public void setAdImageUrl(String adImageUrl) {
        this.adImageUrl = adImageUrl;
    }
}