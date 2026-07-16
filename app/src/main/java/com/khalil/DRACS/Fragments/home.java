package com.khalil.DRACS.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.khalil.DRACS.R;

public class home extends Fragment {

    public home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        View rnaClick = view.findViewById(R.id.RNA);
        View psClick = view.findViewById(R.id.ps);
        View fdaClick = view.findViewById(R.id.FDA);
        View fpClick = view.findViewById(R.id.FP);
        View jeClick = view.findViewById(R.id.JE);

        rnaClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_RNA));
        psClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_PS));
        fdaClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_FDA));
        fpClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_FP));
        jeClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_JE));

        return view;
    }

    private void navigateTo(int actionId) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(actionId);
    }
}
