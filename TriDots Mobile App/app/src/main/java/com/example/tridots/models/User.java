package com.example.tridots.models;

public class User {
    private int userId;
    private String username;
    private String profilePictureUrl;

    public User() {
        // Required for Firebase
    }

    public User(int userId, String username, String profilePictureUrl) {
        this.userId = userId;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    // Setters for Firebase deserialization (DataSnapshot.getValue(User.class))
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}