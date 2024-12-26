package memberships;

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

import java.text.SimpleDateFormat;
import java.util.Date;

public class MembershipActivity extends AppCompatActivity {
    private EditText etMembershipName, etCompanyName, etPrice, etPaymentDate;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membership);
        GLOBAL.enableImmersiveMode(this);
        dbHelper = new DatabaseHelper(this);

        etMembershipName = findViewById(R.id.et_membership_name);
        etCompanyName = findViewById(R.id.et_company_name);
        etPrice = findViewById(R.id.et_price);
        etPaymentDate = findViewById(R.id.et_payment_date);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MembershipName = etMembershipName.getText().toString().trim();
                String CompanyName = etCompanyName.getText().toString().trim();
                String Price = etPrice.getText().toString().trim();
                String PaymentDate = etPaymentDate.getText().toString().trim();

                if (MembershipName.isEmpty() || CompanyName.isEmpty() || Price.isEmpty() || PaymentDate.isEmpty()) {
                    Toast.makeText(MembershipActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    sdf.setLenient(false);
                    Date paymentDate = sdf.parse(PaymentDate);
                    Date currentDate = new Date();

                    if (!paymentDate.after(currentDate)) {
                        Toast.makeText(MembershipActivity.this, "Payment date must be in the future", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                    int userId = sharedPreferences.getInt("user_id", -1);

                    if (userId == -1) {
                        Toast.makeText(MembershipActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!dbHelper.isUserIdValid(userId)) {
                        Toast.makeText(MembershipActivity.this, "User ID not valid", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean isInserted = dbHelper.insertMembership(userId, MembershipName, CompanyName, Price, PaymentDate);

                    if (isInserted) {
                        Toast.makeText(MembershipActivity.this, "Membership details saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MembershipActivity.this, MembershipManager.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Toast.makeText(MembershipActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MembershipActivity.this, "Invalid date format. Use dd.MM.yyyy", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(MembershipActivity.this, MembershipManager.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }
}
