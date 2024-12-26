package random;

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
import com.example.app2.LoginActivity;
import com.example.app2.R;

import java.util.List;



public class RandomActivity extends AppCompatActivity {
    private Button btnAddCard;
    private RecyclerView recyclerViewCards;
    private RandomAdapter adapter;
    private DatabaseHelper databaseHelper;

    private static final String TAG = "RandomActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_manager);
        GLOBAL.enableImmersiveMode(this);
        Log.d(TAG, "onCreate: Activity started");


        btnAddCard = findViewById(R.id.btn_add_card);
        Log.d(TAG, "onCreate: Add card button initialized");


        btnAddCard.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: Add card button clicked");
            Intent intent = new Intent(RandomActivity.this, RandomDataActivity.class);
            startActivity(intent);
        });

        // Initialize the RecyclerView
        recyclerViewCards = findViewById(R.id.recycler_view_cards);
        recyclerViewCards.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "onCreate: Recycler view set up");


        databaseHelper = new DatabaseHelper(this);
        Log.d(TAG, "onCreate: Database helper initialized");


        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        Log.d(TAG, "onCreate: User ID fetched from shared preferences: " + userId);

        if (userId != -1) {
            List<Random> random = databaseHelper.getAllRandom(userId);
            if (random != null && !random.isEmpty()) {

                adapter = new RandomAdapter(random, databaseHelper);

                recyclerViewCards.setAdapter(adapter);
                Log.d(TAG, "onCreate: random list set in adapter");
            } else {
                Log.d(TAG, "onCreate: No randoms found for user ID: " + userId);
            }
        } else {
            Log.d(TAG, "onCreate: User ID invalid, redirecting to LoginActivity");
            Intent intent = new Intent(RandomActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }


    }

    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(RandomActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}

