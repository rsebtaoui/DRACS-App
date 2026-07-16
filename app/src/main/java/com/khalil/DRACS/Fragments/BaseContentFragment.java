package com.khalil.DRACS.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.khalil.DRACS.Activities.Activity_main;
import com.khalil.DRACS.Adapters.ExpandableAdapter;
import com.khalil.DRACS.Adapters.ShimmerAdapter;
import com.khalil.DRACS.Database.FavoriteSection;
import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.ConnectionUtils;
import com.khalil.DRACS.Utils.FileUtils;
import com.khalil.DRACS.ViewModel.ContentUiState;
import com.khalil.DRACS.ViewModel.ContentViewModel;
import com.khalil.DRACS.ViewModel.ContentViewModelFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shared base for Firestore-driven content pages (RNA, PS, FDA, JE, FP).
 * UI state is owned by {@link ContentViewModel} and survives configuration changes.
 */
public abstract class BaseContentFragment extends Fragment {

    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String ARG_TARGET_SECTION = "target_section_id";

    private RecyclerView recyclerView;
    private RecyclerView shimmerRecyclerView;
    private ShimmerFrameLayout shimmerContainer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableAdapter adapter;
    private ContentViewModel viewModel;

    @Nullable
    private String targetSectionId;
    @Nullable
    private Map<String, FirestoreModel.Section> lastSections;

    private final Runnable expandAttemptDelayedShort = () -> tryExpandTargetSection(1);
    private final Runnable expandAttemptDelayedLong = () -> tryExpandTargetSection(2);

    protected abstract String getPageId();

