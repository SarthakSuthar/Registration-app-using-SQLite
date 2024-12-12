package com.example.sqlitesampleapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView dobTextView;
    private TextView dojTextView;
    private TextView departmentTextView;
    private TextView designationTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView countryTextView;

    private DatabaseHelper databaseHelper;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();

        userEmail = getIntent().getStringExtra("EMAIL");

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserData();

        Button editBtn = findViewById(R.id.editProfileButton);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
                intent.putExtra("EMAIL", userEmail);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {

        Cursor cursor = databaseHelper.getUserDetails(userEmail);

        if (cursor != null && cursor.moveToFirst()){
            int nameIndex = cursor.getColumnIndex("name");
            int emailIndex = cursor.getColumnIndex("email");
            int phoneIndex = cursor.getColumnIndex("phone");
            int dobIndex = cursor.getColumnIndex("dob");
            int dojIndex = cursor.getColumnIndex("doj");
            int departmentIndex = cursor.getColumnIndex("department");
            int designationIndex = cursor.getColumnIndex("designation");
            int cityIndex = cursor.getColumnIndex("city");
            int stateIndex = cursor.getColumnIndex("state");
            int countryIndex = cursor.getColumnIndex("country");

            nameTextView.setText(cursor.getString(nameIndex));
            emailTextView.setText(cursor.getString(emailIndex));
            phoneTextView.setText(cursor.getString(phoneIndex));
            dobTextView.setText(cursor.getString(dobIndex));
            dojTextView.setText(cursor.getString(dojIndex));
            departmentTextView.setText(cursor.getString(departmentIndex));
            designationTextView.setText(cursor.getString(designationIndex));
            cityTextView.setText(cursor.getString(cityIndex));
            stateTextView.setText(cursor.getString(stateIndex));
            countryTextView.setText(cursor.getString(countryIndex));

            cursor.close();
        }else {
            Toast.makeText(this,"User not found",Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void initializeViews() {
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        dobTextView = findViewById(R.id.dobTextView);
        dojTextView = findViewById(R.id.dojTextView);
        departmentTextView = findViewById(R.id.departmentTextView);
        designationTextView = findViewById(R.id.designationTextView);
        cityTextView = findViewById(R.id.cityTextView);
        stateTextView = findViewById(R.id.stateTextView);
        countryTextView = findViewById(R.id.countryTextView);

    }
}