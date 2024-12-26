package passwords;

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

public class ServiceActivity extends AppCompatActivity {
    private EditText ServiceName, UserName, Password;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        GLOBAL.enableImmersiveMode(this);
        dbHelper = new DatabaseHelper(this);


        ServiceName = findViewById(R.id.et_service_name);
        UserName = findViewById(R.id.et_username);
        Password = findViewById(R.id.et_password);
        btnSubmit = findViewById(R.id.btn_save);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String MembershipName = ServiceName.getText().toString().trim();
                String CompanyName = UserName.getText().toString().trim();
                String Price = Password.getText().toString().trim();


                // Validate the inputs
                if (MembershipName.isEmpty() || CompanyName.isEmpty() || Price.isEmpty() ) {
                    Toast.makeText(ServiceActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Retrieve the user ID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1); // Default value -1 if not found

                if (userId == -1) {
                    Toast.makeText(ServiceActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the user ID exists in the database
                if (!dbHelper.isUserIdValid(userId)) {
                    Toast.makeText(ServiceActivity.this, "User ID not valid", Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean isInserted = dbHelper.insertPassword(userId, MembershipName, CompanyName, Price);

                if (isInserted) {
                    Toast.makeText(ServiceActivity.this, "membership details saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ServiceActivity.this, ServiceManager
                            .class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(ServiceActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(ServiceActivity.this, ServiceManager.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}