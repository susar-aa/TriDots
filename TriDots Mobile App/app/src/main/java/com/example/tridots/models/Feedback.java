package com.example.tridots.models;

public class Feedback {
    private int feedbackId;
    private int userId;
    private String userName;
    private int rating;
    private String comment;
    private String createdAt;

    public Feedback(int feedbackId, int userId, String userName, int rating, String comment, String createdAt) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }
    // Add getters here
    public int getFeedbackId() { return feedbackId; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}