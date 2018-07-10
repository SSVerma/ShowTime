package com.ssverma.showtime.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private static final String PREF_NAME = "ShowTimePrefs";
    private static final String KEY_SELECTED_PATH = "key_selected_index";

    private SharedPrefHelper() {
        //Prevent instantiation
    }

    public static void saveSortSelectedPath(Context context, String selectedPath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_PATH, selectedPath);
        editor.apply();
    }

    public static String getLastSortSelectedPath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SELECTED_PATH, null);
    }
}
