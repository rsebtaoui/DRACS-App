package com.khalil.DRACS.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Theme modes: follow the phone (auto), or lock light/dark in the app.
 */
public final class ThemeHelper {
    public static final String PREFS_NAME = "DRACS_Prefs";
    /** Legacy boolean: true = force dark. */
    public static final String KEY_DARK_MODE = "dark_mode";
    public static final String KEY_THEME_MODE = "theme_mode";

    public static final String MODE_SYSTEM = "system";
    public static final String MODE_LIGHT = "light";
    public static final String MODE_DARK = "dark";

    private ThemeHelper() {
    }

    public static void applySavedTheme(Context context) {
        AppCompatDelegate.setDefaultNightMode(resolveNightMode(context));
    }

    @NonNull
    public static String getThemeMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String themeMode = prefs.getString(KEY_THEME_MODE, null);
        if (themeMode != null) {
            return themeMode;
        }
        // Migrate legacy dark_mode=true → dark; otherwise auto (system)
        if (prefs.contains(KEY_DARK_MODE) && prefs.getBoolean(KEY_DARK_MODE, false)) {
            return MODE_DARK;
        }
        return MODE_SYSTEM;
    }

    public static int resolveNightMode(Context context) {
        switch (getThemeMode(context)) {
            case MODE_DARK:
                return AppCompatDelegate.MODE_NIGHT_YES;
            case MODE_LIGHT:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case MODE_SYSTEM:
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    public static void setThemeMode(Context context, @NonNull String themeMode) {
        String normalized;
        switch (themeMode) {
            case MODE_LIGHT:
            case MODE_DARK:
            case MODE_SYSTEM:
                normalized = themeMode;
                break;
            default:
                normalized = MODE_SYSTEM;
                break;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_THEME_MODE, normalized)
                .putBoolean(KEY_DARK_MODE, MODE_DARK.equals(normalized))
                .apply();

        AppCompatDelegate.setDefaultNightMode(resolveNightMode(context));
    }

    public static boolean isCurrentlyNight(Context context) {
        int nightFlags = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return nightFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
