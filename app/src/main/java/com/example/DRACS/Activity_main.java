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

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class Activity_main extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavController navController;;
    ActionBarDrawerToggle toggle;
    ImageView drawer_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawer_icon = findViewById(R.id.drawer_icon);
//
//
//
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//
        drawer_icon.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
//
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
//
//        //handling navigation
        navController = findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
//
        TextView textTitle = findViewById(R.id.title);
//
        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                textTitle.setText(destination.getLabel()));
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
}
