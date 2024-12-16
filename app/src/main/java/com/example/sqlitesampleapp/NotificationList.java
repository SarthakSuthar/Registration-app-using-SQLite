package com.example.sqlitesampleapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

public class NotificationList extends AppCompatActivity {

    private LinearLayout notificationsContainer;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        notificationsContainer = findViewById(R.id.notificationsContainer);
        databaseHelper = new DatabaseHelper(this);

        displayNotifications();
        
    }

    private void displayNotifications() {
        List<NotificationModel> notifications = databaseHelper.getAllNotifications();

        if (notifications != null && !notifications.isEmpty()) {
            for (NotificationModel notification : notifications) {
                View notificationView = LayoutInflater.from(this)
                        .inflate(R.layout.notification_item, notificationsContainer, false);

                TextView titleTextView = notificationView.findViewById(R.id.notificationTitle);
                TextView messageTextView = notificationView.findViewById(R.id.notificationMessage);
                TextView timestampTextView = notificationView.findViewById(R.id.notificationTimestamp);
                ImageView imageView = notificationView.findViewById(R.id.notificationImage);

                titleTextView.setText(notification.getTitle());
                messageTextView.setText(notification.getMessage());
                timestampTextView.setText(notification.getTimestamp());

                // Load image using Glide
                if (notification.getImageUrl() != null && !notification.getImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(notification.getImageUrl())
                            .into(imageView);
                } else {
                    imageView.setVisibility(View.GONE);
                }

                notificationsContainer.addView(notificationView);
            }
        }
    }


}