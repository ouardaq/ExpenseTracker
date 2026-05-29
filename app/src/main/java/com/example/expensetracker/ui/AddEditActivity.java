package com.example.expensetracker.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import com.example.expensetracker.R;
import com.example.expensetracker.db.ExpenseDao;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.util.Categories;
import com.example.expensetracker.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "expense_id";

    private EditText etAmount, etNote;
    private EditText tvDate;
    private AutoCompleteTextView spCategory;
    private TextInputLayout tilAmount;
    private long expenseId = -1;
    private final Calendar pickedDate = Calendar.getInstance();
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private ExpenseDao dao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        dao = new ExpenseDao(this);
        session = new SessionManager(this);

        tilAmount = findViewById(R.id.til_amount);
        tilAmount.setPrefixText(session.getCurrency());
        etAmount = findViewById(R.id.et_amount);
        etNote = findViewById(R.id.et_note);
        spCategory = findViewById(R.id.sp_category);
        tvDate = findViewById(R.id.tv_date);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                R.layout.item_dropdown, Categories.ALL);
        spCategory.setAdapter(catAdapter);
        spCategory.setText(Categories.ALL[0], false);

        tvDate.setText(fmt.format(pickedDate.getTime()));
        tvDate.setOnClickListener(v -> showDatePicker());

        expenseId = getIntent().getLongExtra(EXTRA_ID, -1);
        if (expenseId != -1) {
            loadExisting();
            setTitle(R.string.edit_expense);
        } else {
            setTitle(R.string.add_expense);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.btn_save).setOnClickListener(v -> save());
    }

    private void loadExisting() {
        Expense e = dao.getById(expenseId);
        if (e == null) {
            Toast.makeText(this, R.string.not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        etAmount.setText(String.valueOf(e.getAmount()));
        etNote.setText(e.getNote());
        for (int i = 0; i < Categories.ALL.length; i++) {
            if (Categories.ALL[i].equals(e.getCategory())) {
                spCategory.setText(Categories.ALL[i], false);
                break;
            }
        }
        try {
            pickedDate.setTime(fmt.parse(e.getDate()));
        } catch (Exception ignored) {}
        tvDate.setText(e.getDate());
    }

    private void showDatePicker() {
        hideKeyboard();
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    pickedDate.set(year, month, day);
                    tvDate.setText(fmt.format(pickedDate.getTime()));
                },
                pickedDate.get(Calendar.YEAR),
                pickedDate.get(Calendar.MONTH),
                pickedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void save() {
        hideKeyboard();
        tilAmount.setError(null);

        String amountStr = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            tilAmount.setError(getString(R.string.err_amount));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            tilAmount.setError(getString(R.string.err_amount));
            return;
        }
        if (amount <= 0) {
            tilAmount.setError(getString(R.string.err_amount_positive));
            return;
        }

        Expense exp = new Expense();
        exp.setUserId(session.getUserId());
        exp.setAmount(amount);
        exp.setCategory(spCategory.getText().toString());
        exp.setDate(tvDate.getText().toString());
        exp.setNote(etNote.getText().toString().trim());

        if (expenseId == -1) {
            long id = dao.insert(exp);
            if (id > 0) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.err_save, Toast.LENGTH_SHORT).show();
            }
        } else {
            exp.setId(expenseId);
            int n = dao.update(exp);
            if (n > 0) {
                Toast.makeText(this, R.string.updated, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.err_save, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}