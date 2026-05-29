package com.example.expensetracker.ui;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expensetracker.R;
import com.example.expensetracker.db.ExpenseDao;
import com.example.expensetracker.ui.fragments.RecordsFragment;
import com.example.expensetracker.ui.fragments.SearchFragment;
import com.example.expensetracker.ui.fragments.StatsFragment;
import com.example.expensetracker.util.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private ViewPager2 pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name)
                    + " - " + session.getUsername());
        }

        pager = findViewById(R.id.pager);
        TabLayout tabs = findViewById(R.id.tabs);

        pager.setAdapter(new MainPagerAdapter(this));
        new TabLayoutMediator(tabs, pager, (tab, pos) -> {
            switch (pos) {
                case 0: tab.setText(R.string.tab_records); break;
                case 1: tab.setText(R.string.tab_search); break;
                case 2: tab.setText(R.string.tab_stats); break;
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear_all) {
            confirmClearAll();
            return true;
        } else if (id == R.id.action_currency) {
            showCurrencyPicker();
            return true;
        } else if (id == R.id.action_about) {
            showAbout();
            return true;
        } else if (id == R.id.action_logout) {
            confirmLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmClearAll() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.clear_all_title)
                .setMessage(R.string.clear_all_msg)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    int n = new ExpenseDao(this).deleteAllForUser(session.getUserId());
                    Toast.makeText(this, getString(R.string.deleted_n, n),
                            Toast.LENGTH_SHORT).show();
                    refreshCurrentTab();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showAbout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_msg)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_msg)
                .setPositiveButton(R.string.logout, (d, w) -> {
                    session.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showCurrencyPicker() {
        String[] options = {"$ USD", "¥ CNY"};
        String[] symbols = {SessionManager.CURRENCY_USD, SessionManager.CURRENCY_CNY};
        int current = session.getCurrency().equals(SessionManager.CURRENCY_CNY) ? 1 : 0;

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.currency_title)
                .setSingleChoiceItems(options, current, (d, which) -> {
                    session.setCurrency(symbols[which]);
                    d.dismiss();
                    refreshAllTabs();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void refreshAllTabs() {
        for (int i = 0; i < 3; i++) {
            Fragment f = getSupportFragmentManager().findFragmentByTag("f" + i);
            if (f instanceof Refreshable) {
                ((Refreshable) f).refresh();
            }
        }
    }

    /** Force the currently visible fragment to reload its data. */
    public void refreshCurrentTab() {
        Fragment f = getSupportFragmentManager()
                .findFragmentByTag("f" + pager.getCurrentItem());
        if (f instanceof Refreshable) {
            ((Refreshable) f).refresh();
        }
    }

    /** Fragments implement this so the host activity can ask them to reload. */
    public interface Refreshable {
        void refresh();
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {
        MainPagerAdapter(FragmentActivity activity) { super(activity); }

        @NonNull @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new RecordsFragment();
                case 1: return new SearchFragment();
                case 2: return new StatsFragment();
            }
            return new RecordsFragment();
        }

        @Override public int getItemCount() { return 3; }
    }
}
