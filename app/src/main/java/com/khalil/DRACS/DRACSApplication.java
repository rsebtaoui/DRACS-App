package com.khalil.DRACS;

import android.app.Application;

import com.google.firebase.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class DRACSApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        // Configure Firestore settings
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        
        // Initialize Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        
        // Optional: Set custom keys for better crash reporting
        FirebaseCrashlytics.getInstance().setCustomKey("app_version", BuildConfig.VERSION_NAME);
        FirebaseCrashlytics.getInstance().setCustomKey("build_type", BuildConfig.BUILD_TYPE);
    }
} 