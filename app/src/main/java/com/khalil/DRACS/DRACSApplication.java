package com.khalil.DRACS;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.khalil.DRACS.Utils.LocaleHelper;
import com.khalil.DRACS.Utils.ThemeHelper;

public class DRACSApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Follow the phone's system dark/light mode unless the user forced dark in Settings
        ThemeHelper.applySavedTheme(this);

        // Arabic-only release: lock locale + RTL for the whole process
        LocaleHelper.enforceApplicationArabic();
        LocaleHelper.clearLanguagePreference(this);

        FirebaseApp.initializeApp(this);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().setCustomKey("app_version", BuildConfig.VERSION_NAME);
        FirebaseCrashlytics.getInstance().setCustomKey("build_type", BuildConfig.BUILD_TYPE);
        FirebaseCrashlytics.getInstance().setCustomKey(
                "night_mode",
                String.valueOf(AppCompatDelegate.getDefaultNightMode()));
    }
}
