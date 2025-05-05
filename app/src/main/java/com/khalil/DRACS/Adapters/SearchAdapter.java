package com.khalil.DRACS.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.khalil.DRACS.Models.SearchResult;
import com.khalil.DRACS.R;

import java.util.List;

public class SearchAdapter extends ArrayAdapter<SearchResult> {
    private final List<SearchResult> searchResults;

    public SearchAdapter(List<SearchResult> searchResults) {
        super(null, 0, searchResults);
        this.searchResults = searchResults;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_search_result, parent, false);
        }

        SearchResult result = searchResults.get(position);
        
        TextView title = convertView.findViewById(R.id.search_result_title);
        TextView snippet = convertView.findViewById(R.id.search_result_snippet);

        title.setText(result.getTitle());
        snippet.setText(result.getSnippet());

        return convertView;
    }
}
