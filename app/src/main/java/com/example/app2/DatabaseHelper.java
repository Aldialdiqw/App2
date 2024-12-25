package com.example.app2;

import android.annotation.SuppressLint;
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

import SecureNotes.Note;

import memberships.MembershipInfo;
import passwords.Service;
import creditcard.CreditCard;
import personal_id.PersonalID;
import random.Random;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data10.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_NAME = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_FA2 = "FA2";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + "FA2 BOOLEAN DEFAULT FALSE"
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

        String CREATE_MEMBERSHIP_TABLE = "CREATE TABLE IF NOT EXISTS membership_new (" +
                "m_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "membershipName TEXT, " +
                "companyName TEXT, " +
                "user_id INTEGER, " +
                "price REAL, " +
                "date_due TEXT)";
        db.execSQL(CREATE_MEMBERSHIP_TABLE);

        String CREATE_passwords = "CREATE TABLE IF NOT EXISTS passwords ("
                + "p_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "servicename TEXT, "
                + "username TEXT, "
                + "password INTEGER, "
                + "user_id INTEGER, "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id)"
                + ")";
        db.execSQL(CREATE_passwords);

        String CREATE_SecureNote = "CREATE TABLE IF NOT EXISTS SecureNote ("
                + "n_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "notetitle TEXT, "
                + "note TEXT, "
                + "user_id INTEGER, "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id)"
                + ")";
        db.execSQL(CREATE_SecureNote);

        String CREATE_personal_id = "CREATE TABLE IF NOT EXISTS personal_id ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "full_name TEXT NOT NULL, "
                + "personal_id TEXT NOT NULL, "
                + "date_of_birth TEXT NOT NULL, "
                + "place_of_birth TEXT NOT NULL, "
                + "date_of_issue TEXT NOT NULL, "
                + "date_of_expiry TEXT NOT NULL, "
                + "issued_by TEXT NOT NULL, "
                + "card_id TEXT NOT NULL, "
                + "residence TEXT NOT NULL, "
                + "gender TEXT NOT NULL, "
                + "nationality TEXT NOT NULL, "
                + "user_id INTEGER, "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id)"
                + ")";
        db.execSQL(CREATE_personal_id);
        String CREATE_Random = "CREATE TABLE IF NOT EXISTS Random ("
                + "r_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, "
                + "description TEXT, "
                + "user_id INTEGER, "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id)"
                + ")";
        db.execSQL(CREATE_Random);
    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public boolean getFA2Status(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_FA2 + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        boolean fa2Status = false;
        if (cursor.moveToFirst()) {
            fa2Status = cursor.getInt(0) == 1;
        }
        cursor.close();
        return fa2Status;
    }

    public void updateFA2Status(int userId, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FA2, status ? 1 : 0);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
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

    public void deleteCard(int cardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("creditcard", "credit_id = ?", new String[]{String.valueOf(cardId)});
        db.close();
    }

    public boolean insertMembership(int userId, String membershipName, String companyName, String price, String date_due) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("membershipName", membershipName);
        contentValues.put("companyName", companyName);
        contentValues.put("price", price);
        contentValues.put("date_due", date_due);
        contentValues.put("user_id", userId);

        long result = db.insert("membership_new", null, contentValues);
        return result != -1;
    }

    public void deleteMembership(int membershipId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("membership_new", "m_id = ?", new String[]{String.valueOf(membershipId)});
        db.close();
    }

    public List<MembershipInfo> getAllMemberships(int userId) {
        List<MembershipInfo> memberships = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m_id, membershipName, companyName, price, date_due FROM membership_new WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int m_Id = cursor.getInt(0);
                String membershipName = cursor.getString(1);
                String companyName = cursor.getString(2);
                double price = cursor.getDouble(3);  // Changed from String to double
                String date_due = cursor.getString(4);

                memberships.add(new MembershipInfo(m_Id, membershipName, companyName, price, date_due));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return memberships;
    }

    public boolean insertPassword(int userId, String servicename, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("servicename", servicename);
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("user_id", userId);

        long result = db.insert("passwords", null, contentValues);
        return result != -1;
    }

    public void deletePassword(int passwordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("password", "p_id = ?", new String[]{String.valueOf(passwordId)});
        db.close();
    }

    public List<Service> getAllPaswords(int userId) {
        List<Service> passwords = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p_id, servicename, username, password FROM passwords WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int p_Id = cursor.getInt(0);
                String servicename = cursor.getString(1);
                String username = cursor.getString(2);
                String password = cursor.getString(3);


                passwords.add(new Service(p_Id, servicename, username, password));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return passwords;
    }

    public boolean insertNote(int userId, String notetitle, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("notetitle", notetitle);
        contentValues.put("note", note);
        contentValues.put("user_id", userId);

        long result = db.insert("SecureNote ", null, contentValues);
        return result != -1;
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("SecureNote", "n_id = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public List<Note> getAllNotes(int userId) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT n_id, notetitle, note FROM SecureNote WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int n_Id = cursor.getInt(0);
                String notetitle = cursor.getString(1);
                String note = cursor.getString(2);

                notes.add(new Note(n_Id, notetitle, note));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return notes;
    }

    public void deletePersonal_id(int personal_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("personal_id", "id = ?", new String[]{String.valueOf(personal_id)});
        db.close();
    }

    public boolean insertPersonal_id(int id, String fullName, String personalId, String date_of_birth, String place_of_birth,String nationality, String date_of_issue, String date_of_expiry, String issued_by, String card_id, String residence, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("user_id", id);
        contentValues.put("full_name", fullName);
        contentValues.put("personal_id", personalId);
        contentValues.put("date_of_birth", date_of_birth);
        contentValues.put("place_of_birth", place_of_birth);
        contentValues.put("date_of_issue", date_of_issue);
        contentValues.put("date_of_expiry", date_of_expiry);
        contentValues.put("nationality", nationality);
        contentValues.put("issued_by", issued_by);
        contentValues.put("card_id", card_id);
        contentValues.put("residence", residence);
        contentValues.put("gender", gender);


        long result = db.insert("personal_id", null, contentValues);


        return result != -1;
    }

    public List<PersonalID> getAllPersonal_id(int userId) {
        List<PersonalID> personalIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, full_name, personal_id, date_of_birth, nationality, gender, date_of_expiry,date_of_issue,issued_by,card_id,residence FROM personal_id WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String fullName = cursor.getString(1);
                String personalId = cursor.getString(2);
                String dateOfBirth = cursor.getString(3);
                String nationality = cursor.getString(4);
                String gender = cursor.getString(5);
                String dateOfExpiry = cursor.getString(6);
                String dateOfIssue = cursor.getString(7);
                String issuedBy = cursor.getString(8);
                String cardId = cursor.getString(9);
                String residence = cursor.getString(10);


                personalIds.add(new PersonalID(id, fullName, personalId, dateOfBirth, nationality, gender, dateOfExpiry, dateOfIssue, issuedBy, cardId, residence));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return personalIds;
    }
    public void deleteRandom(int personal_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Random", "r_id = ?", new String[]{String.valueOf(personal_id)});
        db.close();
    }
    public boolean insertRandom(int userId, String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("user_id", userId);

        long result = db.insert("Random ", null, contentValues);
        return result != -1;
    }
    public List<Random> getAllRandom(int userId) {
        List<Random> random = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r_id, title, description FROM Random WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int r_Id = cursor.getInt(0);
                String title = cursor.getString(1);
                String description = cursor.getString(2);

                random.add(new Random(r_Id, title, description));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return random;
    }
}

