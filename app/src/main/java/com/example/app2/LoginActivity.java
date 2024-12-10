package com.example.app2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page); // This is the layout for your login page

        // Initialize Database Helper
        dbHelper = new DatabaseHelper(this);

        // Initialize SharedPreferences to manage session
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        // Check if the user is already logged in
        if (sharedPreferences.getBoolean("is_logged_in", false)) {

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        // Find UI elements
        ImageView logo = findViewById(R.id.logo);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.btn_login);
        TextView forgotPassword = findViewById(R.id.forgot_password);

        // Animate UI elements (same as your original code)
        animateUIElements(logo, email, password, loginButton, forgotPassword);

        // Set login button click listener
        loginButton.setOnClickListener(v -> {
            String user = email.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Hash the password before checking
                String hashedPassword = dbHelper.hashPassword(pass);
                if (hashedPassword != null) {
                    // Check if user exists in the database
                    if (dbHelper.checkUser(user, hashedPassword)) {
                        // If user exists, login successful, go to home page
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Retrieve user ID from the database
                        int userId = dbHelper.getUserId(user); // Assuming you have a method to get the user ID

                        // Store session data in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("is_logged_in", true);  // User is logged in
                        editor.putString("user_email", user);    // Store email
                        editor.putInt("user_id", userId);        // Store user ID
                        editor.apply();

                        // Redirect to HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If user doesn't exist
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error hashing password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set forgot password click listener
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Here we can log out the user when the app goes to the background (session ends)
        // You could also clear the session here if needed
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Optionally, clear session on stop to ensure the user logs out when leaving the app
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the app comes back from the background, check if the user is still logged in
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            // Retrieve the user ID from SharedPreferences
            int userId = sharedPreferences.getInt("user_id", -1);

            if (userId != -1) {
                // Proceed to HomeActivity
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                // If the user ID is not found, log out the user
                Toast.makeText(LoginActivity.this, "Session expired", Toast.LENGTH_SHORT).show();
                // Optionally, log out the user
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", false);
                editor.apply();
                // Redirect to login
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    // Function to animate UI elements (same as your original code)
    private void animateUIElements(ImageView logo, EditText username, EditText password, Button loginButton, TextView forgotPassword) {
        logo.setAlpha(0f);
        logo.animate().translationYBy(100f).alpha(1f).setDuration(1000).setStartDelay(300).start();

        username.setAlpha(0f);
        username.animate().alpha(1f).setDuration(800).setStartDelay(500).start();

        password.setAlpha(0f);
        password.animate().alpha(1f).setDuration(800).setStartDelay(700).start();

        loginButton.setAlpha(0f);
        loginButton.animate().alpha(1f).setDuration(800).setStartDelay(900).start();

        forgotPassword.setAlpha(0f);
        forgotPassword.animate().alpha(1f).setDuration(800).setStartDelay(1100).start();
    }
}