    protected abstract int getHomeActionId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetSectionId = getArguments().getString(ARG_TARGET_SECTION);
        }

        Activity_main activity = (Activity_main) requireActivity();
        viewModel = new ViewModelProvider(
                this,
                new ContentViewModelFactory(activity.getContentRepository(), getPageId())
        ).get(ContentViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (!viewModel.hasPersistentData()
                && !ConnectionUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "تحتاج إلى اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            View offlineView = new FrameLayout(requireContext());
            offlineView.post(() -> NavHostFragment.findNavController(this)
                    .navigate(getHomeActionId()));
            return offlineView;
        }

        View view = inflater.inflate(R.layout.fragment_content_page, container, false);
        bindViews(view);
        setupRecyclerViews();
        setupSwipeRefresh();
        setupBackPressHandler();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
        ContentUiState currentState = viewModel.getUiState().getValue();
        if (currentState == null || currentState.getStatus() == ContentUiState.Status.LOADING) {
            showLoading();
            viewModel.loadPage();
        } else if (currentState.getStatus() == ContentUiState.Status.SUCCESS
                && currentState.getModel() != null) {
            displayContent(currentState.getModel());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((Activity_main) requireActivity()).hideBottomAppBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((Activity_main) requireActivity()).showBottomAppBar();
    }

    @Override
    public void onDestroyView() {
        cancelExpandRetries();
        recyclerView = null;
        shimmerRecyclerView = null;
        shimmerContainer = null;
        swipeRefreshLayout = null;
        adapter = null;
        super.onDestroyView();
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null || !isViewAlive()) {
                return;
            }
            switch (state.getStatus()) {
                case LOADING:
                    if (state.isRefresh() || adapter == null) {
                        showLoading();
                    }
                    break;
                case SUCCESS:
                    if (state.getModel() != null) {
                        displayContent(state.getModel());
                        if (!state.isRefresh()) {
                            viewModel.triggerFullPrefetchIfNeeded();
                        }
                    }
                    stopRefreshing();
                    break;
                case ERROR:
                    if (state.isRefresh()) {
                        Toast.makeText(requireContext(), state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else if (shimmerContainer != null) {
                        shimmerContainer.stopShimmer();
                        shimmerContainer.setVisibility(View.GONE);
                    }
                    stopRefreshing();
                    break;
                default:
                    break;
            }
        });

        viewModel.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
            if (adapter != null) {
                adapter.updateFavoriteSectionIds(toFavoriteIdSet(favorites));
            }
        });
    }

    private Set<String> toFavoriteIdSet(@Nullable List<FavoriteSection> favorites) {
        Set<String> ids = new HashSet<>();
        if (favorites != null) {
            for (FavoriteSection favorite : favorites) {
                ids.add(favorite.sectionId);
            }
        }
        return ids;
    }

    private void bindViews(@NonNull View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        shimmerContainer = view.findViewById(R.id.shimmerContainer);
        shimmerRecyclerView = view.findViewById(R.id.shimmerRecyclerView);
        recyclerView = view.findViewById(R.id.contentRecyclerView);
    }

    private void setupRecyclerViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        shimmerRecyclerView.setAdapter(new ShimmerAdapter());
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshPage());
    }

    private void setupBackPressHandler() {
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NavHostFragment.findNavController(BaseContentFragment.this)
                                .navigate(getHomeActionId());
                    }
                }
        );
    }

    private void displayContent(@NonNull FirestoreModel model) {
        Map<String, FirestoreModel.Section> sections = model.getSections();
        lastSections = sections;
        setupClickListeners(sections);

        adapter = new ExpandableAdapter(requireContext(), sections);
        adapter.setOnBookmarkClickListener((sectionId, sectionTitle) ->
                viewModel.toggleFavorite(sectionId, sectionTitle));
        recyclerView.setAdapter(adapter);
        adapter.updateFavoriteSectionIds(toFavoriteIdSet(viewModel.getFavorites().getValue()));
        showContent();

        if (targetSectionId != null) {
            scheduleTargetSectionExpansion();
        }
    }

    private void showLoading() {
        if (!isViewAlive()) {
            return;
        }
        shimmerContainer.startShimmer();
        shimmerContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showContent() {
        if (!isViewAlive()) {
            return;
        }
        shimmerContainer.stopShimmer();
        shimmerContainer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void scheduleTargetSectionExpansion() {
        cancelExpandRetries();
        tryExpandTargetSection(0);
        recyclerView.postDelayed(expandAttemptDelayedShort, 300);
        recyclerView.postDelayed(expandAttemptDelayedLong, 800);
    }

    private void tryExpandTargetSection(int attempt) {
        if (!isViewAlive() || adapter == null || targetSectionId == null || lastSections == null) {
            return;
        }
        try {
            adapter.expandSection(targetSectionId);
            List<Map.Entry<String, FirestoreModel.Section>> sortedSections =
                    new ArrayList<>(lastSections.entrySet());
            sortedSections.sort(Comparator.comparingInt(entry -> entry.getValue().getOrder()));
            for (int position = 0; position < sortedSections.size(); position++) {
                if (sortedSections.get(position).getKey().equals(targetSectionId)) {
                    recyclerView.scrollToPosition(position);
                    break;
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            FirebaseCrashlytics.getInstance().log(
                    "Error expanding section in " + getClass().getSimpleName() + ". Attempt: " + attempt);
        }
    }

    private void cancelExpandRetries() {
        if (recyclerView != null) {
            recyclerView.removeCallbacks(expandAttemptDelayedShort);
            recyclerView.removeCallbacks(expandAttemptDelayedLong);
        }
    }

    private void setupClickListeners(Map<String, FirestoreModel.Section> sections) {
        for (FirestoreModel.Section section : sections.values()) {
            for (FirestoreModel.ClickableWord clickableWord : section.getClickableWords()) {
                String actionType = clickableWord.getActionType();
                if ("download".equals(actionType)) {
                    clickableWord.setOnClickListener(v ->
                            FileUtils.copyFileFromAssets(requireActivity(), clickableWord.getActionValue()));
                } else if ("map".equals(actionType)) {
                    clickableWord.setOnClickListener(v -> openMapAction(clickableWord.getActionValue()));
                } else if ("web".equals(actionType)) {
                    clickableWord.setOnClickListener(v ->
                            FileUtils.handleWebAction(requireContext(), clickableWord.getActionValue()));
                }
            }
        }
    }

    private void openMapAction(String actionValue) {
        String[] coords = actionValue.split(",");
        if (coords.length != 2) {
            return;
        }
        try {
            double lat = Double.parseDouble(coords[0].trim());
            double lng = Double.parseDouble(coords[1].trim());
            FileUtils.openGoogleMaps(requireContext(), lat, lng, "");
        } catch (NumberFormatException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private boolean isViewAlive() {
        return getView() != null && isAdded();
    }
}
