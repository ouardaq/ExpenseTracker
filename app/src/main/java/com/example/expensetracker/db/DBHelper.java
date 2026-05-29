package com.example.expensetracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "expense_tracker.db";
    private static final int DB_VERSION = 2;

    // Users table
    public static final String T_USERS = "users";
    public static final String C_U_ID = "id";
    public static final String C_U_USERNAME = "username";
    public static final String C_U_PASSWORD = "password";
    public static final String C_U_CREATED = "created_at";

    // Expenses table
    public static final String T_EXPENSES = "expenses";
    public static final String C_E_ID = "id";
    public static final String C_E_USER_ID = "user_id";
    public static final String C_E_AMOUNT = "amount";
    public static final String C_E_CATEGORY = "category";
    public static final String C_E_DATE = "date";
    public static final String C_E_NOTE = "note";
    public static final String C_E_CREATED = "created_at";

    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_USERS + " (" +
                C_U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_U_USERNAME + " TEXT UNIQUE NOT NULL, " +
                C_U_PASSWORD + " TEXT NOT NULL, " +
                C_U_CREATED + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + T_EXPENSES + " (" +
                C_E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_E_USER_ID + " INTEGER NOT NULL, " +
                C_E_AMOUNT + " REAL NOT NULL, " +
                C_E_CATEGORY + " TEXT NOT NULL, " +
                C_E_DATE + " TEXT NOT NULL, " +
                C_E_NOTE + " TEXT, " +
                C_E_CREATED + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + C_E_USER_ID + ") REFERENCES " +
                T_USERS + "(" + C_U_ID + ") ON DELETE CASCADE)");

        db.execSQL("CREATE INDEX idx_exp_user ON " + T_EXPENSES + "(" + C_E_USER_ID + ")");
        db.execSQL("CREATE INDEX idx_exp_date ON " + T_EXPENSES + "(" + C_E_DATE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            migrateCategories(db);
        }
    }

    private void migrateCategories(SQLiteDatabase db) {
        // Map old category names to new ones
        db.execSQL("UPDATE " + T_EXPENSES + " SET " + C_E_CATEGORY + " = 'Groceries' WHERE " + C_E_CATEGORY + " = 'Food'");
        db.execSQL("UPDATE " + T_EXPENSES + " SET " + C_E_CATEGORY + " = 'Transportation' WHERE " + C_E_CATEGORY + " = 'Transport'");
        db.execSQL("UPDATE " + T_EXPENSES + " SET " + C_E_CATEGORY + " = 'Utilities' WHERE " + C_E_CATEGORY + " = 'Bills'");
        db.execSQL("UPDATE " + T_EXPENSES + " SET " + C_E_CATEGORY + " = 'Healthcare' WHERE " + C_E_CATEGORY + " = 'Health'");
        db.execSQL("UPDATE " + T_EXPENSES + " SET " + C_E_CATEGORY + " = 'Travel' WHERE " + C_E_CATEGORY + " = 'Other'");
        // Shopping and Entertainment keep same names
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}