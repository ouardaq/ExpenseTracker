package com.example.expensetracker.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import com.example.expensetracker.R;
import com.example.expensetracker.db.UserDao;
import com.example.expensetracker.model.User;
import com.example.expensetracker.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextInputLayout tilUsername, tilPassword;
    private UserDao userDao;
    private SessionManager session;

    private final ActivityResultLauncher<Intent> registerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String username = result.getData().getStringExtra(RegisterActivity.EXTRA_USERNAME);
                    String password = result.getData().getStringExtra(RegisterActivity.EXTRA_PASSWORD);
                    if (username != null) etUsername.setText(username);
                    if (password != null) etPassword.setText(password);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        userDao = new UserDao(this);

        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        findViewById(R.id.btn_login).setOnClickListener(v -> doLogin());
        findViewById(R.id.btn_register).setOnClickListener(v ->
                registerLauncher.launch(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        tilUsername.setError(null);
        tilPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError(getString(R.string.err_fill_all));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_fill_all));
            return;
        }

        @SuppressWarnings("deprecation")
        final ProgressDialog pd = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Dialog);
        pd.setMessage(getString(R.string.logging_in));
        pd.setCancelable(false);
        pd.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            User u = userDao.login(username, password);
            pd.dismiss();
            if (u != null) {
                session.saveLogin(u.getId(), u.getUsername());
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                tilPassword.setError(getString(R.string.err_login_failed));
            }
        }, 600);
    }
}