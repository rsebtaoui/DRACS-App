package com.khalil.DRACS.Activities;

import static androidx.navigation.Navigation.findNavController;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.khalil.DRACS.Utils.ConnectionUtils;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class Activity_main extends AppCompatActivity {

    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LAST_NAV_ITEM = "last_nav_item";

    SmoothBottomBar smoothBottomBar;
    NavController navController;
    ImageView dracsicon;
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
        
        // Initialize DataPreFetcher
        dataPreFetcher = new DataPreFetcher(this);

        dracsicon=findViewById(R.id.dracs);
        dracsicon.setOnClickListener(v -> {
            navController.navigate(R.id.about);
        });

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

        // Handling navigation
        navController = findNavController(this, R.id.navHostFragment);
        TextView textTitle = findViewById(R.id.title);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            textTitle.setText(destination.getLabel());
            handleDestinationChange(destination.getId());
            
            // Update the icon based on destination
            if (destination.getId() == R.id.home) {
                dracsicon.setImageResource(R.mipmap.ic_dra_3);
                dracsicon.setOnClickListener(v -> {
                    navController.navigate(R.id.about);
                });
            } else {
                dracsicon.setImageResource(R.drawable.ic_back);
                dracsicon.setOnClickListener(v -> {
                    navController.navigate(R.id.home);
                });
            }
        });

        // Handling bottom nav bar navigation
        smoothBottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            // Save the selected item index
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt(KEY_LAST_NAV_ITEM, i);
            editor.apply();

            switch (i) {
                case 0:
                    navController.navigate(R.id.home);
                    break;
                case 1:
                    navController.navigate(R.id.Search);
                    break;
                case 2:
                    navController.navigate(R.id.setting);
                    break;
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
        if (smoothBottomBar != null) {
            runOnUiThread(() -> {
                smoothBottomBar.setVisibility(View.GONE);
                smoothBottomBar.postInvalidate();
            });
        }
    }

    public void showBottomAppBar() {
        if (smoothBottomBar != null) {
            runOnUiThread(() -> {
                smoothBottomBar.setVisibility(View.VISIBLE);
                smoothBottomBar.postInvalidate();
            });
        }
    }

    private void syncBottomBarWithFragment(int fragmentId) {
        if (fragmentId == R.id.home) {
            smoothBottomBar.setItemActiveIndex(0);
            saveLastNavItem(0);
            showBottomAppBar();
        } else if (fragmentId == R.id.Search) {
            smoothBottomBar.setItemActiveIndex(1);
            saveLastNavItem(1);
            showBottomAppBar();
        } else if (fragmentId == R.id.setting) {
            smoothBottomBar.setItemActiveIndex(2);
            saveLastNavItem(2);
            showBottomAppBar();
        } else {
            hideBottomAppBar();
        }
    }

    private void handleDestinationChange(int destinationId) {
        updateBottomBarVisibilityForDestination(destinationId);
        
        // Only sync bottom bar selection for main navigation destinations
        if (destinationId == R.id.home || destinationId == R.id.setting || destinationId == R.id.Search) {
            syncBottomBarWithFragment(destinationId);
        }
    }

    public void updateBottomBarSelection(int itemId) {
        runOnUiThread(() -> {
            if (smoothBottomBar != null) {
                if (itemId == R.id.home) {
                    smoothBottomBar.setItemActiveIndex(0);
                    saveLastNavItem(0);
                } else if (itemId == R.id.Search) {
                    smoothBottomBar.setItemActiveIndex(1);
                    saveLastNavItem(1);
                } else if (itemId == R.id.setting) {
                    smoothBottomBar.setItemActiveIndex(2);
                    saveLastNavItem(2);
                }
            }
        });
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
        if (destinationId == R.id.home || destinationId == R.id.setting || destinationId == R.id.Search) {
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
