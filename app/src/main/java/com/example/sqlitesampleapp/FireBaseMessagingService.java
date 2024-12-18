package com.example.sqlitesampleapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

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
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message Received");

        if (remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData());

//            Map<String, String> data = remoteMessage.getData();
//            String title = data.get("title");
//            String message = data.get("message");
//            String imageUrl = data.get("image_url");

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String imageUrl = remoteMessage.getData().get("image_url");

            // Create NotificationModel
            NotificationModel notificationModel = new NotificationModel(
                    title,           // Title from payload
                    message,          // Message from payload
                    imageUrl,         // Image URL from payload
                    getCurrentTimestamp() // Generate current timestamp
            );

            // Save notification to local storage
            saveNotificationToDatabase(notificationModel);

            // Create and show notification
            sendNotification(title, message, imageUrl);

            try {
                showNotification(title, message);
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }

            Intent intent = new Intent(NOTIFICATION_RECEIVED_ACTION);
            sendBroadcast(intent);
        }

        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            sendNotification(
                    notification.getTitle(),
                    notification.getBody(),
                    null
            );
        }
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    private void saveNotificationToDatabase(NotificationModel notificationModel) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        Log.d(TAG, "In SaveNotification");

        long result = databaseHelper.saveNotification(notificationModel);
        if (result == -1) {
            Log.e(TAG, "Failed to save notification to database");
        }
    }


    private void sendNotification(String title, String message, String imageUrl) {

        Intent intent = new Intent(this, NotificationList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        Log.d(TAG, "In SendNotification");

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel for Android Oreo and above
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(0, notificationBuilder.build());


    }



    private void showNotification(String title, String message) throws IllegalAccessException, InstantiationException {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Check and request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                // Request permission
                ActivityCompat.requestPermissions(
                        ProfileActivity.class.newInstance(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
                return;
            }
        }

        // Generate a random notification ID
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
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
