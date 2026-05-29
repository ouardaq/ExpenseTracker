package com.example.expensetracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expensetracker.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDao {

    private final DBHelper helper;

    public ExpenseDao(Context context) {
        this.helper = DBHelper.getInstance(context);
    }

    public long insert(Expense e) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = toValues(e);
        v.put(DBHelper.C_E_CREATED, System.currentTimeMillis());
        return db.insert(DBHelper.T_EXPENSES, null, v);
    }

    public int update(Expense e) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(DBHelper.T_EXPENSES, toValues(e),
                DBHelper.C_E_ID + "=?",
                new String[]{String.valueOf(e.getId())});
    }

    public int delete(long expenseId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(DBHelper.T_EXPENSES,
                DBHelper.C_E_ID + "=?",
                new String[]{String.valueOf(expenseId)});
    }

    public Expense getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_EXPENSES, null,
                DBHelper.C_E_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        Expense e = null;
        if (c.moveToFirst()) e = fromCursor(c);
        c.close();
        return e;
    }

    /** All expenses for a user, newest date first. */
    public List<Expense> getAllForUser(long userId) {
        return search(userId, null, null, null, null);
    }

    /**
     * Conditional search. Any of category, keyword, dateFrom, dateTo may be null.
     * Uses parameterized queries — no SQL injection.
     */
    public List<Expense> search(long userId, String category, String keyword,
                                String dateFrom, String dateTo) {
        StringBuilder where = new StringBuilder(DBHelper.C_E_USER_ID + "=?");
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));

        if (category != null && !category.isEmpty() && !"All".equalsIgnoreCase(category)) {
            where.append(" AND ").append(DBHelper.C_E_CATEGORY).append("=?");
            args.add(category);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND ").append(DBHelper.C_E_NOTE).append(" LIKE ?");
            args.add("%" + keyword.trim() + "%");
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            where.append(" AND ").append(DBHelper.C_E_DATE).append(">=?");
            args.add(dateFrom);
        }
        if (dateTo != null && !dateTo.isEmpty()) {
            where.append(" AND ").append(DBHelper.C_E_DATE).append("<=?");
            args.add(dateTo);
        }

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_EXPENSES, null,
                where.toString(),
                args.toArray(new String[0]),
                null, null,
                DBHelper.C_E_DATE + " DESC, " + DBHelper.C_E_ID + " DESC");

        List<Expense> list = new ArrayList<>();
        while (c.moveToNext()) list.add(fromCursor(c));
        c.close();
        return list;
    }

    /** Total amount spent by a user (optionally within a date range). */
    public double getTotal(long userId, String dateFrom, String dateTo) {
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(" + DBHelper.C_E_AMOUNT + ") FROM " + DBHelper.T_EXPENSES +
                " WHERE " + DBHelper.C_E_USER_ID + "=?");
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));
        if (dateFrom != null) { sql.append(" AND ").append(DBHelper.C_E_DATE).append(">=?"); args.add(dateFrom); }
        if (dateTo != null) { sql.append(" AND ").append(DBHelper.C_E_DATE).append("<=?"); args.add(dateTo); }

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        double total = 0;
        if (c.moveToFirst() && !c.isNull(0)) total = c.getDouble(0);
        c.close();
        return total;
    }

    public int deleteAllForUser(long userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(DBHelper.T_EXPENSES,
                DBHelper.C_E_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
    }

    // ---- helpers ----

    private ContentValues toValues(Expense e) {
        ContentValues v = new ContentValues();
        v.put(DBHelper.C_E_USER_ID, e.getUserId());
        v.put(DBHelper.C_E_AMOUNT, e.getAmount());
        v.put(DBHelper.C_E_CATEGORY, e.getCategory());
        v.put(DBHelper.C_E_DATE, e.getDate());
        v.put(DBHelper.C_E_NOTE, e.getNote() == null ? "" : e.getNote());
        return v;
    }

    private Expense fromCursor(Cursor c) {
        Expense e = new Expense();
        e.setId(c.getLong(c.getColumnIndexOrThrow(DBHelper.C_E_ID)));
        e.setUserId(c.getLong(c.getColumnIndexOrThrow(DBHelper.C_E_USER_ID)));
        e.setAmount(c.getDouble(c.getColumnIndexOrThrow(DBHelper.C_E_AMOUNT)));
        e.setCategory(c.getString(c.getColumnIndexOrThrow(DBHelper.C_E_CATEGORY)));
        e.setDate(c.getString(c.getColumnIndexOrThrow(DBHelper.C_E_DATE)));
        e.setNote(c.getString(c.getColumnIndexOrThrow(DBHelper.C_E_NOTE)));
        e.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(DBHelper.C_E_CREATED)));
        return e;
    }
}
