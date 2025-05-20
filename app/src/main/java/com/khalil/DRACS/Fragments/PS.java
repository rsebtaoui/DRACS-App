package com.khalil.DRACS.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.khalil.DRACS.Utils.ConnectionUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.khalil.DRACS.Adapters.ExpandableAdapter;
import com.khalil.DRACS.Adapters.ShimmerAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khalil.DRACS.Adapters.ExpandableAdapter;
import com.khalil.DRACS.Avtivities.Activity_main;
import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.FileUtils;
import com.khalil.DRACS.Utils.PersistentDataUtils;
import com.khalil.DRACS.Utils.CrashlyticsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PS extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView shimmerRecyclerView;
    private ExpandableAdapter adapter;
    private FirebaseFirestore db;
    private ShimmerFrameLayout shimmerContainer;
    private String targetSectionId = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get section ID from arguments
        if (getArguments() != null) {
            targetSectionId = getArguments().getString("target_section_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Check for persistent data
        SharedPreferences prefs = requireActivity().getSharedPreferences("DRACS_Prefs", MODE_PRIVATE);
        boolean hasPersistentData = prefs.getBoolean("has_persistent_data", false);

        // If no persistent data and no internet connection, show error and stay in home
        if (!hasPersistentData && !ConnectionUtils.isNetworkAvailable(requireContext())) {
            // Navigate back to home
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_PS_to_home);
            return null; // Don't inflate this fragment
        }

        View view = inflater.inflate(R.layout.fragment_p_s, container, false);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.psrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecyclerView = view.findViewById(R.id.shimmerRecyclerView);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerContainer = view.findViewById(R.id.shimmerContainer);

        // Get target section ID from arguments if available
        if (getArguments() != null) {
            targetSectionId = getArguments().getString("target_section_id");
        }
        
        // Set up shimmer adapter
        shimmerRecyclerView.setAdapter(new ShimmerAdapter());

        // Show shimmer effect initially
        shimmerContainer.startShimmer();
        recyclerView.setVisibility(View.GONE);
        shimmerContainer.setVisibility(View.VISIBLE);

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
        // Try to get data from pre-fetcher first
        final FirestoreModel model = ((Activity_main) requireActivity()).getDataPreFetcher().getCachedData("ps");
        if (model != null) {
            // Use pre-fetched data
            Map<String, FirestoreModel.Section> sections = model.getSections();
            setupClickListeners(sections);
            adapter = new ExpandableAdapter(getContext(), sections);
            recyclerView.setAdapter(adapter);
            
            // Hide shimmer and show content
            shimmerContainer.stopShimmer();
            shimmerContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Expand target section if we have one
            if (targetSectionId != null) {
                expandTargetSection(sections);
            }
            return;
        }

        // Fallback to direct Firestore fetch if pre-fetched data is not available
        try {
            db.collection("pages").document("ps")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                final FirestoreModel finalModel = document.toObject(FirestoreModel.class);
                                if (finalModel != null) {
                                    // Cache the data for future use
                                    ((Activity_main) requireActivity()).getDataPreFetcher().cacheData("ps", finalModel);
                                    
                                    // Set up UI
                                    Map<String, FirestoreModel.Section> sections = finalModel.getSections();
                                    setupClickListeners(sections);
                                    adapter = new ExpandableAdapter(getContext(), sections);
                                    recyclerView.setAdapter(adapter);
                                    
                                    // Hide shimmer and show content
                                    shimmerContainer.stopShimmer();
                                    shimmerContainer.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);

                                    // If we have a target section ID from search, expand that section after UI is ready
                                    if (targetSectionId != null) {
                                        expandTargetSection(sections);
                                    }

                                    // Mark that we have persistent data
                                    PersistentDataUtils.setHasPersistentData(getContext(), true);
                                } else {
                                    CrashlyticsUtils.logError(new Exception("FirestoreModel is null"), "Failed to parse PS data");
                                    Toast.makeText(getContext(), "Error loading content", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                CrashlyticsUtils.logError(new Exception("Document does not exist"), "PS document not found in Firestore");
                                Toast.makeText(getContext(), "Content not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                CrashlyticsUtils.handleNetworkException(e, getContext(), "initializing PS data fetch");
                            }
                        }
                    });
        } catch (Exception e) {
            CrashlyticsUtils.handleNetworkException(e, getContext(), "initializing PS data fetch");
        }
    }

    private void setupClickListeners(Map<String, FirestoreModel.Section> sections) {
        for (FirestoreModel.Section section : sections.values()) {
            if (section.getClickableWords() != null) {
                for (FirestoreModel.ClickableWord cw : section.getClickableWords()) {
                    if (cw.getActionType().equals("download")) {
                        cw.setOnClickListener(v -> {
                            String filePath = cw.getActionValue();
                            FileUtils.showDownloadNotification(getContext(), Uri.parse(filePath));
                        });
                    } else if (cw.getActionType().equals("map")) {
                        cw.setOnClickListener(v -> {
                            String[] coords = cw.getActionValue().split(",");
                            if (coords.length == 2) {
                                double lat = Double.parseDouble(coords[0]);
                                double lng = Double.parseDouble(coords[1]);
                                FileUtils.openGoogleMaps(getContext(), lat, lng, "");
                            }
                        });
                    } else if (cw.getActionType().equals("fragment")) {
                        cw.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
                            navController.navigate(Integer.parseInt(cw.getActionValue()));
                        });
                    }
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
        // Access the MainActivity and hide the bottom app bar
        Activity_main mainActivity = (Activity_main) requireActivity();
        mainActivity.hideBottomAppBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Access the MainActivity and show the bottom app bar
        Activity_main mainActivity = (Activity_main) requireActivity();
        mainActivity.showBottomAppBar();
    }

    private void refreshContent() {
        // Show shimmer effect while refreshing
        shimmerContainer.startShimmer();
        shimmerContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Clear the cached data
        ((Activity_main) requireActivity()).getDataPreFetcher().clearCache("ps");

        try {
            // Fetch fresh data from Firestore
            db.collection("pages").document("ps")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                final FirestoreModel finalModel = document.toObject(FirestoreModel.class);
                                if (finalModel != null) {
                                    // Cache the new data
                                    ((Activity_main) requireActivity()).getDataPreFetcher().cacheData("ps", finalModel);
                                    
                                    // Set up UI with new data
                                    Map<String, FirestoreModel.Section> sections = finalModel.getSections();
                                    setupClickListeners(sections);
                                    adapter = new ExpandableAdapter(getContext(), sections);
                                    recyclerView.setAdapter(adapter);
                                    
                                    // Hide shimmer and show content
                                    shimmerContainer.stopShimmer();
                                    shimmerContainer.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);

                                    // If we have a target section ID, expand it
                                    if (targetSectionId != null) {
                                        recyclerView.post(() -> {
                                            adapter.expandSection(targetSectionId);
                                            // Find the position of the section to scroll to it
                                            int position = 0;
                                            for (Map.Entry<String, FirestoreModel.Section> entry : sections.entrySet()) {
                                                if (entry.getKey().equals(targetSectionId)) {
                                                    recyclerView.scrollToPosition(position);
                                                    break;
                                                }
                                                position++;
                                            }
                                        });
                                    }
                                } else {
                                    CrashlyticsUtils.logError(new Exception("FirestoreModel is null"), "Failed to parse PS data during refresh");
                                    Toast.makeText(getContext(), "Error refreshing content", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                CrashlyticsUtils.logError(new Exception("Document does not exist"), "PS document not found in Firestore during refresh");
                                Toast.makeText(getContext(), "Content not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                CrashlyticsUtils.handleNetworkException(e, getContext(), "refreshing PS data");
                            }
                        }
                        
                        // Stop the refresh animation
                        swipeRefreshLayout.setRefreshing(false);
                    });
        } catch (Exception e) {
            CrashlyticsUtils.handleNetworkException(e, getContext(), "initializing PS data refresh");
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void expandTargetSection(Map<String, FirestoreModel.Section> sections) {
        // First attempt - immediate
        tryExpandSection(sections, 0);

        // Second attempt - after a short delay
        recyclerView.postDelayed(() -> tryExpandSection(sections, 1), 300);

        // Third attempt - after a longer delay
        recyclerView.postDelayed(() -> tryExpandSection(sections, 2), 800);
    }

    private void tryExpandSection(Map<String, FirestoreModel.Section> sections, int attempt) {
        try {
            if (adapter != null && targetSectionId != null) {
                adapter.expandSection(targetSectionId);
                // Find the position of the section to scroll to it
                int position = 0;
                for (Map.Entry<String, FirestoreModel.Section> entry : sections.entrySet()) {
                    if (entry.getKey().equals(targetSectionId)) {
                        recyclerView.scrollToPosition(position);
                        break;
                    }
                    position++;
                }
            }
        } catch (Exception e) {
            // Log error but don't crash
            e.printStackTrace();
        }
    }

}