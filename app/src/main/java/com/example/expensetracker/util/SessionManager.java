package com.example.expensetracker.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS = "session_prefs";
    private static final String K_USER_ID = "user_id";
    private static final String K_USERNAME = "username";
    private static final String K_CURRENCY = "currency";

    public static final String CURRENCY_USD = "$";
    public static final String CURRENCY_CNY = "¥";

    private final SharedPreferences prefs;

    public SessionManager(Context ctx) {
        this.prefs = ctx.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveLogin(long userId, String username) {
        prefs.edit()
                .putLong(K_USER_ID, userId)
                .putString(K_USERNAME, username)
                .apply();
    }

    public long getUserId() { return prefs.getLong(K_USER_ID, -1); }

    public String getUsername() { return prefs.getString(K_USERNAME, ""); }

    public boolean isLoggedIn() { return getUserId() != -1; }

    public String getCurrency() {
        return prefs.getString(K_CURRENCY, CURRENCY_USD);
    }

    public void setCurrency(String symbol) {
        prefs.edit().putString(K_CURRENCY, symbol).apply();
    }

    public void logout() { prefs.edit().clear().apply(); }
}
