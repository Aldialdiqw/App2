package com.example.app2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.DatabaseHelper;

public class TwoFactorAuthActivity extends AppCompatActivity {

    private EditText verificationCodeInput;
    private Button verifyButton;
    private String userEmail;
    private String sentCode;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_auth);

        // Initialize UI components
        verificationCodeInput = findViewById(R.id.verification_code_input);
        verifyButton = findViewById(R.id.verify_button);

        // Retrieve SharedPreferences for session management
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        // Retrieve email and verification code from the intent
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("user_email");
        sentCode = intent.getStringExtra("verification_code");

        verifyButton.setOnClickListener(v -> {
            String enteredCode = verificationCodeInput.getText().toString();

            // Check if the entered code matches the sent code
            if (enteredCode.isEmpty()) {
                Toast.makeText(TwoFactorAuthActivity.this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
            } else if (enteredCode.equals(sentCode)) {
                Toast.makeText(TwoFactorAuthActivity.this, "Verification successful!", Toast.LENGTH_SHORT).show();
                loginUser();
            } else {
                Toast.makeText(TwoFactorAuthActivity.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handles logging in the user after successful verification
    private void loginUser() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int userId = dbHelper.getUserId(userEmail);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_email", userEmail);
        editor.putInt("user_id", userId);
        editor.apply();

        Log.d("TwoFactorAuthActivity", "User logged in: " + userEmail);

        // Redirect to the HomeActivity
        Intent intent = new Intent(TwoFactorAuthActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(TwoFactorAuthActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}
