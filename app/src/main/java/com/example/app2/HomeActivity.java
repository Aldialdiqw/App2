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
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import creditcard.CreditCardActivity;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        // Aktivizoni Immersive Mode për fshehjen e Status dhe Navigation Bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // Fshehni ActionBar (nëse ekziston)
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
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
