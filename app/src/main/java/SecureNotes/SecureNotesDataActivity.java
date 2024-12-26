package SecureNotes;

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

public class SecureNotesDataActivity  extends AppCompatActivity {
    private EditText n_id, notetitle, note;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.securenotes);
        GLOBAL.enableImmersiveMode(this);
        dbHelper = new DatabaseHelper(this);


        notetitle = findViewById(R.id.et_note_title);
        note = findViewById(R.id.et_note_content);
        btnSubmit = findViewById(R.id.btn_save_note);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String notetitle1 = notetitle.getText().toString().trim();
                String note1 = note.getText().toString().trim();


                // Validate the inputs
                if (notetitle1.isEmpty() || note1.isEmpty() ) {
                    Toast.makeText(SecureNotesDataActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Retrieve the user ID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1); // Default value -1 if not found

                if (userId == -1) {
                    Toast.makeText(SecureNotesDataActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the user ID exists in the database
                if (!dbHelper.isUserIdValid(userId)) {
                    Toast.makeText(SecureNotesDataActivity.this, "User ID not valid", Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean isInserted = dbHelper.insertNote(userId, notetitle1, note1);

                if (isInserted) {
                    Toast.makeText(SecureNotesDataActivity.this, "membership details saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SecureNotesDataActivity.this, SecureNotesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(SecureNotesDataActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(SecureNotesDataActivity.this, SecureNotesActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}


