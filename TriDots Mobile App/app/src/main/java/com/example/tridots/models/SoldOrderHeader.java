package com.example.tridots.models;

import java.util.ArrayList;
import java.util.List;

public class SoldOrderHeader {
    private int orderId;
    private int buyerUserId;
    private String buyerUsername;
    private String orderDate;
    private String orderStatus;
    private String paymentStatus;
    private double totalAmount; // From Orders table
    private double deliveryFee; // From Orders table
    private List<SoldOrderItem> items;
    private boolean isExpanded; // To manage expand/collapse state

    public SoldOrderHeader(int orderId, int buyerUserId, String buyerUsername,
                           String orderDate, String orderStatus, String paymentStatus,
                           double totalAmount, double deliveryFee) {
        this.orderId = orderId;
        this.buyerUserId = buyerUserId;
        this.buyerUsername = buyerUsername;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.deliveryFee = deliveryFee;
        this.items = new ArrayList<>();
        this.isExpanded = false; // Default to collapsed
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(int buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public List<SoldOrderItem> getItems() {
        return items;
    }

    public void setItems(List<SoldOrderItem> items) {
        this.items = items;
    }

    public void addSoldOrderItem(SoldOrderItem item) {
        this.items.add(item);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}