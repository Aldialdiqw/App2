package memberships;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import database.DatabaseHelper;
import com.example.app2.GLOBAL;
import com.example.app2.HomeActivity;
import com.example.app2.R;

import java.util.ArrayList;
import java.util.List;

public class MembershipManager extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MembershipAdapter adapter;
    private List<MembershipInfo> memberships;
    private DatabaseHelper databaseHelper;
    private HomeActivity HOME;
    private static final String TAG = "MembershipManager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membershipmanager);

        recyclerView = findViewById(R.id.recycler_view_cards);
        Button btnAddMembership = findViewById(R.id.btn_add_card);
        GLOBAL.enableImmersiveMode(this);
        databaseHelper = new DatabaseHelper(this);
        memberships = new ArrayList<>();
        adapter = new MembershipAdapter(memberships, databaseHelper);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddMembership.setOnClickListener(v -> {
            Intent intent = new Intent(MembershipManager.this, MembershipActivity.class);
            startActivityForResult(intent, 1);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            fetchMemberships(userId);
        } else {
            Log.d(TAG, "User ID invalid. Redirecting to LoginActivity.");
            Intent intent = new Intent(MembershipManager.this, com.example.app2.LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void fetchMemberships(int userId) {
        memberships.clear();
        List<MembershipInfo> fetchedMemberships = databaseHelper.getAllMemberships(userId);
        if (fetchedMemberships != null && !fetchedMemberships.isEmpty()) {
            memberships.addAll(fetchedMemberships);
            adapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "No memberships found for user ID: " + userId);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);
            if (userId != -1) {
                fetchMemberships(userId);
            }
        }
    }
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(MembershipManager.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}
