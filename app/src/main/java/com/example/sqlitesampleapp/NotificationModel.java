package com.example.sqlitesampleapp;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private String title;
    private String message;
    private String imageUrl;
    private String timestamp;

    public NotificationModel() {
        // Empty constructor needed for Firebase
    }
    public NotificationModel(String title, String message, String imageUrl, String timestamp) {
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
