package com.example.expensetracker.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.R;
import com.example.expensetracker.db.ExpenseDao;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.ui.AddEditActivity;
import com.example.expensetracker.ui.MainActivity;
import com.example.expensetracker.ui.adapters.ExpenseListAdapter;
import com.example.expensetracker.util.Categories;
import com.example.expensetracker.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment implements MainActivity.Refreshable {

    private EditText etKeyword;
    private AutoCompleteTextView spCategory;
    private TextView tvFrom, tvTo, tvCount;
    private ListView listView;
    private ExpenseListAdapter adapter;

    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private String dateFrom = null, dateTo = null;

    private ExpenseDao dao;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = new ExpenseDao(requireContext());
        session = new SessionManager(requireContext());

        etKeyword = view.findViewById(R.id.et_keyword);
        spCategory = view.findViewById(R.id.sp_category);
        tvFrom = view.findViewById(R.id.tv_from);
        tvTo = view.findViewById(R.id.tv_to);
        tvCount = view.findViewById(R.id.tv_count);
        listView = view.findViewById(R.id.list);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_dropdown,
                Categories.WITH_ALL_FILTER);
        spCategory.setAdapter(catAdapter);
        spCategory.setText(Categories.WITH_ALL_FILTER[0], false);

        tvFrom.setOnClickListener(v -> pickDate(true));
        tvTo.setOnClickListener(v -> pickDate(false));

        adapter = new ExpenseListAdapter(requireContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, v, pos, id) -> {
            Expense e = adapter.getItem(pos);
            if (e != null) {
                Intent i = new Intent(getContext(), AddEditActivity.class);
                i.putExtra(AddEditActivity.EXTRA_ID, e.getId());
                startActivity(i);
            }
        });

        view.findViewById(R.id.btn_search).setOnClickListener(v -> doSearch());
        view.findViewById(R.id.btn_reset).setOnClickListener(v -> reset());
    }

    @Override
    public void onResume() {
        super.onResume();
        doSearch();
    }

    @Override
    public void refresh() { doSearch(); }

    private void hideKeyboard() {
        View v = requireActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void pickDate(boolean from) {
        hideKeyboard();
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (view, y, m, d) -> {
                    c.set(y, m, d);
                    String s = fmt.format(c.getTime());
                    if (from) { dateFrom = s; tvFrom.setText(s); }
                    else { dateTo = s; tvTo.setText(s); }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void doSearch() {
        hideKeyboard();
        String kw = etKeyword.getText().toString();
        String cat = spCategory.getText().toString();
        List<Expense> results = dao.search(session.getUserId(), cat, kw, dateFrom, dateTo);
        adapter.setData(results);
        tvCount.setText(getString(R.string.results_count, results.size()));
    }

    private void reset() {
        etKeyword.setText("");
        spCategory.setText(Categories.WITH_ALL_FILTER[0], false);
        dateFrom = null; dateTo = null;
        tvFrom.setText(R.string.from_date);
        tvTo.setText(R.string.to_date);
        doSearch();
    }
}