package com.example.app2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override


        public void onCreate(SQLiteDatabase db) {
            String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
                    + COLUMN_PASSWORD + " TEXT NOT NULL"
                    + ")";
            db.execSQL(CREATE_TABLE_USERS);
            Log.d("DB_LOG", "Tabela 'users' u krijua me sukses.");
        }



        @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle upgrading the database schema if needed
        if (oldVersion < 2) {
            Log.d("DB_LOG", "Database upgraded from version " + oldVersion + " to " + newVersion);
            // You can add migration logic here if schema changes are required
        }
    }

    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!tableExists(db)) {
            Log.e("DB_ERROR", "Tabela nuk ekziston. Ndërpritje e inserimit.");
            return false;
        }

        // Hash the password before inserting it into the database
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            Log.e("DB_ERROR", "Gabim gjatë krijimit të hash-it të fjalëkalimit.");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashedPassword);

        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            Log.e("DB_ERROR", "Dështoi inserimi.");
            return false;
        }

        Log.d("DB_LOG", "Inserimi u krye me sukses.");
        return true;
    }

    private boolean tableExists(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'", null);
            boolean exists = cursor != null && cursor.moveToFirst();
            Log.d("DB_LOG", "Tabela '" + TABLE_NAME + "' ekziston: " + exists);
            return exists;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Gabim gjatë kontrollimit të tabelës: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Hash the input password
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            Log.e("DB_ERROR", "Gabim gjatë krijimit të hash-it të fjalëkalimit.");
            return false;
        }

        // Query to check for the user with hashed password
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, hashedPassword});

        boolean userExists = false;
        if (cursor != null) {
            userExists = cursor.getCount() > 0;
            cursor.close();  // Always close the cursor to avoid memory leaks
        }

        Log.d("DB_LOG", "Përdoruesi ekziston: " + userExists);

        return userExists;
    }


    // Hashing method
    String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("DB_ERROR", "Gabim gjatë hashing fjalëkalimit: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean exists = false;
        if (cursor != null) {
            exists = cursor.getCount() > 0;
            cursor.close();
        }

        Log.d("DB_LOG", "Emaili ekziston: " + exists);
        return exists;
    }
    public boolean insertCreditCard(String cardholderName, String cardNumber, String expirationDate, String cvv, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("emri", cardholderName);
        values.put("creditcard_number", cardNumber);
        values.put("expiration_date", expirationDate);
        values.put("ccv", cvv);
        values.put("user_id", userId);

        long result = db.insert("creditcard", null, values);
        if (result == -1) {
            Log.e("DB_ERROR", "Dështoi inserimi i kartës së kreditit.");
            return false;
        }

        Log.d("DB_LOG", "Inserimi i kartës së kreditit u krye me sukses.");
        return true;
    }


    // Additional helper to log database path
    public static void logDatabasePath(Context context) {
        Log.d("DB_LOG", "Database path: " + context.getDatabasePath(DATABASE_NAME).getAbsolutePath());
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id", "email"}, "email = ?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("id");

            // Check if the column index is valid
            if (columnIndex >= 0) {
                int userId = cursor.getInt(columnIndex);
                cursor.close();
                return userId;
            }
        }


        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }


}
