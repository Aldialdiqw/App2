package com.example.app2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.DatabaseHelper;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_page);
        GLOBAL.enableImmersiveMode(this);

        Log.d(TAG, "onCreate: ResetPasswordActivity started");



        // Get references to UI components
        EditText newPasswordInput = findViewById(R.id.new_password);
        EditText confirmPasswordInput = findViewById(R.id.confirm_password);
        Button btnReset = findViewById(R.id.btn_reset);

        Log.d(TAG, "onCreate: UI components initialized");

        // Retrieve the email passed from the previous activity
        String email = getIntent().getStringExtra("email");
        Log.d(TAG, "onCreate: Retrieved email: " + email);

        btnReset.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Reset button clicked");

            // Get the entered passwords
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            Log.d(TAG, "onClick: New password entered: " + newPassword);
            Log.d(TAG, "onClick: Confirm password entered: " + confirmPassword);

            // Validate input fields
            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Log.d(TAG, "onClick: Validation failed - empty fields");
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!newPassword.equals(confirmPassword)) {
                Log.d(TAG, "onClick: Validation failed - passwords do not match");
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hash the password
            String hashedPassword = GLOBAL.hashPassword(newPassword);
            if (hashedPassword == null) {
                Log.d(TAG, "onClick: Password hashing failed");
                Toast.makeText(this, "Failed to hash password", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "onClick: Password hashed successfully");
            Log.d("Debug", "Reset email: " + email);
            Log.d("Debug", "Hashed Password during Reset: " + hashedPassword);
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase database = null;
            try {
                database = dbHelper.getWritableDatabase();
                Log.d(TAG, "onClick: Database opened for writing");

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword);
                Log.d(TAG, "onClick: ContentValues prepared with new password");

                int rows = database.update(
                        DatabaseHelper.TABLE_NAME,
                        values,
                        DatabaseHelper.COLUMN_EMAIL + "=?",
                        new String[]{email}
                );

                Log.d(TAG, "onClick: Update query executed, rows affected: " + rows);

                if (rows > 0) {
                    Log.d(TAG, "onClick: Password reset successful");
                    Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();

                    // Redirect to login activity
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    Log.d(TAG, "onClick: Redirected to LoginActivity");
                } else {
                    Log.d(TAG, "onClick: Password reset failed - email not found");
                    Toast.makeText(this, "Failed to reset password. Email not found.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "onClick: Error updating password", e);
                Toast.makeText(this, "An error occurred while resetting the password.", Toast.LENGTH_SHORT).show();
            } finally {
                if (database != null) {
                    database.close();
                    Log.d(TAG, "onClick: Database connection closed");
                }
            }
        });
    }
}
