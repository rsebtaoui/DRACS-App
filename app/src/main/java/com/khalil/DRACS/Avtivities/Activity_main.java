package com.khalil.DRACS.Avtivities;

import static androidx.navigation.Navigation.findNavController;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class Activity_main extends AppCompatActivity {

    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LAST_NAV_ITEM = "last_nav_item";

    SmoothBottomBar smoothBottomBar;
    NavController navController;
    ImageView info;
    private AppUpdateManager appUpdateManager;
    private DataPreFetcher dataPreFetcher;
    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        }
    };
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    public DataPreFetcher getDataPreFetcher() {
        return dataPreFetcher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set window soft input mode to adjust resize
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        
        // Apply saved theme
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        
        setContentView(R.layout.activity_home);

        // Initialize AppUpdateManager with package name verification
        try {
            String packageName = getPackageName();
            if (packageName.equals("com.khalil.DRACS")) {
                appUpdateManager = AppUpdateManagerFactory.create(this);
            } else {
                Log.e("AppUpdate", "Package name mismatch: " + packageName);
                return;
            }
        } catch (Exception e) {
            Log.e("AppUpdate", "Error initializing AppUpdateManager: " + e.getMessage());
            return;
        }
        
        // Initialize DataPreFetcher
        dataPreFetcher = new DataPreFetcher(this);

        // Define the views
        smoothBottomBar = findViewById(R.id.bottomBar);
        ImageView moreIcon = findViewById(R.id.notif_icon);

        // Set up more icon click listener
        moreIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(Activity_main.this, v);
            popup.getMenuInflater().inflate(R.menu.more_menu, popup.getMenu());

            // Force show icons in the popup menu
            try {
                java.lang.reflect.Field[] fields = popup.getClass().getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        java.lang.reflect.Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.share_app) {
                    shareApp();
                    return true;
                } else if (itemId == R.id.exite) {
                    exitApp();
                    return true;
                }
                return false;
            });

            popup.show();
        });

        // Register the ActivityResultLauncher for app updates
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK) {
                        Log.w("Activity_main", "Update flow failed! Result code: " + result.getResultCode());
                        Toast.makeText(this, "Update failed, try again later", Toast.LENGTH_SHORT).show();
                    }
                });

        // Handling navigation
        navController = findNavController(this, R.id.navHostFragment);
        TextView textTitle = findViewById(R.id.title);
        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                textTitle.setText(destination.getLabel()));

        // Handling bottom nav bar navigation
        smoothBottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            // Save the selected item index
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_LAST_NAV_ITEM, i);
            editor.apply();

            switch (i) {
                case 0:
                    navController.navigate(R.id.home);
                    break;
                case 1:
                    navController.navigate(R.id.setting);
                    break;
                case 2:
                    navController.navigate(R.id.Search);
                    break;
            }
            return false;
        });

        // Restore the last selected navigation item
        int lastNavItem = prefs.getInt(KEY_LAST_NAV_ITEM, 0);
        smoothBottomBar.setItemActiveIndex(lastNavItem);

        // Move app update check to a background thread
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Small delay to ensure UI is fully initialized
                runOnUiThread(this::checkForAppUpdate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "تحميل تطبيق المديرية الجهوية للفلاحة لجهة الدارالبيضاء-سطات\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "شارك التطبيق"));
    }

    private void visitWebsite() {
        String url = "https://www.agriculture.gov.ma/"; // Replace with your actual website URL
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void exitApp() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        appUpdateManager.unregisterListener(listener);
    }

    private void checkForAppUpdate() {
        if (appUpdateManager == null) {
            Log.e("AppUpdate", "AppUpdateManager is null");
            return;
        }

        try {
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                try {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    activityResultLauncher,
                                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build());
                        } catch (Exception e) {
                            Log.e("AppUpdate", "Error starting update flow: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    Log.e("AppUpdate", "Error checking update availability: " + e.getMessage());
                }
            }).addOnFailureListener(e -> {
                Log.e("AppUpdate", "Failed to get update info: " + e.getMessage());
            });

            try {
                appUpdateManager.registerListener(listener);
            } catch (Exception e) {
                Log.e("AppUpdate", "Error registering update listener: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e("AppUpdate", "Error in checkForAppUpdate: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (appUpdateManager == null) {
            Log.e("AppUpdate", "AppUpdateManager is null in onResume");
            return;
        }

        try {
            appUpdateManager.getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {
                        try {
                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                popupSnackbarForCompleteUpdate();
                            }
                        } catch (Exception e) {
                            Log.e("AppUpdate", "Error checking install status: " + e.getMessage());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AppUpdate", "Failed to get update info in onResume: " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e("AppUpdate", "Error in onResume: " + e.getMessage());
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        try {
            Snackbar snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "An update has just been downloaded.",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("INSTALL", view -> {
                try {
                    appUpdateManager.completeUpdate();
                } catch (Exception e) {
                    Log.e("AppUpdate", "Error completing update: " + e.getMessage());
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.Emerald_Green_700));
            snackbar.show();
        } catch (Exception e) {
            Log.e("AppUpdate", "Error showing update snackbar: " + e.getMessage());
        }
    }

    public void hideBottomAppBar() {
        // Access your bottom app bar view
        SmoothBottomBar bottomAppBar = findViewById(R.id.bottomBar);

        // Hide the bottom app bar
        bottomAppBar.setVisibility(View.GONE);
    }

    public void showBottomAppBar() {
        // Access your bottom app bar view
        SmoothBottomBar bottomAppBar = findViewById(R.id.bottomBar);

        // Hide the bottom app bar
        bottomAppBar.setVisibility(View.VISIBLE);
    }

    public void updateBottomBarSelection(int itemId) {
        // Access your bottom app bar view
        SmoothBottomBar bottomAppBar = findViewById(R.id.bottomBar);
        
        // Update the selection
        if (itemId == R.id.home) {
            bottomAppBar.setItemActiveIndex(0);
            saveLastNavItem(0);
        } else if (itemId == R.id.setting) {
            bottomAppBar.setItemActiveIndex(1);
            saveLastNavItem(1);
        } else if (itemId == R.id.Search) {
            bottomAppBar.setItemActiveIndex(2);
            saveLastNavItem(2);
        }
    }

    private void saveLastNavItem(int index) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_LAST_NAV_ITEM, index);
        editor.apply();
    }
}
