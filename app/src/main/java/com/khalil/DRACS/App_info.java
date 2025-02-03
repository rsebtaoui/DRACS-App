package com.khalil.DRACS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class App_info extends Fragment {


    public App_info() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_info, container, false);
        Button Update = view.findViewById(R.id.Update);

        Update.setOnClickListener(v -> openAppInPlayStore());

        return view;
    }
    // for the button of the update
    private void openAppInPlayStore() {
        String testUrl = "https://play.google.com/store/apps/details?id=com.khalil.DRACS";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(testUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}