package com.khalil.DRACS.Avtivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 1000; // 1 second
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_HAS_PERSISTENT_DATA = "has_persistent_data";
    private DataPreFetcher dataPreFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Remove the title bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Check for persistent data
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean hasPersistentData = prefs.getBoolean(KEY_HAS_PERSISTENT_DATA, false);

        if (hasPersistentData) {
            Toast.makeText(this, "تم العثور على بيانات مستمرة", Toast.LENGTH_LONG).show();
        }

        // Initialize data pre-fetcher
        dataPreFetcher = new DataPreFetcher(this);
        
        // Start pre-fetching data
        dataPreFetcher.startPreFetching(success -> {
            if (success) {
                // Save that we have pre-fetched data
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_HAS_PERSISTENT_DATA, true);
                editor.apply();
            }
            
            // Start the main activity
            Intent intent = new Intent(SplashActivity.this, Activity_main.class);
            startActivity(intent);
            finish();
        });
    }
}
