package com.example.savemoney.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.savemoney.Model.AccountModel;
import com.example.savemoney.Model.SpendingModel;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_ACCOUNT_NAME = "account";
    public static final String COLUMN_ACCOUNT_ID = "id";
    public static final String COLUMN_AMOUNT_TITLE = "title";
    public static final String COLUMN_ACCOUNT_AMOUNT = "amount";
    public static final String COLUMN_ACCOUNT_TYPE = "type";
    public static final String COLUMN_ACCOUNT_NOTE = "note";
    private static final String DATABASE_NAME = "saveMoney.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SPENDING_NAME = "spending";
    private static final String COLUMN_SPENDING_ID = "id";
    private static final String COLUMN_SPENDING_BALANCE = "balance";
    private static final String COLUMN_SPENDING_TYPE = "type";
    private static final String COLUMN_SPENDING_CATEGORY = "category";
    private static final String COLUMN_SPENDING_DESCRIPTION = "description";
    private static final String COLUMN_SPENDING_DATE = "date";
    private static final String COLUMN_SPENDING_TIME = "time";
    private static final String COLUMN_SPENDING_ACCOUNT_ID = "account_id";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPENDING_NAME);
    }

    public void createDatabase(SQLiteDatabase db) {

        String queryAccount = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNT_NAME +
                " ( " + COLUMN_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT_TITLE + " TEXT, " +
                COLUMN_ACCOUNT_AMOUNT + " INT, " +
                COLUMN_ACCOUNT_TYPE + " INT, " +
                COLUMN_ACCOUNT_NOTE + " TEXT ) ;";

        String querySpending = "CREATE TABLE IF NOT EXISTS " + TABLE_SPENDING_NAME +
                " (" + COLUMN_SPENDING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SPENDING_BALANCE + " INT, " +
                COLUMN_SPENDING_CATEGORY + " INT, " +
                COLUMN_SPENDING_DESCRIPTION + " TEXT, " +
                COLUMN_SPENDING_DATE + " TEXT, " +
                COLUMN_SPENDING_TIME + " TEXT, " +
                COLUMN_SPENDING_TYPE + " TEXT, " +
                COLUMN_SPENDING_ACCOUNT_ID + " INT);";


        db.execSQL(queryAccount);
        db.execSQL(querySpending);
        
    }

    public void deleteAllDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPENDING_NAME);
        createDatabase(db);

    }

    public void addAccount(AccountModel accountModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AMOUNT_TITLE, accountModel.getTitle());
        cv.put(COLUMN_ACCOUNT_AMOUNT, accountModel.getAmount());
        cv.put(COLUMN_ACCOUNT_TYPE, accountModel.getType());
        cv.put(COLUMN_ACCOUNT_NOTE, accountModel.getNote());

        long insert = db.insert(TABLE_ACCOUNT_NAME, null, cv);
        db.close();
    }

    public AccountModel getAccountById(int id) {
        String query = "SELECT * FROM " + TABLE_ACCOUNT_NAME + " WHERE " + COLUMN_ACCOUNT_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int accountId = cursor.getInt(0);
        String title = cursor.getString(1);
        int amount = cursor.getInt(2);
        int type = cursor.getInt(3);
        String note = cursor.getString(4);
        cursor.close();
        db.close();
        return new AccountModel(accountId, title, note, amount, type);
    }

    public ArrayList<AccountModel> getAllAccount() {
        ArrayList<AccountModel> accountModels = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_ACCOUNT_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int accountId = cursor.getInt(0);
                String title = cursor.getString(1);
                int amount = cursor.getInt(2);
                int type = cursor.getInt(3);
                String note = cursor.getString(4);
                AccountModel accountModel = new AccountModel(accountId, title, note, amount, type);
                accountModels.add(accountModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountModels;
    }

    public List<AccountModel> getAccountsToSync() {
        List<AccountModel> accountModels = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_ACCOUNT_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int accountId = cursor.getInt(0);
                String title = cursor.getString(1);
                int amount = cursor.getInt(2);
                int type = cursor.getInt(3);
                String note = cursor.getString(4);
                ArrayList<SpendingModel> spendingModels = getAllSpendingAccountsByAccountId(accountId);

                AccountModel accountModel = new AccountModel(accountId, title, note, amount, type, spendingModels);
                accountModels.add(accountModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountModels;
    }

    public void deleteOne(AccountModel accountModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryDeleteSpending = "DELETE FROM " + TABLE_SPENDING_NAME + " WHERE " + COLUMN_SPENDING_ACCOUNT_ID + " = " + accountModel.getId();
        db.execSQL(queryDeleteSpending);
        String query = "DELETE FROM " + TABLE_ACCOUNT_NAME + " WHERE " + COLUMN_ACCOUNT_ID + " = " + accountModel.getId();
        db.execSQL(query);
        db.close();
    }

    public void updateAccount(AccountModel accountModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AMOUNT_TITLE, accountModel.getTitle());
        cv.put(COLUMN_ACCOUNT_AMOUNT, accountModel.getAmount());
        cv.put(COLUMN_ACCOUNT_TYPE, accountModel.getType());
        cv.put(COLUMN_ACCOUNT_NOTE, accountModel.getNote());

        long tmp = db.update(TABLE_ACCOUNT_NAME, cv, COLUMN_ACCOUNT_ID + "=" + accountModel.getId(), null);
        db.close();
    }

    public boolean addSpendingAccount(SpendingModel spendingModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SPENDING_BALANCE, spendingModel.getBalance());
        cv.put(COLUMN_SPENDING_CATEGORY, spendingModel.getCategory());
        cv.put(COLUMN_SPENDING_DESCRIPTION, spendingModel.getDescription());
        cv.put(COLUMN_SPENDING_DATE, spendingModel.getDate());
        cv.put(COLUMN_SPENDING_TIME, spendingModel.getTime());
        cv.put(COLUMN_SPENDING_TYPE, spendingModel.getType());
        cv.put(COLUMN_SPENDING_ACCOUNT_ID, spendingModel.getAccountId());

        long insert = db.insert(TABLE_SPENDING_NAME, null, cv);
        db.close();
        return insert != -1;
    }

    public ArrayList<SpendingModel> getAllSpendingAccountsByAccountId(int accountId) {
        ArrayList<SpendingModel> spendingModels = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SPENDING_NAME + " WHERE " + COLUMN_SPENDING_ACCOUNT_ID + " = " + accountId;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int spendingId = cursor.getInt(0);
                int balance = cursor.getInt(1);
                int category = cursor.getInt(2);
                String description = cursor.getString(3);
                String date = cursor.getString(4);
                String time = cursor.getString(5);
                String type = cursor.getString(6);

                spendingModels.add(new SpendingModel(spendingId, balance, category, description, date, time, type, accountId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return spendingModels;
    }

    public ArrayList<SpendingModel> getALLSpendAccount(String month) {
        ArrayList<SpendingModel> spendingModels = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SPENDING_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int spendingId = cursor.getInt(0);
                int balance = cursor.getInt(1);
                int category = cursor.getInt(2);
                String description = cursor.getString(3);
                String date = cursor.getString(4);

                String time = cursor.getString(5);
                String type = cursor.getString(6);
                int accountId = cursor.getInt(7);
                String[] formatDate = date.split("/");
                if (formatDate[1].equals(month)) {
                    spendingModels.add(new SpendingModel(spendingId, balance, category, description, date, time, type, accountId));
                }
                Log.d("LOG", formatDate[1]);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return spendingModels;
    }

    public boolean deleteSpending(SpendingModel spendingModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_SPENDING_NAME + " WHERE " + COLUMN_SPENDING_ID + " = " + spendingModel.getId();

        db.execSQL(query);
        db.close();
        return true;
    }

    public long updateSpending(SpendingModel spendingModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SPENDING_BALANCE, spendingModel.getBalance());
        cv.put(COLUMN_SPENDING_CATEGORY, spendingModel.getCategory());
        cv.put(COLUMN_SPENDING_DESCRIPTION, spendingModel.getDescription());
        cv.put(COLUMN_SPENDING_DATE, spendingModel.getDate());
        cv.put(COLUMN_SPENDING_TIME, spendingModel.getTime());
        cv.put(COLUMN_SPENDING_TYPE, spendingModel.getType());
        cv.put(COLUMN_SPENDING_ACCOUNT_ID, spendingModel.getAccountId());

        long tmp = db.update(TABLE_SPENDING_NAME, cv, COLUMN_SPENDING_ID + "=" + spendingModel.getId(), null);
        db.close();
        return tmp;
    }


}
