package com.ssverma.showtime.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AppUtility {

    private AppUtility() {
        //Prevent instantiation
    }

    public static String buildThumbnailUrl(String videoId) {
        return "http://img.youtube.com/vi/" + videoId + "/0.jpg";
    }

    public static void launchYoutube(Context context, String videoId) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube" + videoId));
        if (appIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(appIntent);
            return;
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(browserIntent);
            return;
        }

        ViewUtils.displayToast(context, "Unable to launch!");
    }

    public static String buildPosterUrl(String posterPath) {
        return "http://image.tmdb.org/t/p/w342" + posterPath;
    }

    public static String buildCastImageUrl(String profilePath) {
        return "http://image.tmdb.org/t/p/w342" + profilePath;
    }

    public static String addDollarSymbol(int value) {
        return "$" + value;
    }

    public static void popupShareDialog(Context context, String shareableContent) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareableContent);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "Share with..."));
        } else {
            ViewUtils.displayToast(context, "No app to share!");
        }
    }

    public static String getVideoShareUrl(String videoId) {
        return "http://www.youtube.com/watch?v=" + videoId;
    }

}
