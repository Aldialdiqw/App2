package passwords;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import database.DatabaseHelper;
import com.example.app2.GLOBAL;
import com.example.app2.HomeActivity;
import com.example.app2.LoginActivity;
import com.example.app2.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceManager extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<Service> Passwords;
    private DatabaseHelper databaseHelper;
    private static final String TAG = "ServiceManager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwordmanager);
        GLOBAL.enableImmersiveMode(this);
        recyclerView = findViewById(R.id.recycler_view_cards);
        Button btnAddMembership = findViewById(R.id.btn_add_card);

        Passwords = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        adapter = new ServiceAdapter(Passwords, databaseHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceManager.this, ServiceActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Fetch user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        Log.d(TAG, "onCreate: User ID fetched from shared preferences: " + userId);

        if (userId != -1) {
            List<Service> allpasswords = databaseHelper.getAllPaswords(userId);
            if (allpasswords != null && !allpasswords.isEmpty()) {
                Passwords.clear();
                Passwords.addAll(allpasswords);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onCreate: Membership list set in adapter");
            } else {
                Log.d(TAG, "onCreate: No membership found for user ID: " + userId);
            }
        } else {
            Log.d(TAG, "onCreate: User ID invalid, redirecting to LoginActivity");
            Intent intent = new Intent(ServiceManager.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(ServiceManager.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}
