package com.example.app2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreditCardDataActivity extends AppCompatActivity {
    Button btn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creditcard);

        // Initialize the DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Find views by ID
        EditText tvCardholderName = findViewById(R.id.tv_cardholder_name);
        EditText tvCardNumber = findViewById(R.id.tv_card_number);
        EditText tvExpirationDate = findViewById(R.id.tv_expiration_date);
        EditText tvCvv = findViewById(R.id.tv_cvv);

        btn = findViewById(R.id.btn_save);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cardholderName = tvCardholderName.getText().toString().trim();
                String cardNumber = tvCardNumber.getText().toString().trim();
                String expirationDate = tvExpirationDate.getText().toString().trim();
                String cvv = tvCvv.getText().toString().trim();

                // Validate the inputs
                if (cardholderName.isEmpty() || cardNumber.isEmpty() || expirationDate.isEmpty() || cvv.isEmpty()) {
                    Toast.makeText(CreditCardDataActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve the user ID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1); // Default value -1 if not found

                if (userId == -1) {
                    Toast.makeText(CreditCardDataActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the user ID exists in the database
                if (!dbHelper.isUserIdValid(userId)) {
                    Toast.makeText(CreditCardDataActivity.this, "User ID not valid", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Insert the credit card details into the database
                boolean isInserted = dbHelper.insertCreditCard(userId, cardholderName, cardNumber, expirationDate, cvv);

                if (isInserted) {
                    Toast.makeText(CreditCardDataActivity.this, "Credit card details saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CreditCardDataActivity.this, CreditCardActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreditCardDataActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
