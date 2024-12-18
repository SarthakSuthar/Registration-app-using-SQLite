package com.example.sqlitesampleapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class EditProfile extends AppCompatActivity implements DeleteConfirmationFragment.DeleteConfirmationListener {

    private TextInputEditText nameInput ;
    private TextInputEditText emailInput ;
    private TextInputEditText phoneNoInput ;
    private TextInputEditText dobInput ;
    private TextInputEditText dojInput ;
    private TextInputEditText departmentInput ;
    private TextInputEditText designationInput ;
    private TextInputEditText cityInput ;
    private TextInputEditText stateInput ;
    private TextInputEditText countryInput ;
    private TextInputEditText passwordInput ;
    private TextInputEditText confirmPasswordInput ;

    private Button saveBtn;
    private Button deleteBtn;

    private DatabaseHelper databaseHelper;
    private String userEmail;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        databaseHelper = new DatabaseHelper(this);

        userEmail =getIntent().getStringExtra("EMAIL");

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        
        loadUserData();

        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        deleteBtn = findViewById(R.id.deleteBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        DeleteConfirmationFragment fragment = DeleteConfirmationFragment.newInstance();
        fragment.show(getSupportFragmentManager(), DeleteConfirmationFragment.TAG);
    }

    @Override
    public void onDeleteConfirmed() {
        deleteUserProfile();
    }

    @Override
    public void onDeleteCancelled() {
        // Do nothing, user canceled the deletion
    }

    private void deleteUserProfile() {
        databaseHelper.deleteUser(userEmail);
        Intent intent = new Intent(EditProfile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void updateProfile() {
        String name = Objects.requireNonNull(nameInput.getText()).toString().trim();
        String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
        String phoneNo = Objects.requireNonNull(phoneNoInput.getText()).toString().trim();
        String dob = Objects.requireNonNull(dobInput.getText()).toString().trim();
        String doj = Objects.requireNonNull(dojInput.getText()).toString().trim();
        String department = Objects.requireNonNull(departmentInput.getText()).toString().trim();
        String designation = Objects.requireNonNull(designationInput.getText()).toString().trim();
        String city = Objects.requireNonNull(cityInput.getText()).toString().trim();
        String state = Objects.requireNonNull(stateInput.getText()).toString().trim();
        String country = Objects.requireNonNull(countryInput.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString().trim();

        if (validateInputs(name, email, phoneNo, dob, doj, department, designation, city, state, country, password, confirmPassword)){

            int result = databaseHelper.updateUser(email, name, phoneNo, dob, doj, department,
                    designation, city, state, country);

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                double latitude = location != null ? location.getLatitude() : 0.0;
                                double longitude = location != null ? location.getLongitude() : 0.0;

                                requestLocationPermission();
                                databaseHelper.updateUserLocation(email, latitude, longitude);
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

            }

            if (result > 0){
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
                intent.putExtra("EMAIL",userEmail);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Location permission is required for user registration.", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean validateInputs(String name, String email, String phoneNo, String dob, String doj, String department, String designation, String city, String state, String country, String password, String confirmPassword) {
        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(name)){
            nameInput.setError("Name cannot be empty");
            focusView = nameInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)){
            emailInput.setError("Email cannot be empty");
            focusView = emailInput;
            cancel = true;
        }else if (!isEmailValid(email)){
            emailInput.setError("Invalid email address");
            focusView = emailInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(phoneNo)){
            phoneNoInput.setError("Phone number cannot be empty");
            focusView = phoneNoInput;
            cancel = true;
        }else if (!isPhoneValid(phoneNo)){
            phoneNoInput.setError("Invalid phone number");
            focusView = phoneNoInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(dob)){
            dobInput.setError("Date of birth cannot be empty");
            focusView = dobInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(doj)){
            dojInput.setError("Date of joining cannot be empty");
            focusView = dojInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(department)){
            departmentInput.setError("Department cannot be empty");
            focusView = departmentInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(designation)){
            designationInput.setError("Designation cannot be empty");
            focusView = designationInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(city)){
            cityInput.setError("City cannot be empty");
            focusView = cityInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(state)){
            stateInput.setError("State cannot be empty");
            focusView = stateInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(country)){
            countryInput.setError("Country cannot be empty");
            focusView = countryInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)){
            passwordInput.setError("Password cannot be empty");
            focusView = passwordInput;
            cancel = true;
        }else if (password.length() < 5){
            passwordInput.setError("Password must be at least 5 characters long");
            focusView = passwordInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmPassword)){
            confirmPasswordInput.setError("Confirm password cannot be empty");
            focusView = confirmPasswordInput;
            cancel = true;
        }else if (!password.equals(confirmPassword)){
            confirmPasswordInput.setError("Passwords do not match");
            focusView = confirmPasswordInput;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isPhoneValid(String phoneNo) {
        return phoneNo.length() == 10 && TextUtils.isDigitsOnly(phoneNo);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void loadUserData() {
        Cursor cursor = databaseHelper.getUserDetails(userEmail);

        try {

            if (cursor != null && cursor.moveToFirst()) {
//            int idIndex = cursor.getColumnIndex("DatabaseHelper.KEY_ID");
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
                int passwordIndex = cursor.getColumnIndex( "password");
                int confirmPasswordIndex = cursor.getColumnIndex("confirmPassword");

                // Additional null/invalid index checks
                if (nameIndex == -1 || emailIndex == -1 || phoneIndex == -1 ||
                        dobIndex == -1 || dojIndex == -1 || departmentIndex == -1 ||
                        designationIndex == -1 || cityIndex == -1 || stateIndex == -1 ||
                        countryIndex == -1 || passwordIndex == -1) {

                    Toast.makeText(this, "Error: Invalid database column", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                safeSetText(nameInput, cursor, nameIndex);
                safeSetText(emailInput, cursor, emailIndex);
                safeSetText(phoneNoInput, cursor, phoneIndex);
                safeSetText(dobInput, cursor, dobIndex);
                safeSetText(dojInput, cursor, dojIndex);
                safeSetText(departmentInput, cursor, departmentIndex);
                safeSetText(designationInput, cursor, designationIndex);
                safeSetText(cityInput, cursor, cityIndex);
                safeSetText(stateInput, cursor, stateIndex);
                safeSetText(countryInput, cursor, countryIndex);
                safeSetText(passwordInput, cursor, passwordIndex);
                safeSetText(confirmPasswordInput, cursor, confirmPasswordIndex);

                cursor.close();
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        catch (Exception e){
            Log.e("EditProfile", "Error loading user data",e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void safeSetText(TextInputEditText editText, Cursor cursor, int columnIndex) {
        try {
            String value = cursor.getString(columnIndex);
            editText.setText(value);
        } catch (Exception e) {
            editText.setText(""); // Set empty if unable to read
        }
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.name);
        emailInput = findViewById(R.id.email);
        phoneNoInput = findViewById(R.id.phoneNo);
        dobInput = findViewById(R.id.dobInput);
        dojInput = findViewById(R.id.dojInput);
        departmentInput = findViewById(R.id.department);
        designationInput = findViewById(R.id.designation);
        cityInput = findViewById(R.id.city);
        stateInput = findViewById(R.id.state);
        countryInput = findViewById(R.id.country);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.confirmPassword);


        dobInput.setFocusable(false);
        dojInput.setFocusable(false);

        dobInput.setClickable(true);
        dojInput.setClickable(true);

    }
}