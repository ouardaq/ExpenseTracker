package com.example.expensetracker.ui.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.util.Categories;
import com.example.expensetracker.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.VH> {

    public interface Listener {
        void onClick(Expense e);
        void onLongClick(Expense e);
    }

    private final List<Expense> data = new ArrayList<>();
    private final Listener listener;

    public ExpenseAdapter(Listener l) { this.listener = l; }

    public void setData(List<Expense> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Expense e = data.get(position);
        int idx = Categories.indexOf(e.getCategory());
        int color = ContextCompat.getColor(h.itemView.getContext(), Categories.COLOR_RES[idx]);

        h.tvCategory.setText(e.getCategory());
        h.tvCategoryIcon.setText(Categories.EMOJI[idx]);
        String currency = new SessionManager(h.itemView.getContext()).getCurrency();
        h.tvAmount.setText(String.format(Locale.US, "%s%.2f", currency, e.getAmount()));
        h.tvDate.setText(e.getDate());
        h.tvNote.setText(e.getNote() == null || e.getNote().isEmpty()
                ? h.itemView.getContext().getString(R.string.no_note)
                : e.getNote());

        // Tint indicator bar
        GradientDrawable indicator = (GradientDrawable) h.viewIndicator.getBackground().mutate();
        indicator.setColor(color);

        // Tint category badge
        GradientDrawable badge = (GradientDrawable) h.tvCategory.getBackground().mutate();
        badge.setColor(color);

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(e); });
        h.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(e);
            return true;
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvCategory, tvCategoryIcon, tvAmount, tvDate, tvNote;
        View viewIndicator;
        VH(@NonNull View v) {
            super(v);
            tvCategory = v.findViewById(R.id.tv_category);
            tvCategoryIcon = v.findViewById(R.id.tv_category_icon);
            tvAmount = v.findViewById(R.id.tv_amount);
            tvDate = v.findViewById(R.id.tv_date);
            tvNote = v.findViewById(R.id.tv_note);
            viewIndicator = v.findViewById(R.id.view_indicator);
        }
    }
}