package com.example.app2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import SecureNotes.SecureNotesActivity;
import creditcard.CreditCardActivity;
import memberships.MembershipAdapter;
import memberships.MembershipInfo;
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
    private List<MembershipInfo> membership;
    private DatabaseHelper dbHelper;
    private static final String TAG = "HomeActivity";
    private Button FA2;
    private SharedPreferences sharedPreferences;
    private MembershipAdapter adapter;
    private Set<Integer> notifiedMemberships;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        GLOBAL.enableImmersiveMode(this);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        membership = new ArrayList<>();
        dbHelper = new DatabaseHelper(this);
        adapter = new MembershipAdapter(membership, dbHelper);

        // Load notified memberships from SharedPreferences
        notifiedMemberships = loadNotifiedMemberships();

        // Initialize UI elements
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
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            fetchMemberships(userId);
        } else {
            Log.d(TAG, "User ID invalid. Redirecting to LoginActivity.");
        }
    }

    private void fetchMemberships(int userId) {
        membership.clear();
        List<MembershipInfo> fetchedMemberships = dbHelper.getAllMemberships(userId);

        if (fetchedMemberships != null) {
            membership.addAll(fetchedMemberships);
            for (MembershipInfo membership : fetchedMemberships) {
                scheduleNotification(membership);
            }
        } else {
            Log.e(TAG, "No memberships found for this user.");
        }
    }

    public void scheduleNotification(MembershipInfo membership) {
        if (notifiedMemberships.contains(membership.getId())) {
            Log.d(TAG, "Notification already scheduled for membership: " + membership.getName());
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date date = sdf.parse(membership.getPaymentDate());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                    scheduleExactAlarm(alarmManager, calendar, membership);
                } else {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                    Log.e(TAG, "Exact alarms not allowed by user.");
                }
            } else if (alarmManager != null) {
                scheduleExactAlarm(alarmManager, calendar, membership);
            }

            notifiedMemberships.add(membership.getId());
            saveNotifiedMemberships();
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling notification for " + membership.getName(), e);
        }
    }

    public void scheduleExactAlarm(AlarmManager alarmManager, Calendar calendar, MembershipInfo membership) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("membership_name", membership.getName());
        intent.putExtra("membership_id", membership.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                membership.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "Notification scheduled for " + membership.getName());
    }

    private void logout() {
        notifiedMemberships.clear();
        saveNotifiedMemberships();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveNotifiedMemberships() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> stringSet = new HashSet<>();
        for (Integer id : notifiedMemberships) {
            stringSet.add(String.valueOf(id));
        }
        editor.putStringSet("notified_memberships", stringSet);
        editor.apply();
    }

    private Set<Integer> loadNotifiedMemberships() {
        Set<String> stringSet = sharedPreferences.getStringSet("notified_memberships", new HashSet<>());
        Set<Integer> integerSet = new HashSet<>();
        for (String id : stringSet) {
            integerSet.add(Integer.parseInt(id));
        }
        return integerSet;
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

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {
            // Do nothing
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}
