package com.example.sqlitesampleapp;

import static com.example.sqlitesampleapp.DeleteConfirmationFragment.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM Token", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the FCM token
                    String token = task.getResult();
                    Log.d("FCM Token", "Token: " + token);

                    // You can save the token to your server or SharedPreferences
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

        requestNotificationPermission();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();

                // Log the token
                Log.d(TAG, "FCM Token: " + token);

                // Optional: Send token to your server
                // sendRegistrationToServer(token);
            }
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with notification-related tasks
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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
            passwordInput.setError("Password cannot be empty"); //red alert if field not filled
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