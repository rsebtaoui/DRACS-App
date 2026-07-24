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

import com.google.android.material.button.MaterialButton;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;
import com.khalil.DRACS.Utils.LocaleHelper;
import com.khalil.DRACS.Utils.ThemeHelper;

public class SplashActivity extends AppCompatActivity {
    /** Toggle the onboarding/start screen. Disabled for now — app opens straight to the dashboard. */
    private static final boolean ONBOARDING_ENABLED = false;
    private static final long DATA_FETCH_TIMEOUT = 2000;
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_HAS_PERSISTENT_DATA = "has_persistent_data";

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

        // Re-apply theme early on the splash entry path (Application already set FOLLOW_SYSTEM)
        ThemeHelper.applySavedTheme(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Warm the cache on first run regardless of the onboarding screen.
        boolean hasPersistentData = prefs.getBoolean(KEY_HAS_PERSISTENT_DATA, false);
        if (!hasPersistentData) {
            dataPreFetcher = new DataPreFetcher(this);
            dataPreFetcher.startPreFetching(success -> {
                isDataFetchComplete = true;
                if (success) {
                    prefs.edit().putBoolean(KEY_HAS_PERSISTENT_DATA, true).apply();
                }
            });
        } else {
            isDataFetchComplete = true;
        }

        if (!ONBOARDING_ENABLED) {
            // Onboarding disabled: skip the start screen and open the dashboard directly.
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_splash);

        MaterialButton startButton = findViewById(R.id.splash_start_button);
        startButton.setOnClickListener(v -> startMainActivity());

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> isDataFetchComplete = true, DATA_FETCH_TIMEOUT);
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
