package com.khalil.DRACS.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.khalil.DRACS.Repository.ContentRepository;

public class ContentViewModelFactory implements ViewModelProvider.Factory {

    private final ContentRepository repository;
    private final String pageId;

    public ContentViewModelFactory(ContentRepository repository, String pageId) {
        this.repository = repository;
        this.pageId = pageId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ContentViewModel.class)) {
            return (T) new ContentViewModel(repository, pageId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
