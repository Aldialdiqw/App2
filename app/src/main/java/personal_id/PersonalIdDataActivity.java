package personal_id;

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

public class PersonalIdDataActivity extends AppCompatActivity {
    private EditText etFullName, etPersonalId, etDateOfBirth, etPlaceOfBirth, etDateOfIssue;
    private EditText etDateOfExpiry, etIssuedBy, etCardId, etResidence, etGender,etNationality;
    private Button btnSave;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_id);
        GLOBAL.enableImmersiveMode(this);
        databaseHelper = new DatabaseHelper(this);

        // Initialize EditText fields
        etFullName = findViewById(R.id.et_full_name);
        etPersonalId = findViewById(R.id.et_personal_id);
        etDateOfBirth = findViewById(R.id.et_date_of_birth);
        etPlaceOfBirth = findViewById(R.id.et_place_of_birth);
        etDateOfIssue = findViewById(R.id.et_date_of_issue);
        etDateOfExpiry = findViewById(R.id.et_date_of_expiry);
        etIssuedBy = findViewById(R.id.et_issued_by);
        etCardId = findViewById(R.id.et_card_id);
        etResidence = findViewById(R.id.et_residence);
        etGender = findViewById(R.id.et_gender);
        etNationality = findViewById(R.id.et_nationality);


        btnSave = findViewById(R.id.btn_save);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String personalId = etPersonalId.getText().toString().trim();
                String dateOfBirth = etDateOfBirth.getText().toString().trim();
                String placeOfBirth = etPlaceOfBirth.getText().toString().trim();
                String dateOfIssue = etDateOfIssue.getText().toString().trim();
                String dateOfExpiry = etDateOfExpiry.getText().toString().trim();
                String Nationality = etNationality.getText().toString().trim();
                String issuedBy = etIssuedBy.getText().toString().trim();
                String cardId = etCardId.getText().toString().trim();
                String residence = etResidence.getText().toString().trim();
                String gender = etGender.getText().toString().trim();

                // Validate the inputs
                if (fullName.isEmpty() || personalId.isEmpty() || dateOfBirth.isEmpty() || placeOfBirth.isEmpty() ||
                        dateOfIssue.isEmpty() || dateOfExpiry.isEmpty() || issuedBy.isEmpty() ||
                        cardId.isEmpty() || residence.isEmpty() || gender.isEmpty() || Nationality.isEmpty()) {
                    Toast.makeText(PersonalIdDataActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }



                // Retrieve the user ID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1); // Default value -1 if not found

                if (userId == -1) {
                    Toast.makeText(PersonalIdDataActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the user ID exists in the database
                if (!databaseHelper.isUserIdValid(userId)) {
                    Toast.makeText(PersonalIdDataActivity.this, "User ID not valid", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Insert the membership details into the database
                boolean isInserted = databaseHelper.insertPersonal_id(userId, fullName, personalId, dateOfBirth,
                        placeOfBirth, dateOfIssue, dateOfExpiry, Nationality,issuedBy, cardId, residence, gender);

                if (isInserted) {
                    Toast.makeText(PersonalIdDataActivity.this, "Membership details saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PersonalIdDataActivity.this, PersonalIdActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(PersonalIdDataActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(PersonalIdDataActivity.this, PersonalIdActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}
