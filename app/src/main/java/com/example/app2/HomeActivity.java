package com.example.app2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import SecureNotes.SecureNotesActivity;
import creditcard.CreditCardActivity;
import memberships.MembershipManager;
import passwords.ServiceManager;
import personal_id.PersonalIdActivity;
import random.RandomActivity;

public class HomeActivity extends AppCompatActivity {
    private ImageView creditcard;
    private ImageView memberships;
    private ImageView Securenotes;
    private ImageView passwords;
    private ImageView personal_id;
    private ImageView other;
    private DatabaseHelper dbHelper;

    private Button FA2;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        GLOBAL.enableImmersiveMode(this);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize ImageViews and Buttons
        creditcard = findViewById(R.id.cards);
        memberships = findViewById(R.id.memberships);
        Securenotes = findViewById(R.id.Secure_Notes);
        passwords = findViewById(R.id.password);
        personal_id = findViewById(R.id.personal_ids);
        other = findViewById(R.id.other);
        FA2 = findViewById(R.id.FA2);


        setImageViewClickListeners();


        FA2.setOnClickListener(v -> toggleFA2());

        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> logout());


        updateFA2ButtonText();
    }

    private void setImageViewClickListeners() {
        creditcard.setOnClickListener(v -> startActivity(CreditCardActivity.class));
        memberships.setOnClickListener(v -> startActivity(MembershipManager.class));
        passwords.setOnClickListener(v -> startActivity(ServiceManager.class));
        Securenotes.setOnClickListener(v -> startActivity(SecureNotesActivity.class));
        personal_id.setOnClickListener(v -> startActivity(PersonalIdActivity.class));
        other.setOnClickListener(v -> startActivity(RandomActivity.class));
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(HomeActivity.this, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
        Log.d("HomeActivity", "Starting " + cls.getSimpleName());
    }

    private void toggleFA2() {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            boolean currentFA2Status = dbHelper.getFA2Status(userId);
            boolean newFA2Status = !currentFA2Status;
            dbHelper.updateFA2Status(userId, newFA2Status);
            updateFA2ButtonText();
            Toast.makeText(this, "2FA " + (newFA2Status ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFA2ButtonText() {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            boolean fa2Status = dbHelper.getFA2Status(userId);
            FA2.setText(fa2Status ? "Disable 2FA" : "Enable 2FA");
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        }else{

        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}

