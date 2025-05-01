package com.khalil.DRACS.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khalil.DRACS.Adapters.ExpandableAdapter;
import com.khalil.DRACS.Avtivities.Activity_main;
import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PS extends Fragment {
    private RecyclerView recyclerView;
    private ExpandableAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_p_s, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.psrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch data from Firestore
        fetchDataFromFirestore();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Navigate back to HomeFragment using Navigation Component
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
                        navController.navigate(R.id.action_PS_to_home);
                    }
                }
        );

        return view;
    }

    private void fetchDataFromFirestore() {
        db.collection("pages").document("ps")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FirestoreModel model = document.toObject(FirestoreModel.class);
                            if (model != null) {
                                // Debug logging for Firestore data
                                Map<String, FirestoreModel.Section> sections = model.getSections();
                                for (Map.Entry<String, FirestoreModel.Section> entry : sections.entrySet()) {
                                    String sectionName = entry.getKey();
                                    FirestoreModel.Section section = entry.getValue();
                                }

                                // Set up click listeners for clickable words
                                setupClickListeners(model);
                                adapter = new ExpandableAdapter(getContext(), model);
                                recyclerView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getContext(), "Failed to parse Firestore data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error getting document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupClickListeners(FirestoreModel model) {
        Map<String, FirestoreModel.Section> sections = model.getSections();
        for (FirestoreModel.Section section : sections.values()) {
            if (section.getClickableWords() != null) {
                for (FirestoreModel.ClickableWord cw : section.getClickableWords()) {
                    cw.setOnClickListener(v -> handleClickableWordAction(cw.getActionType(), cw.getActionValue()));
                }
            }
        }
    }

    private void handleClickableWordAction(String actionType, String actionValue) {
        switch (actionType) {
            case "download":
                FileUtils.copyFileFromAssets(getContext(), actionValue);
                break;
            case "map":
                String[] coords = actionValue.split(",");
                if (coords.length == 2) {
                    double lat = Double.parseDouble(coords[0]);
                    double lng = Double.parseDouble(coords[1]);
                    FileUtils.openGoogleMaps(getContext(), lat, lng, "");
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Access the MainActivity
        Activity_main mainActivity = (Activity_main) requireActivity();
        // Hide the bottom app bar
        mainActivity.hideBottomAppBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Access the MainActivity
        Activity_main mainActivity = (Activity_main) requireActivity();
        // Show the bottom app bar
        mainActivity.showBottomAppBar();
    }

}