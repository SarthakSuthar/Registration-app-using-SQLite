package com.example.sqlitesampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        //email and password verification
        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputEditText passwordInput = findViewById(R.id.passwordInput);

        Button signInBtn = findViewById(R.id.signInBtn);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignIn(emailInput,passwordInput);
            }
        });


        //new user
        TextView newUserTxt = findViewById(R.id.newUserTxt);
        SpannableString content = new SpannableString("new user?");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        newUserTxt.setText(content);


        newUserTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationPage.class);
                startActivity(intent);
            }
        });
    }

    private void performSignIn(TextInputEditText emailInput, TextInputEditText passwordInput) {
        emailInput.setError(null);
        passwordInput.setError(null);

        String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password cannot be empty");
            focusView = passwordInput;
            cancel = true;
        }

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email cannot be empty");
            focusView = emailInput;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailInput.setError("Invalid email address");
            focusView = emailInput;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }else {
            if (databaseHelper.validateUser(email, password)) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");

    }

}