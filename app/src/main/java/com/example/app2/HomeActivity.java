package com.example.app2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private ImageView creditcard;
    private ImageView memberships;
    private ImageView vouchers;
    private ImageView passwords;
    private ImageView personal_id;
    private ImageView other;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        // Initialize ImageViews
        creditcard = findViewById(R.id.cards);
        memberships = findViewById(R.id.memberships);
        vouchers = findViewById(R.id.vouchers);
        passwords = findViewById(R.id.password);
        personal_id = findViewById(R.id.personal_ids);
        other = findViewById(R.id.other);

        // Set click listeners for ImageViews
        creditcard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreditCardActivity.class);
            startActivity(intent);
            Log.d("HomeActivity", "Starting CreditCardActivity");
        });

        // Initialize and set click listener for logout button
        Button logoutButton = findViewById(R.id.btn_logout); // Ensure you have a logout button in your layout

        logoutButton.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Redirect to the login page
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
