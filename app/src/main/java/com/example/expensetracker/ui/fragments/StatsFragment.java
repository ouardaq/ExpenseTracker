package com.example.expensetracker.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.R;
import com.example.expensetracker.db.ExpenseDao;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.ui.MainActivity;
import com.example.expensetracker.util.Categories;
import com.example.expensetracker.util.SessionManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsFragment extends Fragment implements MainActivity.Refreshable {

    private TextView tvTotal, tvCount;
    private LinearLayout container;
    private PieChart pie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotal = view.findViewById(R.id.tv_total);
        tvCount = view.findViewById(R.id.tv_total_count);
        container = view.findViewById(R.id.container_categories);
        pie = view.findViewById(R.id.pie_chart);

        // Style the chart for dark/glass theme
        pie.setUsePercentValues(true);
        pie.getDescription().setEnabled(false);
        pie.setDrawHoleEnabled(true);
        pie.setHoleColor(Color.TRANSPARENT);
        pie.setHoleRadius(50f);
        pie.setTransparentCircleColor(Color.TRANSPARENT);
        pie.setTransparentCircleRadius(54f);
        pie.setEntryLabelTextSize(11f);
        int secondaryColor = ContextCompat.getColor(requireContext(), R.color.text_secondary);
        pie.setEntryLabelColor(Color.WHITE);
        pie.getLegend().setEnabled(true);
        pie.getLegend().setTextSize(12f);
        pie.getLegend().setTextColor(secondaryColor);
        pie.setNoDataTextColor(secondaryColor);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {
        ExpenseDao dao = new ExpenseDao(requireContext());
        SessionManager session = new SessionManager(requireContext());
        List<Expense> all = dao.getAllForUser(session.getUserId());

        Map<String, Double> byCategory = new HashMap<>();
        double total = 0;
        for (Expense e : all) {
            total += e.getAmount();
            Double cur = byCategory.get(e.getCategory());
            byCategory.put(e.getCategory(), (cur == null ? 0 : cur) + e.getAmount());
        }

        String currency = session.getCurrency();
        tvTotal.setText(String.format(Locale.US, "%s%.2f", currency, total));
        tvCount.setText(getString(R.string.records_count, all.size()));

        updateChart(byCategory, total);
        updateCategoryList(byCategory, total);
    }

    private void updateChart(Map<String, Double> byCategory, double total) {
        if (total <= 0) {
            pie.clear();
            pie.setNoDataText(getString(R.string.no_records));
            pie.invalidate();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < Categories.ALL.length; i++) {
            String cat = Categories.ALL[i];
            Double v = byCategory.get(cat);
            if (v != null && v > 0) {
                entries.add(new PieEntry(v.floatValue(), cat));
                colors.add(ContextCompat.getColor(requireContext(), Categories.COLOR_RES[i]));
            }
        }

        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(colors);
        set.setValueTextSize(12f);
        set.setValueTextColor(Color.WHITE);
        set.setSliceSpace(2f);

        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter(pie));
        pie.setData(data);
        pie.animateY(700, Easing.EaseInOutQuad);
        pie.invalidate();
    }

    private void updateCategoryList(Map<String, Double> byCategory, double total) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        String cur = new SessionManager(requireContext()).getCurrency();
        for (int i = 0; i < Categories.ALL.length; i++) {
            String cat = Categories.ALL[i];
            double amt = byCategory.containsKey(cat) ? byCategory.get(cat) : 0.0;
            double pct = total > 0 ? (amt / total) * 100 : 0;
            View row = inflater.inflate(R.layout.item_stat_row, container, false);

            // Color dot
            int color = ContextCompat.getColor(requireContext(), Categories.COLOR_RES[i]);
            GradientDrawable dot = (GradientDrawable) row.findViewById(R.id.view_dot)
                    .getBackground().mutate();
            dot.setColor(color);

            // Emoji
            ((TextView) row.findViewById(R.id.tv_emoji)).setText(Categories.EMOJI[i]);

            ((TextView) row.findViewById(R.id.tv_cat)).setText(cat);
            ((TextView) row.findViewById(R.id.tv_amt))
                    .setText(String.format(Locale.US, "%s%.2f  (%.1f%%)", cur, amt, pct));
            container.addView(row);
        }
    }
}