package creditcard;

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

import java.util.Calendar;
import java.util.regex.Pattern;



public class CreditCardDataActivity extends AppCompatActivity {
    Button btn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creditcard);
        GLOBAL.enableImmersiveMode(this);


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

                // Validate card number
                if (!isValidCardNumber(cardNumber)) {
                    Toast.makeText(CreditCardDataActivity.this, "Invalid card number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate expiration date
                if (!isValidExpirationDate(expirationDate)) {
                    Toast.makeText(CreditCardDataActivity.this, "Invalid expiration date", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate CVV
                if (!isValidCvv(cvv)) {
                    Toast.makeText(CreditCardDataActivity.this, "Invalid CVV", Toast.LENGTH_SHORT).show();
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
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    Toast.makeText(CreditCardDataActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(CreditCardDataActivity.this, CreditCardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }

    private boolean isValidCardNumber(String cardNumber) {

        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    // Method to validate expiration date (MM/YY format)
    private boolean isValidExpirationDate(String expirationDate) {
        if (!Pattern.matches("^(0[1-9]|1[0-2])/(\\d{2})$", expirationDate)) {
            return false;
        }
        // Additional logic to ensure the expiration date is in the future
        String[] parts = expirationDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000;

        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;

        return year > currentYear || (year == currentYear && month >= currentMonth);
    }

    // Method to validate CVV (3-4 digit numbers)
    private boolean isValidCvv(String cvv) {
        return Pattern.matches("^\\d{3,4}$", cvv);
    }
}
