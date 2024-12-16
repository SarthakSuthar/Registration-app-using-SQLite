package com.example.sqlitesampleapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileActivity extends AppCompatActivity {


    private DatabaseHelper databaseHelper;
    private LinearLayout userListContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);
        userListContainer = findViewById(R.id.userListContainer);


        displayUsers();

        Button notificationBtn = findViewById(R.id.notificationBtn);

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,NotificationList.class);
                startActivity(intent);
            }
        });

    }

    private void displayUsers() {
        userListContainer.removeAllViews();

        // Get readable database
        Cursor cursor = databaseHelper.getAllUsers();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Inflate the card view for each user
                View cardView = LayoutInflater.from(this)
                        .inflate(R.layout.user_details_card, userListContainer, false);

                // Find TextViews in the card view
                TextView nameTextView = cardView.findViewById(R.id.cardNameTextView);
                TextView emailTextView = cardView.findViewById(R.id.cardEmailTextView);
                TextView phoneTextView = cardView.findViewById(R.id.cardPhoneTextView);

                // Get column indices
                int nameIndex = cursor.getColumnIndex("name");
                int emailIndex = cursor.getColumnIndex("email");
                int phoneIndex = cursor.getColumnIndex("phone");


                nameTextView.setText(nameIndex != -1 ?
                        (cursor.getString(nameIndex) != null ? cursor.getString(nameIndex) : "N/A")
                        : "N/A");

                emailTextView.setText(emailIndex != -1 ?
                        (cursor.getString(emailIndex) != null ? cursor.getString(emailIndex) : "N/A")
                        : "N/A");

                phoneTextView.setText(phoneIndex != -1 ?
                        (cursor.getString(phoneIndex) != null ? cursor.getString(phoneIndex) : "N/A")
                        : "N/A");

                FloatingActionButton editBtn = cardView.findViewById(R.id.editBtn);

                final String userEmail = cursor.getString(emailIndex);
                editBtn.setTag(userEmail);

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = (String) v.getTag();
                        Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
                        intent.putExtra("EMAIL",email);
                        startActivity(intent);
                    }
                });

                FloatingActionButton mapBtn = cardView.findViewById(R.id.mapBtn);

                mapBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openUserLocation(userEmail);
                    }
                });

                userListContainer.addView(cardView);

            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openUserLocation(String userEmail){
        Intent intent = new Intent(ProfileActivity.this, DisplayLocation.class);
        intent.putExtra("EMAIL", userEmail);
        startActivity(intent);
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, you can access location
                Toast.makeText(this, "Location permission granted",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}