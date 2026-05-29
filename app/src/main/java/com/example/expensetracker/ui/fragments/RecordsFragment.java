package com.example.expensetracker.ui.fragments;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.expensetracker.R;
import com.example.expensetracker.db.ExpenseDao;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.ui.AddEditActivity;
import com.example.expensetracker.ui.MainActivity;
import com.example.expensetracker.ui.adapters.ExpenseAdapter;
import com.example.expensetracker.util.SessionManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class RecordsFragment extends Fragment implements MainActivity.Refreshable {

    private SwipeRefreshLayout swipe;
    private RecyclerView recycler;
    private View emptyContainer;
    private ExpenseAdapter adapter;
    private ExpenseDao dao;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_records, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = new ExpenseDao(requireContext());
        session = new SessionManager(requireContext());

        swipe = view.findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.primary, R.color.accent);
        swipe.setProgressBackgroundColorSchemeResource(R.color.bg_surface);
        recycler = view.findViewById(R.id.recycler);
        emptyContainer = view.findViewById(R.id.empty_container);
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fab_add);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(new ExpenseAdapter.Listener() {
            @Override public void onClick(Expense e) { openEdit(e.getId()); }
            @Override public void onLongClick(Expense e) { confirmDelete(e); }
        });
        recycler.setAdapter(adapter);

        swipe.setOnRefreshListener(this::refresh);
        fab.setOnClickListener(v -> openEdit(-1));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {
        List<Expense> list = dao.getAllForUser(session.getUserId());
        adapter.setData(list);
        emptyContainer.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        swipe.setRefreshing(false);
    }

    private void openEdit(long id) {
        Intent i = new Intent(getContext(), AddEditActivity.class);
        if (id != -1) i.putExtra(AddEditActivity.EXTRA_ID, id);
        startActivity(i);
    }

    private void confirmDelete(Expense e) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_msg, e.getCategory(), e.getAmount()))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    int n = dao.delete(e.getId());
                    if (n > 0) {
                        Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
