package com.khalil.DRACS.Avtivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 500; // Reduced to 500ms
    private static final long DATA_FETCH_TIMEOUT = 2000; // 2 seconds timeout for data fetching
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_HAS_PERSISTENT_DATA = "has_persistent_data";
    private static final String KEY_DARK_MODE = "dark_mode";
    private DataPreFetcher dataPreFetcher;
    private Handler handler;
    private boolean isDataFetchComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Remove the title bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Apply saved theme before setting content view
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_splash);

        handler = new Handler(Looper.getMainLooper());

        // Check if we already have persistent data
        boolean hasPersistentData = prefs.getBoolean(KEY_HAS_PERSISTENT_DATA, false);

        if (hasPersistentData) {
            // If we have persistent data, just show splash for minimum time
            handler.postDelayed(this::startMainActivity, SPLASH_DELAY);
        } else {
            // Initialize data pre-fetcher
            dataPreFetcher = new DataPreFetcher(this);
            
            // Start pre-fetching data
            dataPreFetcher.startPreFetching(success -> {
                isDataFetchComplete = true;
                if (success) {
                    // Save that we have pre-fetched data
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(KEY_HAS_PERSISTENT_DATA, true);
                    editor.apply();
                }
                startMainActivity();
            });

            // Set a timeout for data fetching
            handler.postDelayed(() -> {
                if (!isDataFetchComplete) {
                    startMainActivity();
                }
            }, DATA_FETCH_TIMEOUT);
        }
    }

    private void startMainActivity() {
        if (!isFinishing()) {
            Intent intent = new Intent(SplashActivity.this, Activity_main.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
