package com.example.sqlitesampleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 2;
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
                + KEY_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2){
            db.execSQL("ALTER TABLE users ADD COLUMN designation TEXT");
        }
    }

    // Add User
    public long addUser(String name, String email, String phone,
                        String dob, String doj,  String department, String designation,
                        String city, String state, String country, String password) {
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
        return db.delete(TABLE_USERS, KEY_EMAIL + "=?", new String[]{userEmail});
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{KEY_NAME, KEY_EMAIL, KEY_PHONE},
                null, null, null, null, null);
    }
}
