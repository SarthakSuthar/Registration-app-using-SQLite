package com.example.sqlitesampleapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
//        this.setTitle(title);
        this.message = message;
//        this.setMessage(message);
        this.imageUrl = imageUrl;
//        this.setImageUrl(imageUrl);
        this.timestamp = timestamp;
//        this.setTimestamp(timestamp);
    }

    public String getTitle() {
        return title != null ? title : "No Title";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message != null ? message : "No Message";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTimestamp() {
        return timestamp != null ? timestamp : getCurrentTimestamp();
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Helper method to get current timestamp
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }
}
