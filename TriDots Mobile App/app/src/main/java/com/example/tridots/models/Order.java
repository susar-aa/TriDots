package com.example.tridots.models;

import java.io.Serializable; // Crucial: Import Serializable
import java.math.BigDecimal; // Import for BigDecimal
import java.util.List;

public class Order implements Serializable { // Crucial: Implement Serializable
    // Recommended to include a serialVersionUID for Serializable classes
    private static final long serialVersionUID = 1L;

    private int orderId;
    private int userId;
    private int addressId;
    private String orderDate;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private String orderStatus;
    private String paymentMethod;
    private String paymentStatus;
    private String stripePaymentIntentId;
    private String transactionId;
    private List<OrderItem> items; // List of items in this order (OrderItem also needs to be Serializable)

    public Order() {
        // Default constructor
    }

    public Order(int orderId, int userId, int addressId, String orderDate, BigDecimal totalAmount, BigDecimal deliveryFee, String orderStatus, String paymentMethod, String paymentStatus, String stripePaymentIntentId, String transactionId, List<OrderItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.addressId = addressId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.deliveryFee = deliveryFee;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.transactionId = transactionId;
        this.items = items;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}