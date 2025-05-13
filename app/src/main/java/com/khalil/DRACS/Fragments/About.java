package com.khalil.DRACS.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.khalil.DRACS.R;

public class About extends Fragment {

    public About() {
        // Required empty public constructor
    }

    public static About newInstance(String param1, String param2) {
        About fragment = new About();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        
        MaterialButton updateButton = view.findViewById(R.id.Update);
        updateButton.setOnClickListener(v -> openAppInPlayStore());

        // Set up back press handling
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to settings
                Navigation.findNavController(requireView()).navigate(R.id.action_about_to_home);
            }
        });

        return view;
    }

    // for the button of the update
    private void openAppInPlayStore() {
        String appUrl = "https://play.google.com/store/apps/details?id=com.khalil.DRACS";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}