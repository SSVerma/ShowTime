package com.ssverma.showtime.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private static final String PREF_NAME = "ShowTimePref";
    private static final String KEY_SELECTED_INDEX = "key_selected_index";

    private SharedPrefHelper() {
        //Prevent instantiation
    }

    public static void saveSortSelectedIndex(Context context, int selectedIndex) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SELECTED_INDEX, selectedIndex);
        editor.apply();
    }

    public static int getSortSelectedIndex(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_SELECTED_INDEX, 0);
    }
}
