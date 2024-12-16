package com.example.sqlitesampleapp;

import static android.content.Intent.getIntent;

import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DisplayLocation extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private static final String GOOGLE_MAPS_URL = "https://www.google.com/maps/search/?api=1&query=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_location);

        databaseHelper = new DatabaseHelper(this);

        String userEmail = getIntent().getStringExtra("EMAIL");

        if (userEmail == null) {
            Toast.makeText(this, "No user email provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Retrieve user's location from database
        Cursor cursor = databaseHelper.getUserLocation(userEmail);

        if (cursor != null && cursor.moveToFirst()) {
            int latitudeIndex = cursor.getColumnIndex("latitude");
            int longitudeIndex = cursor.getColumnIndex("longitude");

            double latitude = cursor.getDouble(latitudeIndex);
            double longitude = cursor.getDouble(longitudeIndex);

            cursor.close();

            WebView webView = findViewById(R.id.locationWebView);

            // Configure WebView settings
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);

            // Set WebViewClient to prevent opening links in external browser
            webView.setWebViewClient(new WebViewClient());

            // Load Google Maps URL with the specified location
            String mapsUrl = GOOGLE_MAPS_URL + latitude + "," + longitude;
            webView.loadUrl(mapsUrl);
        } else {
            Toast.makeText(this, "No location found for this user", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}