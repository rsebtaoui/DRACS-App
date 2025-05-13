package com.khalil.DRACS.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.activity.OnBackPressedCallback;

import com.khalil.DRACS.Avtivities.Activity_main;
import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.Models.SearchResult;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Adapters.SearchAdapter;
import com.khalil.DRACS.Utils.DataPreFetcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import me.ibrahimsn.lib.SmoothBottomBar;

public class SearchFragment extends Fragment {
    // Page IDs that can be searched
    private static final String[] PAGE_IDS = {"ps", "fda", "fp", "je", "rna"};
    
    private EditText searchInput;
    private ListView searchResultsList;
    private SearchAdapter searchAdapter;
    private List<SearchResult> searchResults;
    private NavController navController;
    private View loadingView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                          @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        
        // Hide bottom app bar when search is active
        Activity_main mainActivity = (Activity_main) requireActivity();
        mainActivity.hideBottomAppBar();

        searchInput = view.findViewById(R.id.search_input);
        searchResultsList = view.findViewById(R.id.search_results_list);
        loadingView = view.findViewById(R.id.loading_view);
        searchResults = new ArrayList<>();
        searchAdapter = new SearchAdapter(requireContext(), searchResults, this::onSearchResultClick);
        searchResultsList.setAdapter(searchAdapter);

        // Show loading state initially
        loadingView.setVisibility(View.VISIBLE);
        searchResultsList.setVisibility(View.GONE);

