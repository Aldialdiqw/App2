package com.example.app2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSignup, btnLogin;
    private ImageView logo;
    private TextView terms;
    private TextView appName;
    private SQLiteDatabase db;  // Database reference
    private DatabaseHelper dbHelper; // Database helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.app_name);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Delete all users


        // Log all users in the database
        logUsers();

        // Animation for logo and app name (optional)
        animateLogoAndAppName();
        animateButton(btnSignup);
        animateButtonWithDelay(btnLogin, 300);

        // Set OnClickListeners
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

    }

    // Function to delete all users from the 'users' table


    // Log all users in the 'users' table
    private void logUsers() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM users", null);

        // Check if cursor has data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    // Ensure columns exist
                    int emailIndex = cursor.getColumnIndexOrThrow("email");
                    int passwordIndex = cursor.getColumnIndexOrThrow("password");

                    String email = cursor.getString(emailIndex);
                    String password = cursor.getString(passwordIndex);
                    Log.d("DB_LOG", "User: " + email + ", Password: " + password);
                } catch (IllegalArgumentException e) {
                    Log.e("DB_ERROR", "Column not found: " + e.getMessage());
                }
            } while (cursor.moveToNext());
        } else {
            Log.d("DB_LOG", "No users found in the database.");
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    // Fade in logo and app name
    private void animateLogoAndAppName() {
        AlphaAnimation fadeInLogo = new AlphaAnimation(0, 1);
        fadeInLogo.setDuration(1000);
        logo.startAnimation(fadeInLogo);

        AlphaAnimation fadeInAppName = new AlphaAnimation(0, 1);
        fadeInAppName.setDuration(1000);
        appName.startAnimation(fadeInAppName);
    }

    private void animateButton(Button button) {
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        button.startAnimation(fadeIn);
    }

    private void animateButtonWithDelay(Button button, int delay) {
        button.postDelayed(() -> animateButton(button), delay);
    }
}
