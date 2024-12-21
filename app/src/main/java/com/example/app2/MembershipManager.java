package com.example.app2;

import android.app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import creditcard.CreditCard;


public class MembershipManager extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MembershipAdapter adapter;
    private List<MembershipInfo> memberships;
    private DatabaseHelper databaseHelper;
    private static final String TAG = "MembershipManager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membershipmanager);
        GLOBAL.enableImmersiveMode(this);
        recyclerView = findViewById(R.id.recycler_view_cards);
        Button btnAddMembership = findViewById(R.id.btn_add_card);

        memberships = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        adapter = new MembershipAdapter(memberships, databaseHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MembershipManager.this, MembershipActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Fetch user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        Log.d(TAG, "onCreate: User ID fetched from shared preferences: " + userId);

        if (userId != -1) {
            List<MembershipInfo> fetchedMemberships = databaseHelper.getAllMemberships(userId);
            if (fetchedMemberships != null && !fetchedMemberships.isEmpty()) {
                memberships.clear();
                memberships.addAll(fetchedMemberships);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onCreate: Membership list set in adapter");
            } else {
                Log.d(TAG, "onCreate: No membership found for user ID: " + userId);
            }
        } else {
            Log.d(TAG, "onCreate: User ID invalid, redirecting to LoginActivity");
            Intent intent = new Intent(MembershipManager.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }
}
