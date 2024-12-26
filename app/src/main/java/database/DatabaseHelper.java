package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.app2.GLOBAL;

import java.util.ArrayList;
import java.util.List;

import SecureNotes.Note;

import memberships.MembershipInfo;
import passwords.Service;
import creditcard.CreditCard;
import personal_id.PersonalID;
import random.Random;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data15.db";
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
        String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + "users" + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "email" + " TEXT UNIQUE NOT NULL,"
                + "password" + " TEXT NOT NULL,"
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
                + "password TEXT, "
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

        String hashedPassword = GLOBAL.hashPassword(password);
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


        String hashedPassword = GLOBAL.hashPassword(password);

        Log.d("DB_DEBUG", "Checking user: " + username + ", Hashed Password: " + hashedPassword);

        if (hashedPassword == null) {
            Log.e("DB_ERROR", "Gabim gjatë krijimit të hash-it të fjalëkalimit.");
            return false;
        }

        String query = "SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            String dbHash = cursor.getString(0); // Merrni hash-in nga baza e të dhënave
            cursor.close();


            if (hashedPassword.equals(dbHash)) {
                Log.d("DB_DEBUG", "Përdoruesi ekziston dhe hash-i përputhet.");
                return true;
            } else {
                Log.e("DB_DEBUG", "Hash-i nuk përputhet. Hash në DB: " + dbHash);
            }
        } else {
            Log.e("DB_DEBUG", "Përdoruesi nuk ekziston në bazën e të dhënave.");
        }

        if (cursor != null) {
            cursor.close();
        }

        return false;
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

        try {

            Log.d("DB_DEBUG", "Futja e të dhënave të kartës së kreditit për userId: " + userId);
            Log.d("DB_DEBUG", "Emri: " + emri + ", CreditCardNumber: " + creditCardNumber + ", ExpirationDate: " + expirationDate + ", CVV: " + cvv);


            String encryptedEmri = CryptoUtils.encrypt(emri);
            String encryptedCreditCardNumber = CryptoUtils.encrypt(creditCardNumber);
            String encryptedExpirationDate = CryptoUtils.encrypt(expirationDate);
            String encryptedCvv = CryptoUtils.encrypt(cvv);


            Log.d("DB_DEBUG", "Emri i enkriptuar: " + encryptedEmri);
            Log.d("DB_DEBUG", "Numri i kartës së kreditit i enkriptuar: " + encryptedCreditCardNumber);
            Log.d("DB_DEBUG", "Data e skadimit e enkriptuar: " + encryptedExpirationDate);
            Log.d("DB_DEBUG", "CVV i enkriptuar: " + encryptedCvv);


            contentValues.put("emri", encryptedEmri);
            contentValues.put("creditcard_number", encryptedCreditCardNumber);
            contentValues.put("expiration_date", encryptedExpirationDate);
            contentValues.put("ccv", encryptedCvv);
            contentValues.put("user_id", userId);

            // Futja në bazën e të dhënave
            long result = db.insert("creditcard", null, contentValues);

            // Log rezultati
            if (result != -1) {
                Log.d("DB_DEBUG", "Kartë krediti u fut me sukses për userId: " + userId);
            } else {
                Log.e("DB_DEBUG", "Dështoi futja e kartës së kreditit për userId: " + userId);
            }

            return result != -1;
        } catch (Exception e) {
            // Log për gabime
            Log.e("DB_ERROR", "Gabim gjatë futjes së kartës së kreditit: ", e);
            e.printStackTrace();
            return false;
        }
    }



    public List<CreditCard> getAllCreditCards(int userId) {
        List<CreditCard> creditCards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT credit_id, emri, creditcard_number, expiration_date, ccv FROM creditcard WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                try {
                    int creditId = cursor.getInt(0);

                    String cardholderName = CryptoUtils.decrypt(cursor.getString(1));
                    String cardNumber = CryptoUtils.decrypt(cursor.getString(2));
                    String expirationDate = CryptoUtils.decrypt(cursor.getString(3));
                    String cvv = CryptoUtils.decrypt(cursor.getString(4));

                    creditCards.add(new CreditCard(creditId, cardholderName, cardNumber, expirationDate, cvv));
                } catch (Exception e) {
                    e.printStackTrace();

                }
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

        try {
            String encryptedMembershipName = CryptoUtils.encrypt(membershipName);
            String encryptedCompanyName = CryptoUtils.encrypt(companyName);
            String encryptedPrice = CryptoUtils.encrypt(price);
            String encryptedDateDue = CryptoUtils.encrypt(date_due);

            // Log the encrypted data
            Log.d("ENCRYPTED_DATA", "Encrypted Membership Name: " + encryptedMembershipName);
            Log.d("ENCRYPTED_DATA", "Encrypted Company Name: " + encryptedCompanyName);
            Log.d("ENCRYPTED_DATA", "Encrypted Price: " + encryptedPrice);
            Log.d("ENCRYPTED_DATA", "Encrypted Date Due: " + encryptedDateDue);

            contentValues.put("membershipName", encryptedMembershipName);
            contentValues.put("companyName", encryptedCompanyName);
            contentValues.put("price", encryptedPrice);
            contentValues.put("date_due", encryptedDateDue);
            contentValues.put("user_id", userId);

            long result = db.insert("membership_new", null, contentValues);
            return result != -1;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error encrypting data for membership insertion", e);
            return false;
        } finally {
            db.close();
        }
    }



    public void deleteMembership(int membershipId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("membership_new", "m_id = ?", new String[]{String.valueOf(membershipId)});
        db.close();
    }
    public void updateMembershipPaymentDate(int membershipId, String newPaymentDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String encryptedPaymentDate = CryptoUtils.encrypt(newPaymentDate);

            ContentValues values = new ContentValues();
            values.put("date_due", encryptedPaymentDate);

            int rowsAffected = db.update(
                    "membership_new",
                    values,
                    "m_id = ?",
                    new String[]{String.valueOf(membershipId)}
            );

            if (rowsAffected > 0) {
                Log.d("DatabaseHelper", "Updated payment date for membership ID: " + membershipId + " to " + newPaymentDate);
            } else {
                Log.d("DatabaseHelper", "No rows updated for membership ID: " + membershipId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating payment date: ", e);
        } finally {
            db.close();
        }
    }


    public List<MembershipInfo> getAllMemberships(int userId) {
        List<MembershipInfo> memberships = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m_id, membershipName, companyName, price, date_due FROM membership_new WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                try {
                    int m_Id = cursor.getInt(0);


                    String membershipName = CryptoUtils.decrypt(cursor.getString(1));
                    String companyName = CryptoUtils.decrypt(cursor.getString(2));
                    String price = CryptoUtils.decrypt(cursor.getString(3));
                    String dateDue = CryptoUtils.decrypt(cursor.getString(4));

                    memberships.add(new MembershipInfo(m_Id, membershipName, companyName, Double.parseDouble(price), dateDue));
                } catch (Exception e) {
                    e.printStackTrace();

                }
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

        try {
            String encryptedServiceName = CryptoUtils.encrypt(servicename);
            String encryptedUsername = CryptoUtils.encrypt(username);
            String encryptedPassword = CryptoUtils.encrypt(password);


            Log.d("ENCRYPTED_DATA", "Encrypted Service Name: " + encryptedServiceName);
            Log.d("ENCRYPTED_DATA", "Encrypted Username: " + encryptedUsername);
            Log.d("ENCRYPTED_DATA", "Encrypted Password: " + encryptedPassword);

            contentValues.put("servicename", encryptedServiceName);
            contentValues.put("username", encryptedUsername);
            contentValues.put("password", encryptedPassword);
            contentValues.put("user_id", userId);

            long result = db.insert("passwords", null, contentValues);
            return result != -1;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error encrypting data for password insertion", e);
            return false;
        } finally {
            db.close();
        }
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
                try {
                    int p_Id = cursor.getInt(0);


                    String servicename = CryptoUtils.decrypt(cursor.getString(1));
                    String username = CryptoUtils.decrypt(cursor.getString(2));
                    String password = CryptoUtils.decrypt(cursor.getString(3));

                    passwords.add(new Service(p_Id, servicename, username, password));
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return passwords;
    }



    public void deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("SecureNote", "n_id = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public boolean insertNote(int userId, String notetitle, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {
            String encryptedNoteTitle = CryptoUtils.encrypt(notetitle);
            String encryptedNote = CryptoUtils.encrypt(note);


            Log.d("ENCRYPTED_DATA", "Encrypted Note Title: " + encryptedNoteTitle);
            Log.d("ENCRYPTED_DATA", "Encrypted Note: " + encryptedNote);

            contentValues.put("notetitle", encryptedNoteTitle);
            contentValues.put("note", encryptedNote);
            contentValues.put("user_id", userId);

            long result = db.insert("SecureNote", null, contentValues);
            return result != -1;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error encrypting data for note insertion", e);
            return false;
        } finally {
            db.close();
        }
    }


    public List<Note> getAllNotes(int userId) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT n_id, notetitle, note FROM SecureNote WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                try {
                    int n_Id = cursor.getInt(0);


                    String notetitle = CryptoUtils.decrypt(cursor.getString(1));
                    String note = CryptoUtils.decrypt(cursor.getString(2));

                    notes.add(new Note(n_Id, notetitle, note));
                } catch (Exception e) {
                    e.printStackTrace();

                }
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

    public boolean insertPersonal_id(int id, String fullName, String personalId, String date_of_birth, String place_of_birth, String nationality, String date_of_issue, String date_of_expiry, String issued_by, String card_id, String residence, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {

            String encryptedFullName = CryptoUtils.encrypt(fullName);
            String encryptedPersonalId = CryptoUtils.encrypt(personalId);
            String encryptedDateOfBirth = CryptoUtils.encrypt(date_of_birth);
            String encryptedPlaceOfBirth = CryptoUtils.encrypt(place_of_birth);
            String encryptedNationality = CryptoUtils.encrypt(nationality);
            String encryptedDateOfIssue = CryptoUtils.encrypt(date_of_issue);
            String encryptedDateOfExpiry = CryptoUtils.encrypt(date_of_expiry);
            String encryptedIssuedBy = CryptoUtils.encrypt(issued_by);
            String encryptedCardId = CryptoUtils.encrypt(card_id);
            String encryptedResidence = CryptoUtils.encrypt(residence);
            String encryptedGender = CryptoUtils.encrypt(gender);


            Log.d("ENCRYPTED_DATA", "Encrypted Full Name: " + encryptedFullName);
            Log.d("ENCRYPTED_DATA", "Encrypted Personal ID: " + encryptedPersonalId);
            Log.d("ENCRYPTED_DATA", "Encrypted Date of Birth: " + encryptedDateOfBirth);
            Log.d("ENCRYPTED_DATA", "Encrypted Place of Birth: " + encryptedPlaceOfBirth);
            Log.d("ENCRYPTED_DATA", "Encrypted Nationality: " + encryptedNationality);
            Log.d("ENCRYPTED_DATA", "Encrypted Date of Issue: " + encryptedDateOfIssue);
            Log.d("ENCRYPTED_DATA", "Encrypted Date of Expiry: " + encryptedDateOfExpiry);
            Log.d("ENCRYPTED_DATA", "Encrypted Issued By: " + encryptedIssuedBy);
            Log.d("ENCRYPTED_DATA", "Encrypted Card ID: " + encryptedCardId);
            Log.d("ENCRYPTED_DATA", "Encrypted Residence: " + encryptedResidence);
            Log.d("ENCRYPTED_DATA", "Encrypted Gender: " + encryptedGender);

            contentValues.put("user_id", id);
            contentValues.put("full_name", encryptedFullName);
            contentValues.put("personal_id", encryptedPersonalId);
            contentValues.put("date_of_birth", encryptedDateOfBirth);
            contentValues.put("place_of_birth", encryptedPlaceOfBirth);
            contentValues.put("date_of_issue", encryptedDateOfIssue);
            contentValues.put("date_of_expiry", encryptedDateOfExpiry);
            contentValues.put("nationality", encryptedNationality);
            contentValues.put("issued_by", encryptedIssuedBy);
            contentValues.put("card_id", encryptedCardId);
            contentValues.put("residence", encryptedResidence);
            contentValues.put("gender", encryptedGender);

            long result = db.insert("personal_id", null, contentValues);
            return result != -1;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error encrypting data for personal ID insertion", e);
            return false;
        } finally {
            db.close();
        }
    }


    public List<PersonalID> getAllPersonal_id(int userId) {
        List<PersonalID> personalIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, full_name, personal_id, date_of_birth, nationality, gender, date_of_expiry, date_of_issue, issued_by, card_id, residence FROM personal_id WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(0);


                    String fullName = CryptoUtils.decrypt(cursor.getString(1));
                    String personalId = CryptoUtils.decrypt(cursor.getString(2));
                    String dateOfBirth = CryptoUtils.decrypt(cursor.getString(3));
                    String nationality = CryptoUtils.decrypt(cursor.getString(4));
                    String gender = CryptoUtils.decrypt(cursor.getString(5));
                    String dateOfExpiry = CryptoUtils.decrypt(cursor.getString(6));
                    String dateOfIssue = CryptoUtils.decrypt(cursor.getString(7));
                    String issuedBy = CryptoUtils.decrypt(cursor.getString(8));
                    String cardId = CryptoUtils.decrypt(cursor.getString(9));
                    String residence = CryptoUtils.decrypt(cursor.getString(10));

                    personalIds.add(new PersonalID(id, fullName, personalId, dateOfBirth, nationality, gender, dateOfExpiry, dateOfIssue, issuedBy, cardId, residence));
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
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

        try {

            String encryptedTitle = CryptoUtils.encrypt(title);
            String encryptedDescription = CryptoUtils.encrypt(description);


            Log.d("ENCRYPTED_DATA", "Encrypted Title: " + encryptedTitle);
            Log.d("ENCRYPTED_DATA", "Encrypted Description: " + encryptedDescription);

            contentValues.put("title", encryptedTitle);
            contentValues.put("description", encryptedDescription);
            contentValues.put("user_id", userId);

            long result = db.insert("Random", null, contentValues);
            return result != -1;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error encrypting data for random insertion", e);
            return false;
        } finally {
            db.close();
        }
    }


    public List<Random> getAllRandom(int userId) {
        List<Random> randomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r_id, title, description FROM Random WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                try {
                    int r_Id = cursor.getInt(0);

                    // Decrypt sensitive fields
                    String title = CryptoUtils.decrypt(cursor.getString(1));
                    String description = CryptoUtils.decrypt(cursor.getString(2));

                    randomList.add(new Random(r_Id, title, description));
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return randomList;
    }

}

