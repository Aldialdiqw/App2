package random;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.DatabaseHelper;
import com.example.app2.GLOBAL;
import com.example.app2.R;


public class RandomDataActivity extends AppCompatActivity {
    private EditText n_id, title, description;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random);
        GLOBAL.enableImmersiveMode(this);
        dbHelper = new DatabaseHelper(this);


        title = findViewById(R.id.et_name);
        description = findViewById(R.id.et_description);
        btnSubmit = findViewById(R.id.btn_submit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title1 = title.getText().toString().trim();
                String description1 = description.getText().toString().trim();


                // Validate the inputs
                if (title1.isEmpty() || description1.isEmpty() ) {
                    Toast.makeText(RandomDataActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Retrieve the user ID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1); // Default value -1 if not found

                if (userId == -1) {
                    Toast.makeText(RandomDataActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the user ID exists in the database
                if (!dbHelper.isUserIdValid(userId)) {
                    Toast.makeText(RandomDataActivity.this, "User ID not valid", Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean isInserted = dbHelper.insertRandom(userId, title1, description1);

                if (isInserted) {
                    Toast.makeText(RandomDataActivity.this, "membership details saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RandomDataActivity.this, RandomActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(RandomDataActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(RandomDataActivity.this, RandomActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}


