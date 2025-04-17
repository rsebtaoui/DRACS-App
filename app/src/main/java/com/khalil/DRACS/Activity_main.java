package com.khalil.DRACS;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
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

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class Activity_main extends AppCompatActivity {

    SmoothBottomBar smoothBottomBar;
    DrawerLayout drawerLayout;
    NavController navController;
    ActionBarDrawerToggle toggle;
    ImageView drawer_icon;
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
        drawerLayout = findViewById(R.id.drawer_layout);
        drawer_icon = findViewById(R.id.drawer_icon);
//        info = findViewById(R.id.FAQ_icon);

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
                    navController.navigate(R.id.home);
                    break;
                case 2:
                    navController.navigate(R.id.home);
                    break;
            }
            return false;
        });

//        info.setOnClickListener(v -> navController.navigate(R.id.Activity_info));

        drawer_icon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        checkForAppUpdate();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (!navController.popBackStack()) {
            super.onBackPressed();
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
}
