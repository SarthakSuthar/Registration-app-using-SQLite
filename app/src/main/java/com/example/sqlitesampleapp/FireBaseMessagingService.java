package com.example.sqlitesampleapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "NotificationChannel";
    public static final String NOTIFICATION_RECEIVED_ACTION = "com.example.sqlitesampleapp.NOTIFICATION_RECEIVED";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message Received");
        Log.d(TAG, "Remote Message Data: " + remoteMessage.getData());


            // Log all data in the payload
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                Log.d(TAG, "Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }

            String title = null;
            String message = null;
            String imageUrl = null;

            if (remoteMessage.getNotification() != null) {
                if (title == null) title = remoteMessage.getNotification().getTitle();  //it gets message title to db
                if (message == null) message = remoteMessage.getNotification().getBody(); // it gets message body to db
                if (imageUrl == null) imageUrl = String.valueOf(remoteMessage.getNotification().getImageUrl()); // it gets image url to database
            }


            Log.d(TAG, "Extracted Title: " + title);
            Log.d(TAG, "Extracted Message: " + message);
            Log.d(TAG, "Extracted Image URL: " + imageUrl);

            NotificationModel notificationModel = new NotificationModel(
                    title,// Title from payload
                    message,          // Message from payload
                    imageUrl,         // Image URL from payload
                    getCurrentTimestamp() // Generate current timestamp
            );



            // Save notification to local storage
            saveNotificationToDatabase(notificationModel);

            // Create and show notification
            try {
                sendNotification(title, message, imageUrl);
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }

            Intent intent = new Intent(NOTIFICATION_RECEIVED_ACTION);
            sendBroadcast(intent);
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    private void saveNotificationToDatabase(NotificationModel notificationModel) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        try {
            Log.d(TAG, "Attempting to save notification:");
            Log.d(TAG, "Title: " + notificationModel.getTitle());
            Log.d(TAG, "Message: " + notificationModel.getMessage());
            Log.d(TAG, "Image URL: " + notificationModel.getImageUrl());
            Log.d(TAG, "Timestamp: " + notificationModel.getTimestamp());

            long result = databaseHelper.saveNotification(notificationModel);

            if (result != -1) {
                Log.d(TAG, "Notification saved successfully. Row ID: " + result);
            } else {
                Log.e(TAG, "Failed to save notification to database");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving notification", e);
        } finally {
            databaseHelper.close();
        }

    }


    private void sendNotification(String title, String message, String imageUrl) throws IllegalAccessException, InstantiationException {
        Intent intent = new Intent(this, NotificationList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title != null ? title : "Default Title")
                        .setContentText(message != null ? message : "Default Message")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (imageUrl != null){
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(resource)
                                    .setBigContentTitle(title)
                                    .setSummaryText(message)
                                    .bigLargeIcon((Bitmap) null)); // Show large image in notification
                            showNotification(notificationBuilder);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.e(TAG, "Image load failed for notification");
                            showNotification(notificationBuilder); // Fallback to text-only notification
                        }
                    });
        }else {
            showNotification(notificationBuilder); // No image, show basic notification
        }

    }

    @SuppressLint("MissingPermission")
    private void showNotification(NotificationCompat.Builder builder) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        int notificationId = (int) System.currentTimeMillis(); // Unique ID for each notification
        notificationManager.notify(notificationId, builder.build());  //may throw error
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "Refreshed token: " + token);

        // Send this token to your server if needed
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Implement method to send token to your backend server
        Log.d(TAG, "Sending token to server: " + token);
    }


}
