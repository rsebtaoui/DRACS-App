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
import android.view.View;
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

        //define the views
        smoothBottomBar = findViewById(R.id.bottomBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawer_icon = findViewById(R.id.drawer_icon);
        info = findViewById(R.id.FAQ_icon);


        //parametrizing the navigation drawer
//        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.App_ifo);
            }
        });

        //to assign the drawer to the icon , when click the button the drawer open
        drawer_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //de-comment to assign it
//                drawerLayout.openDrawer(GravityCompat.START);
                //back home
                navController.navigate(R.id.home);
            }
        });

        //handling drawer
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        navigationView.setItemIconTintList(null);
//
//        // Handling navigation
        navController = findNavController(this, R.id.navHostFragment);
//        NavigationUI.setupWithNavController(navigationView, navController);
        TextView textTitle = findViewById(R.id.title);
        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                textTitle.setText(destination.getLabel()));

        //handling button nav bar navigation
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
                    case 2:
                        exitApp();
                }
                return false;
            }
        });

        // Handle drawer navigation item clicks
//        navigationView.setNavigationItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.home) {
//                navController.navigate(R.id.home);
//                smoothBottomBar.setItemActiveIndex(0);
//            } else if (item.getItemId() == R.id.RNA) {
//                navController.navigate(R.id.RNA);
//                smoothBottomBar.setItemActiveIndex(0);
//            }
//            drawerLayout.closeDrawer(GravityCompat.START);
//            return true;
//        });
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

}
