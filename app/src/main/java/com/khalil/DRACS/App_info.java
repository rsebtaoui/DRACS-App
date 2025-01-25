package com.khalil.DRACS;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class App_info extends Fragment {


    public App_info() {
        // Required empty public constructor
    }

    public static App_info newInstance(String param1, String param2) {
        App_info fragment = new App_info();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_info, container, false);

    }
// for the button of the update
//    private void openAppInPlayStore() {
//        String packageName = getPackageName(); // Get the app's package name
//        try {
//            // Open Play Store app if installed
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } catch (Exception e) {
//            // Fallback to browser if Play Store app is not installed
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//    }
}