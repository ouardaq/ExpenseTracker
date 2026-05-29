package com.example.expensetracker.util;

import com.example.expensetracker.R;

public class Categories {

    public static final String[] ALL = {
            "Housing", "Utilities", "Transportation", "Groceries", "Dining Out",
            "Healthcare", "Entertainment", "Personal Care", "Shopping", "Savings", "Travel"
    };

    public static final String[] WITH_ALL_FILTER = {
            "All", "Housing", "Utilities", "Transportation", "Groceries", "Dining Out",
            "Healthcare", "Entertainment", "Personal Care", "Shopping", "Savings", "Travel"
    };

    public static final String[] EMOJI = {
            "\uD83C\uDFE0",  // Housing: house
            "\uD83D\uDCA1",  // Utilities: light bulb
            "\uD83D\uDE97",  // Transportation: car
            "\uD83D\uDED2",  // Groceries: cart
            "\uD83C\uDF7D\uFE0F",  // Dining Out: fork and knife with plate
            "\uD83C\uDFE5",  // Healthcare: hospital
            "\uD83C\uDFAC",  // Entertainment: clapper
            "\uD83D\uDC87",  // Personal Care: haircut
            "\uD83D\uDECD\uFE0F",  // Shopping: bags
            "\uD83D\uDCB0",  // Savings: money bag
            "\u2708\uFE0F"   // Travel: airplane
    };

    public static final int[] COLOR_RES = {
            R.color.category_housing,
            R.color.category_utilities,
            R.color.category_transportation,
            R.color.category_groceries,
            R.color.category_dining,
            R.color.category_healthcare,
            R.color.category_entertainment,
            R.color.category_personal_care,
            R.color.category_shopping,
            R.color.category_savings,
            R.color.category_travel
    };

    public static int indexOf(String category) {
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].equals(category)) return i;
        }
        return ALL.length - 1; // default to Travel
    }

    private Categories() {}
}