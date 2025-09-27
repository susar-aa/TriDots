package com.example.tridots.models; // Adjust package as needed

public class Message {
    private String messageId;
    private int senderId; // Assuming integer IDs for users
    private int receiverId; // Assuming integer IDs for users
    private String text;
    private long timestamp;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String messageId, int senderId, int receiverId, String text, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters
    public String getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters (required for Firebase if you're using DataSnapshot.getValue(Message.class))
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}