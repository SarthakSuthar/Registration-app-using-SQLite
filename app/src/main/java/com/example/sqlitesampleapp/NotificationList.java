package com.example.sqlitesampleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

public class NotificationList extends AppCompatActivity {

    private LinearLayout notificationsContainer;
    private DatabaseHelper databaseHelper;
    private BroadcastReceiver notificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        notificationsContainer = findViewById(R.id.notificationsContainer);
        databaseHelper = new DatabaseHelper(this);

        // Setup broadcast receiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setupNotificationReceiver();
        }


        displayNotifications();

    }

    private void setupNotificationReceiver() {
        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (FireBaseMessagingService.NOTIFICATION_RECEIVED_ACTION.equals(intent.getAction())) {
                    runOnUiThread(() -> {
                        notificationsContainer.removeAllViews(); // Clear old views
                        displayNotifications(); // Reload notifications from DB
                    });
                }
            }
        };

        IntentFilter filter = new IntentFilter(FireBaseMessagingService.NOTIFICATION_RECEIVED_ACTION);
        registerReceiver(notificationReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister receiver to prevent memory leaks
        if (notificationReceiver != null) {
            unregisterReceiver(notificationReceiver);
        }

    }

    private void displayNotifications() {
        try {
            // Ensure database helper is initialized
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(this);
            }

            // Retrieve notifications
            List<NotificationModel> notifications = databaseHelper.getAllNotifications();

            // Clear any existing views
            notificationsContainer.removeAllViews();

            Log.d("NotificationList", "Total notifications retrieved: " + notifications.size());

            if (notifications != null && !notifications.isEmpty()) {
                for (NotificationModel notification : notifications) {
                    // Log each notification
                    Log.d("NotificationList", "Processing Notification:" +
                            "\nTitle: " + notification.getTitle() +
                            "\nMessage: " + notification.getMessage() +
                            "\nImage URL: " + notification.getImageUrl());

                    // Inflate notification item view
                    View notificationView = LayoutInflater.from(this)
                            .inflate(R.layout.notification_item, notificationsContainer, false);

                    // Find views
                    TextView titleTextView = notificationView.findViewById(R.id.notificationTitle);
                    TextView messageTextView = notificationView.findViewById(R.id.notificationMessage);
                    TextView timestampTextView = notificationView.findViewById(R.id.notificationTimestamp);
                    ImageView imageView = notificationView.findViewById(R.id.notificationImage);

                    // Set text views with null checks
                    if (titleTextView != null) {
                        titleTextView.setText(notification.getTitle());
                    }
                    if (messageTextView != null) {
                        messageTextView.setText(notification.getMessage());
                    }
                    if (timestampTextView != null) {
                        timestampTextView.setText(notification.getTimestamp());
                    }

                    // Image loading
                    if (imageView != null) {
                        String imageUrl = notification.getImageUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            try {
                                Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)
                                        .into(imageView);
                            } catch (Exception e) {
                                Log.e("NotificationList", "Image loading error", e);
                                imageView.setVisibility(View.GONE);
                            }
                        } else {
                            imageView.setVisibility(View.GONE);
                        }
                    }

                    // Add view to container
                    notificationsContainer.addView(notificationView);
                }
            } else {
                Log.d("NotificationList", "No notifications found");
                // Optionally, show a 'no notifications' view
                View noNotificationsView = LayoutInflater.from(this)
                        .inflate(R.layout.no_notifications_view, notificationsContainer, false);
                notificationsContainer.addView(noNotificationsView);
            }
        } catch (Exception e) {
            Log.e("NotificationList", "Error displaying notifications", e);
        }
    }
}