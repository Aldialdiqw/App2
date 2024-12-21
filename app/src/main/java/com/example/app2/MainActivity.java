package com.example.app2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSignup, btnLogin;
    private ImageView logo;
    private TextView terms;
    private TextView appName;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLOBAL.enableImmersiveMode(this);

        // Initialize views
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        logo = findViewById(R.id.logo);


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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        // Fade-in effect for the logo
        AlphaAnimation fadeInLogo = new AlphaAnimation(0, 1);
        fadeInLogo.setDuration(1000);
        logo.startAnimation(fadeInLogo);


        ObjectAnimator glowAnimator = ObjectAnimator.ofFloat(logo, "alpha", 1f, 0.5f, 1f);
        glowAnimator.setDuration(1500);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.start();
        // Add a scaling effect to the logo (similar to button scaling)
        ScaleAnimation scaleUp = new ScaleAnimation(1, 1f, 1, 1);  // Slight scale-up effect
        scaleUp.setDuration(500);
        scaleUp.setRepeatMode(Animation.REVERSE);
        scaleUp.setRepeatCount(1);
        logo.startAnimation(scaleUp);
    }

    private void animateButton(Button button) {
        // Fade-in effect for the button
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);

        // Glow effect by adding shadow layer
        button.setShadowLayer(10, 0, 0, Color.parseColor("#F3E5F5")); // glow with a custom color
        button.startAnimation(fadeIn);


    }

    private void animateButtonWithDelay(Button button, int delay) {
        button.postDelayed(() -> animateButton(button), delay);
    }

    private void animateButtonWithRotation(Button button) {
        // Adding rotation animation
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE); // repeat indefinitely
        button.startAnimation(rotate);
    }
}