package com.khalil.DRACS.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khalil.DRACS.Adapters.ExpandableAdapter;
import com.khalil.DRACS.Adapters.ShimmerAdapter;
import com.khalil.DRACS.Avtivities.Activity_main;
import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.FileUtils;
import com.khalil.DRACS.Utils.ConnectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FP extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView shimmerRecyclerView;
    private ExpandableAdapter adapter;
    private FirebaseFirestore db;
    private ShimmerFrameLayout shimmerContainer;
    private String targetSectionId = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Check for persistent data
        SharedPreferences prefs = requireActivity().getSharedPreferences("DRACS_Prefs", MODE_PRIVATE);
        boolean hasPersistentData = prefs.getBoolean("has_persistent_data", false);

        // If no persistent data and no internet connection, show error and stay in home
        if (!hasPersistentData && !ConnectionUtils.isNetworkAvailable(requireContext())) {
            // Show error toast
            Toast.makeText(requireContext(), "تحتاج إلى اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            // Navigate back to home
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_FP_to_home);
            return null; // Don't inflate this fragment
        }

        View view = inflater.inflate(R.layout.fragment_f_p, container, false);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.fprecyclerView);
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
                        navController.navigate(R.id.action_FP_to_home);
                    }
                }
        );

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get section ID from arguments
        if (getArguments() != null) {
            targetSectionId = getArguments().getString("target_section_id");
        }
    }

    private void fetchDataFromFirestore() {
        // Try to get data from pre-fetcher first
        FirestoreModel model = ((Activity_main) requireActivity()).getDataPreFetcher().getCachedData("fp");
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
            return;
        }

        // Fallback to direct Firestore fetch if pre-fetched data is not available
        db.collection("pages").document("fp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            final FirestoreModel finalModel = document.toObject(FirestoreModel.class);
                            if (finalModel != null) {
                                // Cache the data for future use
                                ((Activity_main) requireActivity()).getDataPreFetcher().cacheData("fp", finalModel);
                                
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
                            }
                        }
                    }
                });
    }

    private void setupClickListeners(Map<String, FirestoreModel.Section> sections) {
        for (FirestoreModel.Section section : sections.values()) {
            if (section.getClickableWords() != null) {
                for (FirestoreModel.ClickableWord cw : section.getClickableWords()) {
                    if (cw.getActionType().equals("file")) {
                        cw.setOnClickListener(v -> {
                            String filePath = cw.getActionValue();
                            FileUtils.showDownloadNotification(getContext(), Uri.parse(filePath));
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
        // Check for internet connection first
        if (!ConnectionUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "تحتاج إلى اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        // Show shimmer effect while refreshing
        shimmerContainer.startShimmer();
        shimmerContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Clear the cached data
        ((Activity_main) requireActivity()).getDataPreFetcher().clearCache("fp");

        // Fetch fresh data from Firestore
        db.collection("pages").document("fp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            final FirestoreModel finalModel = document.toObject(FirestoreModel.class);
                            if (finalModel != null) {
                                // Cache the new data
                                ((Activity_main) requireActivity()).getDataPreFetcher().cacheData("fp", finalModel);
                                
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
                            }
                        }
                    } else {
                        // Show error toast if refresh fails
                        Toast.makeText(getContext(), "فشل تحديث المحتوى", Toast.LENGTH_SHORT).show();
                    }
                    
                    // Stop the refresh animation
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

}