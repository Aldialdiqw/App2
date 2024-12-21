package com.example.app2;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import creditcard.CreditCardActivity;
import creditcard.CreditCardDataActivity;

public class MembershipActivity extends AppCompatActivity {
    private EditText etMembershipId, etMembershipName, etCompanyName, etPrice, etPaymentDate;
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

                // Validate the inputs
                if (MembershipName.isEmpty() || CompanyName.isEmpty() || Price.isEmpty() || PaymentDate.isEmpty()) {
                    Toast.makeText(MembershipActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Retrieve the user ID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1); // Default value -1 if not found

                if (userId == -1) {
                    Toast.makeText(MembershipActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the user ID exists in the database
                if (!dbHelper.isUserIdValid(userId)) {
                    Toast.makeText(MembershipActivity.this, "User ID not valid", Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean isInserted = dbHelper.insertMembership(userId, MembershipName, CompanyName, Price, PaymentDate);

                if (isInserted) {
                    Toast.makeText(MembershipActivity.this, "membership details saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MembershipActivity.this, CreditCardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(MembershipActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
        @Override
        public void onBackPressed() {
            if (shouldAllowBack()) {
                super.onBackPressed();
            } else {

            }
        }



    private boolean shouldAllowBack() {
        return false;
    }
}
