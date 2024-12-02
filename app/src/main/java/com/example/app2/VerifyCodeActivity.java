package com.example.app2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VerifyCodeActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_code_page);

        EditText codeInput = findViewById(R.id.code_input);
        Button btnVerify = findViewById(R.id.btn_verify);

        // Get the passed data
        String email = getIntent().getStringExtra("email");
        String expectedCode = getIntent().getStringExtra("code");

        btnVerify.setOnClickListener(v -> {
            String enteredCode = codeInput.getText().toString().trim();

            if (TextUtils.isEmpty(enteredCode)) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enteredCode.equals(expectedCode)) {
                // Navigate to ResetPasswordActivity
                Intent intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