        // Ensure data is loaded before allowing search
        DataPreFetcher dataPreFetcher = ((Activity_main) requireActivity()).getDataPreFetcher();
        if (!isDataLoaded(dataPreFetcher)) {
            dataPreFetcher.startPreFetching(success -> {
                if (success) {
                    requireActivity().runOnUiThread(() -> {
                        loadingView.setVisibility(View.GONE);
                        searchResultsList.setVisibility(View.VISIBLE);
                        // If there's text in the search input, perform the search
                        if (searchInput.getText().length() >= 2) {
                            performSearch(searchInput.getText().toString());
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        loadingView.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            loadingView.setVisibility(View.GONE);
            searchResultsList.setVisibility(View.VISIBLE);
        }

        navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);

        // Add back press handler
        requireActivity().getOnBackPressedDispatcher().addCallback(
            getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Navigate back to home
                    navController.navigate(R.id.action_Search_to_home);
                    
                    // Update bottom app bar selection
                    Activity_main mainActivity = (Activity_main) requireActivity();
                    mainActivity.updateBottomBarSelection(R.id.home);
                }
            }
        );

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    performSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show bottom app bar when leaving search
        Activity_main mainActivity = (Activity_main) requireActivity();
        mainActivity.showBottomAppBar();
    }

    private void performSearch(String query) {
        searchResults.clear();
        
        // Get the DataPreFetcher from MainActivity
        DataPreFetcher dataPreFetcher = ((Activity_main) requireActivity()).getDataPreFetcher();
        
        // Search through all pages
        for (String pageId : PAGE_IDS) {
            FirestoreModel model = dataPreFetcher.getCachedData(pageId);
            if (model != null) {
                Map<String, FirestoreModel.Section> sections = model.getSections();
                
                // Search through sections by ID
                for (Map.Entry<String, FirestoreModel.Section> entry : sections.entrySet()) {
                    String sectionId = entry.getKey();
                    FirestoreModel.Section section = entry.getValue();
                    
                    if (matchesQuery(section.getTitle(), query) || 
                        matchesQuery(section.getIntroduction(), query) || 
                        matchesQuery(section.getConclusion(), query) ||
                        matchesQueryInDashes(section.getDashes(), query)) {
                        
                        String snippet = getSnippet(section, query);
                        Class<?> targetFragment = getTargetFragment(pageId);
                        SearchResult result = new SearchResult(
                            section.getTitle(),
                            snippet,
                            targetFragment
                        );
                        result.setTargetSection(sectionId);
                        
                        searchResults.add(result);
                    }
                }
            }
        }
        
        searchAdapter.notifyDataSetChanged();
    }

    private boolean matchesQuery(String text, String query) {
        if (text == null || query == null) return false;
        return text.toLowerCase().contains(query.toLowerCase());
    }

    private boolean matchesQueryInDashes(List<String> dashes, String query) {
        if (dashes == null || query == null) return false;
        for (String dash : dashes) {
            if (matchesQuery(dash, query)) {
                return true;
            }
        }
        return false;
    }

    private String getSnippet(FirestoreModel.Section section, String query) {
        // Create a snippet showing where the match was found
        StringBuilder snippet = new StringBuilder();
        
        // Check introduction
        if (matchesQuery(section.getIntroduction(), query)) {
            snippet.append(" ").append(getHighlightedText(section.getIntroduction(), query));
        }
        
        // Check dashes
        if (section.getDashes() != null) {
            for (String dash : section.getDashes()) {
                if (matchesQuery(dash, query)) {
                    if (snippet.length() > 0) snippet.append("\n");
                    snippet.append(" ").append(getHighlightedText(dash, query));
                }
            }
        }
        
        // Check conclusion
        if (matchesQuery(section.getConclusion(), query)) {
            if (snippet.length() > 0) snippet.append("\n");
            snippet.append(" ").append(getHighlightedText(section.getConclusion(), query));
        }
        
        // If no match in intro, dashes, or conclusion, check title
        if (snippet.length() == 0 && matchesQuery(section.getTitle(), query)) {
            snippet.append(" ").append(getHighlightedText(section.getTitle(), query));
        }
        
        return snippet.toString();
    }

    private String getHighlightedText(String text, String query) {
        if (text == null || query == null) return "";
        int index = text.toLowerCase().indexOf(query.toLowerCase());
        if (index == -1) return text;
        
        // Show 20 characters before and after the match
        int start = Math.max(0, index - 20);
        int end = Math.min(text.length(), index + query.length() + 20);
        
        return text.substring(start, end);
    }

    private Class<?> getTargetFragment(String pageId) {
        switch (pageId) {
            case "ps": return PS.class;
            case "fda": return FDA.class;
            case "fp": return FP.class;
            case "je": return JE.class;
            case "rna": return RNA.class;
            default: return null;
        }
    }

    public void onSearchResultClick(SearchResult result) {
        // Get the target fragment and section ID
        Class<?> targetFragmentClass = result.getTargetFragment();
        String targetSectionId = result.getTargetSection();
        
        // Create a bundle to pass the section ID
        Bundle args = new Bundle();
        args.putString("target_section_id", targetSectionId);
        
        // Get the NavController and MainActivity
        NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
        Activity_main mainActivity = (Activity_main) requireActivity();
        
        // Force hide bottom app bar
        mainActivity.hideBottomAppBar();
        
        // Map the fragment class to the correct action ID
        int actionId;
        final int destinationId;
        
        if (targetFragmentClass == PS.class) {
            actionId = R.id.action_Search_to_PS;
            destinationId = R.id.PS;
        } else if (targetFragmentClass == FDA.class) {
            actionId = R.id.action_Search_to_FDA;
            destinationId = R.id.FDA;
        } else if (targetFragmentClass == FP.class) {
            actionId = R.id.action_Search_to_FP;
            destinationId = R.id.FP;
        } else if (targetFragmentClass == JE.class) {
            actionId = R.id.action_Search_to_JE;
            destinationId = R.id.JE;
        } else if (targetFragmentClass == RNA.class) {
            actionId = R.id.action_Search_to_RNA;
            destinationId = R.id.RNA;
        } else {
            return; // Invalid fragment class
        }
        
        // Ensure correct bottom bar visibility for target destination
        mainActivity.updateBottomBarVisibilityForDestination(destinationId);
        
        // Navigate
        navController.navigate(actionId, args);
        
        // Post additional visibility updates with delays to ensure it applies after navigation
        final View view = requireView(); // Capture view in final variable
        view.postDelayed(() -> mainActivity.updateBottomBarVisibilityForDestination(destinationId), 100);
        view.postDelayed(() -> mainActivity.updateBottomBarVisibilityForDestination(destinationId), 300);
        view.postDelayed(() -> mainActivity.updateBottomBarVisibilityForDestination(destinationId), 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show bottom app bar when returning to search
        Activity_main mainActivity = (Activity_main) requireActivity();
        mainActivity.showBottomAppBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Hide bottom app bar when leaving search
        Activity_main mainActivity = (Activity_main) requireActivity();
        mainActivity.hideBottomAppBar();
    }

    private boolean isDataLoaded(DataPreFetcher dataPreFetcher) {
        for (String pageId : PAGE_IDS) {
            if (!dataPreFetcher.hasCachedData(pageId)) {
                return false;
            }
        }
        return true;
    }
}
