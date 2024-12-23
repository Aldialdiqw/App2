package com.example.app2;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
        setContentView(R.layout.login_page);
        GLOBAL.enableImmersiveMode(this);

        dbHelper = new DatabaseHelper(this);


        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);


        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }


        ImageView logo = findViewById(R.id.logo);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.btn_login);
        TextView forgotPassword = findViewById(R.id.forgot_password);


        animateUIElements(logo, email, password, loginButton, forgotPassword);


        loginButton.setOnClickListener(v -> {
            String user = email.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {

                String hashedPassword = dbHelper.hashPassword(pass);
                if (hashedPassword != null) {

                    if (dbHelper.checkUser(user, hashedPassword)) {

                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        int userId = dbHelper.getUserId(user);


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("is_logged_in", true);
                        editor.putString("user_email", user);
                        editor.putInt("user_id", userId);
                        editor.apply();
                        Log.d("LoginActivity", "user: " + user);
                        // Redirect to HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {

                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();

        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        Log.d("LoginActivity", "isLoggedIn: " + isLoggedIn); // Log the login status

        if (isLoggedIn) {
            int userId = sharedPreferences.getInt("user_id", -1);
            Log.d("LoginActivity", "User ID fetched: " + userId); // Log the user ID

            if (userId != -1) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Session expired", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", false);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }
    }


    // Function to animate UI elements (same as your original code)
    private void animateUIElements(ImageView logo, EditText username, EditText password, Button loginButton, TextView forgotPassword) {
        logo.setAlpha(0f);
        logo.animate().translationYBy(100f).alpha(1f).setDuration(700).setStartDelay(300).start();

        username.setAlpha(0f);
        username.animate().alpha(1f).setDuration(500).setStartDelay(400).start();

        password.setAlpha(0f);
        password.animate().alpha(1f).setDuration(500).setStartDelay(600).start();

        loginButton.setAlpha(0f);
        loginButton.animate().alpha(1f).setDuration(500).setStartDelay(800).start();

        forgotPassword.setAlpha(0f);
        forgotPassword.animate().alpha(1f).setDuration(500).setStartDelay(1000).start();
    }
}

