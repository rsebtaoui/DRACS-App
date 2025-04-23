package com.khalil.DRACS;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

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
    }
} 