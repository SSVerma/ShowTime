package com.ssverma.showtime.utils;

import android.content.Context;
import android.util.TypedValue;
import android.widget.Toast;

public class ViewUtils {
    private ViewUtils() {
        //Prevent instantiation
    }

    public static void displayToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void displayToast(Context context, String message, boolean shouldShowLong) {
        Toast.makeText(context, message, shouldShowLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static float dpToPx(Context context, int dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

}
