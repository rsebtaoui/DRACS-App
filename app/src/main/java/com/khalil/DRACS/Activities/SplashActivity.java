package com.khalil.DRACS.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;
import com.khalil.DRACS.Utils.LocaleHelper;

public class SplashActivity extends AppCompatActivity {
    private static final long DATA_FETCH_TIMEOUT = 2000;
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_HAS_PERSISTENT_DATA = "has_persistent_data";
    private static final String KEY_DARK_MODE = "dark_mode";

    private DataPreFetcher dataPreFetcher;
    private Handler handler;
    private boolean isDataFetchComplete = false;
    private boolean launchedMain = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.wrapArabic(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force Arabic + RTL before inflating the splash layout
        LocaleHelper.enforceApplicationArabic();
        LocaleHelper.applyArabicToResources(getResources());
        LocaleHelper.clearLanguagePreference(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_splash);

        MaterialButton startButton = findViewById(R.id.splash_start_button);
        startButton.setOnClickListener(v -> startMainActivity());

        handler = new Handler(Looper.getMainLooper());

        boolean hasPersistentData = prefs.getBoolean(KEY_HAS_PERSISTENT_DATA, false);
        if (!hasPersistentData) {
            dataPreFetcher = new DataPreFetcher(this);
            dataPreFetcher.startPreFetching(success -> {
                isDataFetchComplete = true;
                if (success) {
                    prefs.edit().putBoolean(KEY_HAS_PERSISTENT_DATA, true).apply();
                }
            });
            handler.postDelayed(() -> isDataFetchComplete = true, DATA_FETCH_TIMEOUT);
        } else {
            isDataFetchComplete = true;
        }
    }

    private void startMainActivity() {
        if (isFinishing() || launchedMain) {
            return;
        }
        launchedMain = true;
        Intent intent = new Intent(SplashActivity.this, Activity_main.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
