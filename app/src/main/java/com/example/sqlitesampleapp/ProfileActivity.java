package com.example.sqlitesampleapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

//    private void openUserLocation(String userEmail) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Request permissions if not granted
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null){
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//
//                            // Save location to database
//                            DatabaseHelper databaseHelper = new DatabaseHelper(ProfileActivity.this);
//                            int rowsUpdated = databaseHelper.updateUserLocation(userEmail, latitude, longitude);
//
//                            if (rowsUpdated > 0) {
//                                // Open Google Maps with the location
//                                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude);
//                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                                mapIntent.setPackage("com.google.android.apps.maps");
//
//                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
//                                    startActivity(mapIntent);
//                                } else {
//                                    Toast.makeText(ProfileActivity.this, "Google Maps app not installed", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Toast.makeText(ProfileActivity.this, "Failed to save location", Toast.LENGTH_SHORT).show();
//                            }
//                        }else {
//                            Toast.makeText(ProfileActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, you can access location
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

}