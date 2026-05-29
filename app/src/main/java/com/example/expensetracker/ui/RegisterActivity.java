package com.example.expensetracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import com.example.expensetracker.R;
import com.example.expensetracker.db.UserDao;

public class RegisterActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "registered_username";
    public static final String EXTRA_PASSWORD = "registered_password";

    private EditText etUsername, etPassword, etPasswordConfirm;
    private TextInputLayout tilUsername, tilPassword, tilPasswordConfirm;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDao = new UserDao(this);

        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilPasswordConfirm = findViewById(R.id.til_password_confirm);
        etUsername = findViewById(R.id.et_reg_username);
        etPassword = findViewById(R.id.et_reg_password);
        etPasswordConfirm = findViewById(R.id.et_reg_password_confirm);

        findViewById(R.id.btn_register).setOnClickListener(v -> doRegister());
        findViewById(R.id.btn_back_login).setOnClickListener(v -> finish());
    }

    private void doRegister() {
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilPasswordConfirm.setError(null);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError(getString(R.string.err_fill_all));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_fill_all));
            return;
        }
        if (password.length() < 4) {
            tilPassword.setError(getString(R.string.err_pw_short));
            return;
        }
        if (!password.equals(passwordConfirm)) {
            tilPasswordConfirm.setError(getString(R.string.err_pw_mismatch));
            return;
        }
        if (userDao.usernameExists(username)) {
            tilUsername.setError(getString(R.string.err_user_exists));
            return;
        }

        long id = userDao.register(username, password);
        if (id > 0) {
            // Pass credentials back to LoginActivity so fields auto-fill
            Intent result = new Intent();
            result.putExtra(EXTRA_USERNAME, username);
            result.putExtra(EXTRA_PASSWORD, password);
            setResult(RESULT_OK, result);
            finish();
        } else {
            tilUsername.setError(getString(R.string.err_register_failed));
        }
    }
}