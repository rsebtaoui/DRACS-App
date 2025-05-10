package com.khalil.DRACS.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.khalil.DRACS.R;
import com.khalil.DRACS.Views.InteractiveMapView;

public class home extends Fragment {

    public home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView RNAclick = view.findViewById(R.id.RNA);
        TextView PSclick = view.findViewById(R.id.ps);
        TextView FDAclick = view.findViewById(R.id.FDA);
        TextView FPclick = view.findViewById(R.id.FP);
        TextView JEclick = view.findViewById(R.id.JE);

        //navigation to RNA fragment
        RNAclick.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_home_to_RNA);
        });

        //navigation to PS fragment
        PSclick.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_home_to_PS);
        });

        FDAclick.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_home_to_FDA);
        });

        FPclick.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_home_to_FP);
        });

        JEclick.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_home_to_JE);
        });

        return view;
    }
}
