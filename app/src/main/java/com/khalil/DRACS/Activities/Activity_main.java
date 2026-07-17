package com.khalil.DRACS.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.khalil.DRACS.Repository.ContentRepository;
import com.khalil.DRACS.Utils.DataPreFetcher;
import com.khalil.DRACS.Utils.ConnectionUtils;
import com.khalil.DRACS.Utils.LocaleHelper;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class Activity_main extends AppCompatActivity {

    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LARGE_FONT = "large_font";
    private static final String KEY_LAST_NAV_ITEM = "last_nav_item";

    BottomNavigationView bottomNav;
    NavController navController;
    ImageView dracsicon;
    ImageView info;
    private AppUpdateManager appUpdateManager;
    private DataPreFetcher dataPreFetcher;
    private ContentRepository contentRepository;
    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        }
    };
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private Runnable pendingNotificationAction;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float fontScale = prefs.getBoolean(KEY_LARGE_FONT, false) ? 1.15f : 1.0f;
        Context arabicBase = LocaleHelper.wrapArabic(newBase);
        Configuration config = new Configuration(arabicBase.getResources().getConfiguration());
        config.fontScale = fontScale;
        super.attachBaseContext(arabicBase.createConfigurationContext(config));
    }

    public DataPreFetcher getDataPreFetcher() {
        return dataPreFetcher;
    }

    public ContentRepository getContentRepository() {
        return contentRepository;
    }

    /**
     * Runs the action immediately if notification permission is granted (or not required).
     * On API 33+, requests POST_NOTIFICATIONS via Activity Result API when needed.
     */
    public void runWithNotificationPermission(Runnable action) {
        if (action == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            action.run();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            action.run();
            return;
        }
        pendingNotificationAction = action;
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted && pendingNotificationAction != null) {
                        pendingNotificationAction.run();
                    } else if (!isGranted) {
                        Toast.makeText(this, "يرجى تفعيل الإشعارات للاستمرار", Toast.LENGTH_LONG).show();
                    }
                    pendingNotificationAction = null;
                }
        );
        
        // Set window soft input mode to adjust resize
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        
        setContentView(R.layout.activity_home);

        // Initialize AppUpdateManager with package name verification
        try {
            String packageName = getPackageName();
            if (packageName.equals("com.khalil.DRACS")) {
                appUpdateManager = AppUpdateManagerFactory.create(this);
            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }
        
        // Initialize DataPreFetcher and ContentRepository
        dataPreFetcher = new DataPreFetcher(this);
        contentRepository = new ContentRepository(this, dataPreFetcher);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found for R.id.navHostFragment");
        }
        navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottom_nav);
        dracsicon = findViewById(R.id.dracs);
        ImageView moreIcon = findViewById(R.id.notif_icon);
        ImageView searchIcon = findViewById(R.id.search_icon);
        TextView textTitle = findViewById(R.id.title);

        searchIcon.setOnClickListener(v -> navController.navigate(R.id.Search));

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
                } else if (itemId == R.id.send_feedback) {
                    sendFeedback();
                    return true;
                } else if (itemId == R.id.visit_website) {
                    visitWebsite();
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
                    }
                });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            textTitle.setText(destination.getLabel());
            handleDestinationChange(destination.getId());

            if (destination.getId() == R.id.home) {
                dracsicon.clearColorFilter();
                dracsicon.setImageResource(R.mipmap.ic_dra_3);
                dracsicon.setOnClickListener(v -> navController.navigate(R.id.about));
            } else {
                dracsicon.setImageResource(R.drawable.ic_back);
                dracsicon.setColorFilter(ContextCompat.getColor(this, R.color.primary_foreground));
                dracsicon.setOnClickListener(v -> navController.navigate(R.id.home));
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                navController.navigate(R.id.home);
                saveLastNavItem(0);
                return true;
            } else if (itemId == R.id.favorites) {
                navController.navigate(R.id.favorites);
                saveLastNavItem(1);
                return true;
            } else if (itemId == R.id.setting) {
                navController.navigate(R.id.setting);
                saveLastNavItem(2);
                return true;
            }
            return false;
        });

        // Initial visibility check
        if (navController.getCurrentDestination() != null) {
            handleDestinationChange(navController.getCurrentDestination().getId());
        }

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
        String url = "https://www.dracs.gov.ma/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "لا يمكن فتح الموقع", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:r.sebtaoui@agriculture.gov.ma"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "تقييم تطبيق المديرية الجهوية للفلاحة");
        intent.putExtra(Intent.EXTRA_TEXT, "مرحباً،\n\nأود مشاركة رأيي حول التطبيق:\n\n");
        try {
            startActivity(Intent.createChooser(intent, "إرسال البريد الإلكتروني"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "لا يوجد تطبيق بريد إلكتروني مثبت", Toast.LENGTH_SHORT).show();
        }
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
            return;
        }

        try {
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                try {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                        if (ConnectionUtils.isNetworkAvailable(this)) {
                            // Mandatory update if internet is available
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    activityResultLauncher,
                                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
                        } else {
                            // Flexible update if no internet
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    activityResultLauncher,
                                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build());
                        }
                    }
                } catch (Exception e) {
                    // Log the exception to Firebase Crashlytics
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }).addOnFailureListener(e -> {
                // Log the failure to Firebase Crashlytics
                FirebaseCrashlytics.getInstance().recordException(e);
            });

            try {
                appUpdateManager.registerListener(listener);
            } catch (Exception e) {
                // Log the exception to Firebase Crashlytics
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        } catch (Exception e) {
            // Log the exception to Firebase Crashlytics
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (appUpdateManager == null) {
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
                        }
                    })
                    .addOnFailureListener(e -> {
                    });
        } catch (Exception e) {
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
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.Emerald_Green_700));
            snackbar.show();
        } catch (Exception e) {
        }
    }

    public void hideBottomAppBar() {
        if (bottomNav != null) {
            runOnUiThread(() -> bottomNav.setVisibility(View.GONE));
        }
    }

    public void showBottomAppBar() {
        if (bottomNav != null) {
            runOnUiThread(() -> bottomNav.setVisibility(View.VISIBLE));
        }
    }

    private void syncBottomBarWithFragment(int fragmentId) {
        if (fragmentId == R.id.home) {
            checkBottomNavItem(R.id.home);
            saveLastNavItem(0);
            showBottomAppBar();
        } else if (fragmentId == R.id.favorites) {
            checkBottomNavItem(R.id.favorites);
            saveLastNavItem(1);
            showBottomAppBar();
        } else if (fragmentId == R.id.setting) {
            checkBottomNavItem(R.id.setting);
            saveLastNavItem(2);
            showBottomAppBar();
        } else {
            hideBottomAppBar();
        }
    }

    private void checkBottomNavItem(int itemId) {
        if (bottomNav != null && bottomNav.getMenu().findItem(itemId) != null) {
            bottomNav.getMenu().findItem(itemId).setChecked(true);
        }
    }

    private void handleDestinationChange(int destinationId) {
        updateBottomBarVisibilityForDestination(destinationId);

        if (destinationId == R.id.home
                || destinationId == R.id.setting
                || destinationId == R.id.favorites) {
            syncBottomBarWithFragment(destinationId);
        }
    }

    public void updateBottomBarSelection(int itemId) {
        runOnUiThread(() -> checkBottomNavItem(itemId));
    }

    private void saveLastNavItem(int index) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_LAST_NAV_ITEM, index);
        editor.apply();
    }

    // Method to manually set bottom app bar visibility
    // based on destination ID for search results navigation
    public void updateBottomBarVisibilityForDestination(int destinationId) {
        if (destinationId == R.id.home
                || destinationId == R.id.setting
                || destinationId == R.id.favorites) {
            showBottomAppBar();
        } else {
            hideBottomAppBar();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
