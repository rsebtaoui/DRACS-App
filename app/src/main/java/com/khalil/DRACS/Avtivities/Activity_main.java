package com.khalil.DRACS.Avtivities;

import static androidx.navigation.Navigation.findNavController;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class Activity_main extends AppCompatActivity {

    SmoothBottomBar smoothBottomBar;
    NavController navController;
    ImageView info;
    private AppUpdateManager appUpdateManager;
    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        }
    };
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
                } else if (itemId == R.id.visit_website) {
                    visitWebsite();
                    return true;
                } else if (itemId == R.id.update_app) {
                    checkForAppUpdate();
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
            switch (i) {
                case 0:
                    navController.navigate(R.id.home);
                    break;
                case 1:
                    navController.navigate(R.id.setting);
                    break;
                case 2:
                    navController.navigate(R.id.About);
                    break;
            }
            return false;
        });

//        info.setOnClickListener(v -> navController.navigate(R.id.Activity_info));

        checkForAppUpdate();
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
        appUpdateManager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build());
            }
        });

        appUpdateManager.registerListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    }
                });
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("INSTALL", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(getResources().getColor(R.color.Emerald_Green_700));
        snackbar.show();
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
}
