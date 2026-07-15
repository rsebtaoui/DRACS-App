package com.khalil.DRACS.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.khalil.DRACS.Database.FavoriteSection;
import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.Repository.ContentCallback;
import com.khalil.DRACS.Repository.ContentRepository;

import java.util.List;

/**
 * Survives configuration changes. Holds no Context references.
 */
public class ContentViewModel extends ViewModel {

    private final ContentRepository repository;
    private final String pageId;
    private final MutableLiveData<ContentUiState> uiState =
            new MutableLiveData<>(ContentUiState.loading());
    private boolean hasTriggeredPrefetch;

    public ContentViewModel(ContentRepository repository, String pageId) {
        this.repository = repository;
        this.pageId = pageId;
    }

    public LiveData<ContentUiState> getUiState() {
        return uiState;
    }

    public LiveData<List<FavoriteSection>> getFavorites() {
        return repository.observeFavoritesForPage(pageId);
    }

    public void loadPage() {
        uiState.setValue(ContentUiState.loading());
        repository.getPage(pageId, createCallback(false));
    }

    public void refreshPage() {
        uiState.setValue(ContentUiState.loading(true));
        repository.refreshPage(pageId, createCallback(true));
    }

    public void toggleFavorite(String sectionId, String title) {
        repository.toggleFavorite(pageId, sectionId, title);
    }

    public void triggerFullPrefetchIfNeeded() {
        if (hasTriggeredPrefetch || repository.hasPersistentData()) {
            return;
        }
        hasTriggeredPrefetch = true;
        repository.startFullPrefetch(success -> {
            if (success) {
                repository.markPersistentDataAvailable();
            }
        });
    }

    public boolean hasPersistentData() {
        return repository.hasPersistentData();
    }

    private ContentCallback createCallback(boolean isRefresh) {
        return new ContentCallback() {
            @Override
            public void onSuccess(FirestoreModel model) {
                uiState.postValue(ContentUiState.success(model, isRefresh));
            }

            @Override
            public void onError(String message) {
                uiState.postValue(ContentUiState.error(message, isRefresh));
            }
        };
    }
}
