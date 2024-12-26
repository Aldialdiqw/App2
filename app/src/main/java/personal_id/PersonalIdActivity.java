package personal_id;

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




public class PersonalIdActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PersonalIdAdapter adapter;
    private List<PersonalID> personalIDList;
    private DatabaseHelper databaseHelper;
    private static final String TAG = "PersonalIdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membershipmanager);
        GLOBAL.enableImmersiveMode(this);
        recyclerView = findViewById(R.id.recycler_view_cards);
        Button btnAddMembership = findViewById(R.id.btn_add_card);

        personalIDList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        adapter = new PersonalIdAdapter(personalIDList, databaseHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalIdActivity.this, PersonalIdDataActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Fetch user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        Log.d(TAG, "onCreate: User ID fetched from shared preferences: " + userId);

        if (userId != -1) {
            List<PersonalID> fetchedMemberships = databaseHelper.getAllPersonal_id(userId);
            if (fetchedMemberships != null && !fetchedMemberships.isEmpty()) {
                personalIDList.clear();
                personalIDList.addAll(fetchedMemberships);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onCreate: Personal_id list set in adapter");
            } else {
                Log.d(TAG, "onCreate: No Personal_id found for user ID: " + userId);
            }
        } else {
            Log.d(TAG, "onCreate: User ID invalid, redirecting to LoginActivity");
            Intent intent = new Intent(PersonalIdActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(PersonalIdActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();;
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}
