package com.example.sqlitesampleapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class RegistrationPage extends AppCompatActivity {

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

    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        Button signupBtn = findViewById(R.id.signupBtn);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();

        setUpDatePicker();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });
    }

    private void performRegistration() {

        resetErrors();

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
        
        if (validateInputs(name, email, phoneNo, dob, doj, department, designation, city, state, country, password, confirmPassword)) {
         if (databaseHelper.checkEmail(email)){
             emailInput.setError("Email already exists");
             emailInput.requestFocus();
             return;
         }

//         SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
//         String obj = sdf.format(Calendar.getInstance().getTime());

         long result = databaseHelper.addUser(
                 name, email, phoneNo, dob, doj,
                 department, designation, city,
                 state, country, password
         );

         if (result > 0){
             Toast.makeText(this,"Registration Successful",Toast.LENGTH_SHORT).show();

             Intent intent = new Intent(RegistrationPage.this,ProfileActivity.class);
             intent.putExtra("EMAIL",email);
             startActivity(intent);
             finish();
         }else {
             Toast.makeText(this,"Registration Failed",Toast.LENGTH_SHORT).show();
         }
        }
    }

    private boolean validateInputs(String name, String email, String phoneNo,
                                   String dob, String doj, String department,
                                   String designation, String city,
                                   String state, String country,
                                   String password, String confirmPassword) {

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

    private void resetErrors() {
        nameInput.setError(null);
        emailInput.setError(null);
        phoneNoInput.setError(null);
        dobInput.setError(null);
        dojInput.setError(null);
        departmentInput.setError(null);
        designationInput.setError(null);
        cityInput.setError(null);
        stateInput.setError(null);
        countryInput.setError(null);
        passwordInput.setError(null);
        confirmPasswordInput.setError(null);
    }

    private void setUpDatePicker() {
        TextInputEditText dobInput = findViewById(R.id.dobInput);
        TextInputEditText dojInput = findViewById(R.id.dojInput);

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

                dobInput.setText(sdf.format(calendar.getTime()));
                dojInput.setText(sdf.format(calendar.getTime()));
            }
        };

        dojInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegistrationPage.this, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dobInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegistrationPage.this, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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