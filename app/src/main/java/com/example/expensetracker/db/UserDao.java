package com.example.expensetracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expensetracker.model.User;
import com.example.expensetracker.util.PasswordUtil;

public class UserDao {

    private final DBHelper helper;

    public UserDao(Context context) {
        this.helper = DBHelper.getInstance(context);
    }

    /**
     * Registers a new user. Returns the new user ID, or -1 if the username
     * already exists or insertion fails.
     */
    public long register(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty()) return -1;
        if (plainPassword == null || plainPassword.length() < 4) return -1;

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(DBHelper.C_U_USERNAME, username.trim());
        v.put(DBHelper.C_U_PASSWORD, PasswordUtil.hash(plainPassword));
        v.put(DBHelper.C_U_CREATED, System.currentTimeMillis());
        try {
            return db.insertOrThrow(DBHelper.T_USERS, null, v);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Attempts to log in. Returns the User on success, null on failure.
     */
    public User login(String username, String plainPassword) {
        if (username == null || plainPassword == null) return null;

        SQLiteDatabase db = helper.getReadableDatabase();
        String hash = PasswordUtil.hash(plainPassword);
        Cursor c = db.query(DBHelper.T_USERS,
                new String[]{DBHelper.C_U_ID, DBHelper.C_U_USERNAME, DBHelper.C_U_PASSWORD},
                DBHelper.C_U_USERNAME + "=? AND " + DBHelper.C_U_PASSWORD + "=?",
                new String[]{username.trim(), hash},
                null, null, null);

        User u = null;
        if (c.moveToFirst()) {
            u = new User(c.getLong(0), c.getString(1), c.getString(2));
        }
        c.close();
        return u;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_USERS, new String[]{DBHelper.C_U_ID},
                DBHelper.C_U_USERNAME + "=?", new String[]{username.trim()},
                null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }
}
