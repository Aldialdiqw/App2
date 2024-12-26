package com.example.app2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        GLOBAL.enableImmersiveMode(this);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        ImageView logo = findViewById(R.id.logo);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.btn_login);
        TextView forgotPassword = findViewById(R.id.forgot_password);

        animateUIElements(logo, email, password, loginButton, forgotPassword);

        loginButton.setOnClickListener(v -> {
            String user = email.getText().toString();
            String pass = password.getText().toString();





            Log.d("Debug", "Hashed Password during Login: " + GLOBAL.hashPassword(pass));
            if (user.isEmpty() || pass.isEmpty() ) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                String hashedPassword = GLOBAL.hashPassword(pass);
                if (hashedPassword != null) {
                    if (dbHelper.checkUser(user, hashedPassword)) {

                        int userId = dbHelper.getUserId(user);
                        if (dbHelper.getFA2Status(userId)) {
                            String verificationCode = generateVerificationCode();
                            sendVerificationEmail(user, verificationCode);

                            Intent intent = new Intent(LoginActivity.this, TwoFactorAuthActivity.class);
                            intent.putExtra("user_email", user);
                            intent.putExtra("verification_code", verificationCode);
                            startActivity(intent);
                        } else {
                            loginUser(user);
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error hashing password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void loginUser(String user) {
        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

        int userId = dbHelper.getUserId(user);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_email", user);

        editor.putInt("user_id", userId);
        editor.apply();
        Log.d("LoginActivity", "user: " + user);

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private String generateVerificationCode() {
        return String.format("%06d", new java.util.Random().nextInt(1000000));
    }

    private void sendVerificationEmail(String email, String verificationCode) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            boolean success = sendEmail(email, verificationCode);
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(LoginActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to send email.", Toast.LENGTH_SHORT).show();
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
            message.setSubject("Your 2FA Verification Code");
            message.setText("Your 2FA verification code is: " + verificationCode);

            Transport.send(message);
            return true;

        } catch (Exception e) {
            Log.e("LoginActivity", "Error sending email: ", e);
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        Log.d("LoginActivity", "isLoggedIn: " + isLoggedIn);

        if (isLoggedIn) {
            int userId = sharedPreferences.getInt("user_id", -1);
            Log.d("LoginActivity", "User ID fetched: " + userId);

            if (userId != -1) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Session expired", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", false);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }
    }

    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private boolean shouldAllowBack() {
        return false;
    }

    private void animateUIElements(ImageView logo, EditText username, EditText password, Button loginButton, TextView forgotPassword) {
        logo.setAlpha(0f);
        logo.animate().translationYBy(100f).alpha(1f).setDuration(700).setStartDelay(300).start();

        username.setAlpha(0f);
        username.animate().alpha(1f).setDuration(500).setStartDelay(400).start();

        password.setAlpha(0f);
        password.animate().alpha(1f).setDuration(500).setStartDelay(600).start();

        loginButton.setAlpha(0f);
        loginButton.animate().alpha(1f).setDuration(500).setStartDelay(800).start();

        forgotPassword.setAlpha(0f);
        forgotPassword.animate().alpha(1f).setDuration(500).setStartDelay(1000).start();
    }
}
