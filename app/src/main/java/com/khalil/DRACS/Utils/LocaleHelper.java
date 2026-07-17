package com.khalil.DRACS.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

/**
 * Locks the app to Arabic (RTL) for this release.
 */
public final class LocaleHelper {

    public static final String LANGUAGE_ARABIC = "ar";
    public static final String PREFS_NAME = "DRACS_Prefs";
    /** Legacy preference — removed; app is Arabic-only. */
    public static final String KEY_LANGUAGE = "app_language";

    private LocaleHelper() {
    }

    @NonNull
    public static Context wrapArabic(@NonNull Context context) {
        Locale locale = new Locale(LANGUAGE_ARABIC);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        return context.createConfigurationContext(config);
    }

    /** Apply Arabic as the AppCompat per-app locale (survives process recreation). */
    public static void enforceApplicationArabic() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(LANGUAGE_ARABIC));
    }

    /** Drop obsolete language preference from {@code DRACS_Prefs}. */
    public static void clearLanguagePreference(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.contains(KEY_LANGUAGE)) {
            prefs.edit().remove(KEY_LANGUAGE).apply();
        }
    }

    @SuppressWarnings("deprecation")
    public static void applyArabicToResources(@NonNull Resources resources) {
        Locale locale = new Locale(LANGUAGE_ARABIC);
        Locale.setDefault(locale);
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
