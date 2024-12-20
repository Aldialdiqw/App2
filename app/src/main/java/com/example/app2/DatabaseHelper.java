package com.example.app2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import creditcard.CreditCard;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "datab.db";
    private static final int DATABASE_VERSION = 3;
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

        String CREATE_TABLE_CREDITCARD = "CREATE TABLE IF NOT EXISTS creditcard ("
                + "credit_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "emri VARCHAR(255),"
                + "creditcard_number INTEGER,"
                + "expiration_date VARCHAR(10),"
                + "ccv INTEGER,"
                + "user_id INTEGER,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";
        db.execSQL(CREATE_TABLE_CREDITCARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            Log.d("DB_LOG", "Database upgraded from version " + oldVersion + " to " + newVersion);
        }
    }

    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!tableExists(db)) {
            Log.e("DB_ERROR", "Tabela nuk ekziston. Ndërpritje e inserimit.");
            return false;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            Log.e("DB_ERROR", "Gabim gjatë krijimit të hash-it të fjalëkalimit.");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashedPassword);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    private boolean tableExists(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'", null);
            return cursor != null && cursor.moveToFirst();
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
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            Log.e("DB_ERROR", "Gabim gjatë krijimit të hash-it të fjalëkalimit.");
            return false;
        }

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, hashedPassword});

        boolean userExists = false;
        if (cursor != null) {
            userExists = cursor.getCount() > 0;
            cursor.close();
        }
        return userExists;
    }

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
            return null;
        }
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    public boolean insertCreditCard(int userId, String emri, String creditCardNumber, String expirationDate, String cvv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("emri", emri);
        contentValues.put("creditcard_number", creditCardNumber);
        contentValues.put("expiration_date", expirationDate);
        contentValues.put("ccv", cvv);
        contentValues.put("user_id", userId);

        long result = db.insert("creditcard", null, contentValues);
        return result != -1;
    }

    public boolean isUserIdValid(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    public static void logDatabasePath(Context context) {
        Log.d("DB_LOG", "Database path: " + context.getDatabasePath(DATABASE_NAME).getAbsolutePath());
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id", "email"}, "email = ?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("id");
            if (columnIndex >= 0) {
                int userId = cursor.getInt(columnIndex);
                cursor.close();
                Log.d("", "user123123: " + userId);
                return userId;

            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }

    public void deleteCard(int cardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("creditcard", "credit_id = ?", new String[]{String.valueOf(cardId)});
        db.close();
    }

    public List<CreditCard> getAllCreditCards(int userId) {
        List<CreditCard> creditCards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT credit_id, emri, creditcard_number, expiration_date, ccv FROM creditcard WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int creditId = cursor.getInt(0); // Retrieve credit_id
                String cardholderName = cursor.getString(1);
                String cardNumber = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String cvv = cursor.getString(4);

                // Pass creditId to the CreditCard constructor or add a setter if needed
                creditCards.add(new CreditCard(creditId, cardholderName, cardNumber, expirationDate, cvv));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return creditCards;
    }
}

