package com.example.app2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import database.DatabaseHelper;

public class ForgotActivity extends AppCompatActivity {
    private String verificationCode;
    private String emailToVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_page);
        GLOBAL.enableImmersiveMode(this);



        ImageView logo = findViewById(R.id.logo);
        EditText emailInput = findViewById(R.id.email);
        Button btnSend = findViewById(R.id.btn_send);
        TextView backToLogin = findViewById(R.id.back_to_login);

        animateUIElements(logo, emailInput, btnSend, backToLogin);

        backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSend.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper db = new DatabaseHelper(this);
            if (!db.emailExists(email)) {
                Toast.makeText(this, "Email not found in our records", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate the verification code
            verificationCode = generateVerificationCode();
            emailToVerify = email;

            // Send the verification email
            sendVerificationEmail(email, verificationCode);

            // Navigate to VerifyCodeActivity
            Intent intent = new Intent(ForgotActivity.this, VerifyCodeActivity.class);
            intent.putExtra("email", emailToVerify);
            intent.putExtra("code", verificationCode);
            startActivity(intent);
        });
    }

    private void animateUIElements(ImageView logo, EditText email, Button btnSend, TextView backToLogin) {
        logo.setAlpha(0f);
        logo.animate().translationYBy(100f).alpha(1f).setDuration(1000).setStartDelay(300).start();
        email.setAlpha(0f);
        email.animate().alpha(1f).setDuration(800).setStartDelay(500).start();
        btnSend.setAlpha(0f);
        btnSend.animate().alpha(1f).setDuration(800).setStartDelay(700).start();
        backToLogin.setAlpha(0f);
        backToLogin.animate().alpha(1f).setDuration(800).setStartDelay(900).start();
    }

    private void sendVerificationEmail(String email, String verificationCode) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            boolean success = sendEmail(email, verificationCode);
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(ForgotActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotActivity.this, "Failed to send email.", Toast.LENGTH_SHORT).show();
                }
            });
            executorService.shutdown();
        });
    }

    protected boolean sendEmail(String email, String verificationCode) {
        try {
            final String senderEmail = "aldi.keka@gmail.com";
            final String appPassword = "fqfu htve xroj bqwp";


            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");


            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, appPassword);
                }
            });


            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your Verification Code");
            message.setText("Your verification code is: " + verificationCode);


            Transport.send(message);
            return true;

        } catch (Exception e) {
            Log.e("ForgotActivity", "Error sending email: ", e);
            return false;
        }
    }
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

            Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
