package com.example.app2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import database.DatabaseHelper;

public class SignupActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        GLOBAL.enableImmersiveMode(this);
        dbHelper = new DatabaseHelper(this);

        // Find views
        ImageView logo = findViewById(R.id.logo);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);

        EditText confirmPassword = findViewById(R.id.confirm_password);
        Button signupButton = findViewById(R.id.btn_signup);
        TextView alreadyHaveAccount = findViewById(R.id.already_have_account);


        AnimateUIElements(logo, email, password, confirmPassword, signupButton, alreadyHaveAccount);


        signupButton.setOnClickListener(v -> {
            String mail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            if (mail.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            } else if (pass.length() < 6) {
                Toast.makeText(SignupActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            } else if (!isValidPassword(pass)) {
                Toast.makeText(SignupActivity.this, "Password must contain at least one uppercase letter and one number", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirmPass)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Hash the password
                String hashedPassword = GLOBAL.hashPassword(pass);

                if (hashedPassword == null) {
                    Toast.makeText(SignupActivity.this, "Error hashing password", Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean isInserted = dbHelper.insertUser(mail, hashedPassword);
                if (isInserted) {
                    Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(SignupActivity.this, "Signup Failed. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Navigate to login screen
        alreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private boolean isValidPassword(String password) {

        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).+$");
        return pattern.matcher(password).matches();
    }

    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }

    private void AnimateUIElements(ImageView logo, EditText email, EditText password, EditText confirmPassword, Button signupButton, TextView alreadyHaveAccount) {
        // Animate logo
        logo.setAlpha(0f);
        logo.animate().translationYBy(100f).alpha(1f).setDuration(800).setStartDelay(300).start();

        // Animate email field
        email.setAlpha(0f);
        email.animate().alpha(1f).setDuration(600).setStartDelay(400).start();

        // Animate password field
        password.setAlpha(0f);
        password.animate().alpha(1f).setDuration(600).setStartDelay(550).start();

        // Animate confirm password field
        confirmPassword.setAlpha(0f);
        confirmPassword.animate().alpha(1f).setDuration(600).setStartDelay(700).start();

        // Animate signup button
        signupButton.setAlpha(0f);
        signupButton.animate().alpha(1f).setDuration(600).setStartDelay(850).start();

        // Animate "Already have an account" text
        alreadyHaveAccount.setAlpha(0f);
        alreadyHaveAccount.animate().alpha(1f).setDuration(600).setStartDelay(1000).start();
    }
}
