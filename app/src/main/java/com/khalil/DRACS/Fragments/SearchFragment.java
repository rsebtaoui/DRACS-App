package com.khalil.DRACS.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khalil.DRACS.Models.SearchResult;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText searchInput;
    private ListView searchResultsList;
    private SearchAdapter searchAdapter;
    private List<SearchResult> searchResults;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                          @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        
        searchInput = view.findViewById(R.id.search_input);
        searchResultsList = view.findViewById(R.id.search_results_list);
        searchResults = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchResults);
        searchResultsList.setAdapter(searchAdapter);

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

    private void performSearch(String query) {
        searchResults.clear();
        
        // Add your search logic here
        // This is a placeholder - you'll need to implement the actual search across your app's content
        searchResults.add(new SearchResult("PS Fragment", "Found in section 1", PS.class));
        searchResults.add(new SearchResult("RNA Fragment", "Found in section 2", RNA.class));
        
        searchAdapter.notifyDataSetChanged();
    }
}
