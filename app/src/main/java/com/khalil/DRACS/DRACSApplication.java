package com.khalil.DRACS;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class DRACSApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
} 