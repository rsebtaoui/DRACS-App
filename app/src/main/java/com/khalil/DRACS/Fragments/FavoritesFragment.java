package com.khalil.DRACS.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khalil.DRACS.Activities.Activity_main;
import com.khalil.DRACS.Adapters.FavoritesAdapter;
import com.khalil.DRACS.Database.FavoriteSection;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Repository.ContentRepository;

public class FavoritesFragment extends Fragment {

    private LinearLayout emptyState;
    private LinearLayout contentState;
    private TextView countView;
    private FavoritesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyState = view.findViewById(R.id.favorites_empty);
        contentState = view.findViewById(R.id.favorites_content);
        countView = view.findViewById(R.id.favorites_count);
        RecyclerView recyclerView = view.findViewById(R.id.favorites_recycler);

        adapter = new FavoritesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnFavoriteClickListener(this::openFavorite);

        ContentRepository repository = ((Activity_main) requireActivity()).getContentRepository();
        repository.observeAllFavorites().observe(getViewLifecycleOwner(), favorites -> {
            boolean hasItems = favorites != null && !favorites.isEmpty();
            emptyState.setVisibility(hasItems ? View.GONE : View.VISIBLE);
            contentState.setVisibility(hasItems ? View.VISIBLE : View.GONE);
            if (hasItems) {
                countView.setText(getString(R.string.favorites_count_format, favorites.size()));
                adapter.submitList(favorites);
            }
        });
    }

    private void openFavorite(FavoriteSection favorite) {
        int destinationId;
        switch (favorite.pageId) {
            case "ps":
                destinationId = R.id.PS;
                break;
            case "fda":
                destinationId = R.id.FDA;
                break;
            case "je":
                destinationId = R.id.JE;
                break;
            case "fp":
                destinationId = R.id.FP;
                break;
            case "rna":
            default:
                destinationId = R.id.RNA;
                break;
        }

        Bundle args = new Bundle();
        args.putString("target_section_id", favorite.sectionId);
        NavHostFragment.findNavController(this).navigate(destinationId, args);
    }
}
