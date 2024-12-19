package com.example.sqlitesampleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_USERS = "users";

    // Column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_DOB = "dob";
    private static final String KEY_DOJ = "doj";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String KEY_NOTIFICATION_ID = "id";
    private static final String KEY_NOTIFICATION_TITLE = "title";
    private static final String KEY_NOTIFICATION_MESSAGE = "message";
    private static final String KEY_NOTIFICATION_IMAGE = "image_url";
    private static final String KEY_NOTIFICATION_TIMESTAMP = "timestamp";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PHONE + " INT,"
                + KEY_DOB + " TEXT,"
                + KEY_DOJ + " TEXT,"
                + KEY_DEPARTMENT + " TEXT,"
                + KEY_DESIGNATION + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_STATE + " TEXT,"
                + KEY_COUNTRY + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL"+ ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + KEY_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NOTIFICATION_TITLE + " TEXT,"
                + KEY_NOTIFICATION_MESSAGE + " TEXT,"
                + KEY_NOTIFICATION_IMAGE + " TEXT,"
                + KEY_NOTIFICATION_TIMESTAMP + " TEXT)";
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 4) {
            Cursor cursor = null;
            try {
                // Query table info to get existing columns
                cursor = db.rawQuery("PRAGMA table_info(" + TABLE_USERS + ")", null);

                // Store existing column names in a set for fast lookup
                List<String> columnNames = new ArrayList<>();
                int nameIndex = cursor.getColumnIndex("name");

                while (cursor.moveToNext()) {
                    columnNames.add(cursor.getString(nameIndex));
                }

                // Check and add missing columns
                if (!columnNames.contains(KEY_LATITUDE)) {
                    try {
                        db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_LATITUDE + " REAL");
                        Log.i("DatabaseHelper", "Column " + KEY_LATITUDE + " added successfully.");
                    } catch (SQLiteException e) {
                        Log.e("DatabaseHelper", "Failed to add column: " + KEY_LATITUDE, e);
                    }
                }

                if (!columnNames.contains(KEY_LONGITUDE)) {
                    try {
                        db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_LONGITUDE + " REAL");
                        Log.i("DatabaseHelper", "Column " + KEY_LONGITUDE + " added successfully.");
                    } catch (SQLiteException e) {
                        Log.e("DatabaseHelper", "Failed to add column: " + KEY_LONGITUDE, e);
                    }
                }
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error during table upgrade", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

    }

    // Add User
    public long addUser(String name, String email, String phone,
                        String dob, String doj,  String department, String designation,
                        String city, String state, String country, String password,
                        double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PHONE, phone);
        values.put(KEY_DOB, dob);
        values.put(KEY_DOJ, doj);
        values.put(KEY_DEPARTMENT, department);
        values.put(KEY_DESIGNATION, designation);
        values.put(KEY_CITY, city);
        values.put(KEY_STATE, state);
        values.put(KEY_COUNTRY, country);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);

        return db.insert(TABLE_USERS, null, values);
    }

    //check if email exists
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID},
                KEY_EMAIL + "=?", new String[]{email},
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Validate User Login
    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_ID},
                KEY_EMAIL + "=? AND " + KEY_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Get User Details
    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null,
                KEY_EMAIL + "=?", new String[]{email},
                null, null, null);

    }

    // Update User Details
    public int updateUser(String email, String name, String phone,
                          String dob, String doj, String department,
                          String designation, String city, String state,
                          String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_DOB, dob);
        values.put(KEY_DOJ,doj);
        values.put(KEY_DEPARTMENT, department);
        values.put(KEY_DESIGNATION, designation);
        values.put(KEY_CITY, city);
        values.put(KEY_STATE, state);
        values.put(KEY_COUNTRY, country);

        return db.update(TABLE_USERS, values,
                KEY_EMAIL + "=?", new String[]{email});
    }

    public int deleteUser(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        cleanupOldNotifications();
        return db.delete(TABLE_USERS, KEY_EMAIL + "=?", new String[]{userEmail});

    }

    public void cleanupOldNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NOTIFICATIONS +
                " WHERE id NOT IN (SELECT id FROM " + TABLE_NOTIFICATIONS + ")";
        db.execSQL(query);
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{KEY_NAME, KEY_EMAIL, KEY_PHONE},
                null, null, null, null, null);
    }

    public int updateUserLocation(String email, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);

        return db.update(TABLE_USERS, values,
                KEY_EMAIL + "=?", new String[]{email});
    }

    // Method to get user's location
    public Cursor getUserLocation(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{KEY_LATITUDE, KEY_LONGITUDE},
                KEY_EMAIL + "=?", new String[]{email},
                null, null, null);
    }

    public long saveNotification(NotificationModel notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            // Comprehensive logging
            Log.d("DatabaseHelper", "Attempting to save notification:");
            Log.d("DatabaseHelper", "Title: " + notification.getTitle());
            Log.d("DatabaseHelper", "Message: " + notification.getMessage());
            Log.d("DatabaseHelper", "Image URL: " + notification.getImageUrl());
            Log.d("DatabaseHelper", "Timestamp: " + notification.getTimestamp());

            // Ensure values are not null
            values.put(KEY_NOTIFICATION_TITLE,
                    notification.getTitle() != null ? notification.getTitle() : "");
            values.put(KEY_NOTIFICATION_MESSAGE,
                    notification.getMessage() != null ? notification.getMessage() : "");
            values.put(KEY_NOTIFICATION_IMAGE,
                    notification.getImageUrl() != null ? notification.getImageUrl() : "");
            values.put(KEY_NOTIFICATION_TIMESTAMP,
                    notification.getTimestamp() != null ? notification.getTimestamp() : "");

            // Insert and log result
            long result = db.insert(TABLE_NOTIFICATIONS, null, values);
            Log.d("DatabaseHelper", "Notification save result: " + result);

            return result;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error saving notification", e);
            return -1;
        }
    }

    public List<NotificationModel> getAllNotifications() {
        List<NotificationModel> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try{
            Log.d("DatabaseHelper", "Database is open: " + db.isOpen());

            // Check if database exists
            if (db == null) {
                Log.e("DatabaseHelper", "Database is null");
                return notifications;
            }

            Cursor cursor = db.query(TABLE_NOTIFICATIONS,
                    null, null, null, null, null,
                    KEY_NOTIFICATION_TIMESTAMP + " DESC");

            Log.d("DatabaseHelper", "Cursor count: " + (cursor != null ? cursor.getCount() : "null cursor"));

            if (cursor != null) {
                try {
                    int titleIndex = cursor.getColumnIndex(KEY_NOTIFICATION_TITLE);
                    int messageIndex = cursor.getColumnIndex(KEY_NOTIFICATION_MESSAGE);
                    int imageIndex = cursor.getColumnIndex(KEY_NOTIFICATION_IMAGE);
                    int timestampIndex = cursor.getColumnIndex(KEY_NOTIFICATION_TIMESTAMP);

                    Log.d("DatabaseHelper", "Column indices - Title: " + titleIndex +
                            ", Message: " + messageIndex +
                            ", Image: " + imageIndex +
                            ", Timestamp: " + timestampIndex);

                    while (cursor.moveToNext()) {
                        String title = cursor.isNull(titleIndex) ? "" : cursor.getString(titleIndex);
                        String message = cursor.isNull(messageIndex) ? "" : cursor.getString(messageIndex);
                        String imageUrl = cursor.isNull(imageIndex) ? "" : cursor.getString(imageIndex);
                        String timestamp = cursor.isNull(timestampIndex) ? "" : cursor.getString(timestampIndex);

                        Log.d("DatabaseHelper", "Notification - Title: " + title +
                                ", Message: " + message +
                                ", Image: " + imageUrl +
                                ", Timestamp: " + timestamp);

                        NotificationModel notification = new NotificationModel(
                                title, message, imageUrl, timestamp
                        );
                        notifications.add(notification);
                    }

                } catch (Exception e) {
                    Log.e("DatabaseHelper", "Error reading notifications", e);
                } finally {
                    cursor.close();
                }
            }

        }catch (Exception e) {
            Log.e("DatabaseHelper", "General error in getAllNotifications", e);
        }


        Log.d("DatabaseHelper", "Total notifications retrieved: " + notifications.size());
        return notifications;
    }
}
