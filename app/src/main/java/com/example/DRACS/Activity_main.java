package com.example.DRACS;

import static androidx.navigation.Navigation.findNavController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class Activity_main extends AppCompatActivity {

    SmoothBottomBar smoothBottomBar;
    DrawerLayout drawerLayout;
    NavController navController;
    ActionBarDrawerToggle toggle;
    ImageView drawer_icon;
    ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        smoothBottomBar = findViewById(R.id.bottomBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawer_icon = findViewById(R.id.drawer_icon);
        info = findViewById(R.id.FAQ_icon);


        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        drawer_icon.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        // Handling navigation
        navController = findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView textTitle = findViewById(R.id.title);

        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                textTitle.setText(destination.getLabel()));

        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        navController.navigate(R.id.home);
                        break;
                    case 1:
                        navController.navigate(R.id.setting2);
                        break;
                }
                return false;
            }
        });

        // Handle drawer navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                navController.navigate(R.id.home);
                smoothBottomBar.setItemActiveIndex(0);
            } else if (item.getItemId() == R.id.RNA) {
                navController.navigate(R.id.RNA);
                smoothBottomBar.setItemActiveIndex(0);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
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
    public void changeIconmenu(int newIconResId) {
        if (drawer_icon != null) {
            drawer_icon.setImageResource(newIconResId);
        }
    }
    public void changeIconinfo(int newIconResId) {
        if (info != null) {
            info.setImageResource(newIconResId);
        }
    }
}
