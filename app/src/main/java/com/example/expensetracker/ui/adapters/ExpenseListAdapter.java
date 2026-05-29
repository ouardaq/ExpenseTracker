package com.example.expensetracker.ui.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.expensetracker.R;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.util.Categories;
import com.example.expensetracker.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Expense> data = new ArrayList<>();
    private final LayoutInflater inflater;

    public ExpenseListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<Expense> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @Override public int getCount() { return data.size(); }
    @Override public Expense getItem(int position) { return data.get(position); }
    @Override public long getItemId(int position) { return data.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_expense, parent, false);
            h = new VH();
            h.tvCategory = convertView.findViewById(R.id.tv_category);
            h.tvCategoryIcon = convertView.findViewById(R.id.tv_category_icon);
            h.tvAmount = convertView.findViewById(R.id.tv_amount);
            h.tvDate = convertView.findViewById(R.id.tv_date);
            h.tvNote = convertView.findViewById(R.id.tv_note);
            h.viewIndicator = convertView.findViewById(R.id.view_indicator);
            convertView.setTag(h);
        } else {
            h = (VH) convertView.getTag();
        }
        Expense e = data.get(position);
        int idx = Categories.indexOf(e.getCategory());
        int color = ContextCompat.getColor(context, Categories.COLOR_RES[idx]);

        h.tvCategory.setText(e.getCategory());
        h.tvCategoryIcon.setText(Categories.EMOJI[idx]);
        String currency = new SessionManager(context).getCurrency();
        h.tvAmount.setText(String.format(Locale.US, "%s%.2f", currency, e.getAmount()));
        h.tvDate.setText(e.getDate());
        h.tvNote.setText(e.getNote() == null || e.getNote().isEmpty()
                ? context.getString(R.string.no_note) : e.getNote());

        // Tint indicator bar
        GradientDrawable indicator = (GradientDrawable) h.viewIndicator.getBackground().mutate();
        indicator.setColor(color);

        // Tint category badge
        GradientDrawable badge = (GradientDrawable) h.tvCategory.getBackground().mutate();
        badge.setColor(color);

        return convertView;
    }

    private static class VH {
        TextView tvCategory, tvCategoryIcon, tvAmount, tvDate, tvNote;
        View viewIndicator;
    }
}