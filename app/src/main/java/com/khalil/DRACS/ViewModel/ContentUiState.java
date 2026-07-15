package com.khalil.DRACS.ViewModel;

import androidx.annotation.Nullable;

import com.khalil.DRACS.Models.FirestoreModel;

public class ContentUiState {

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    @Nullable
    private final FirestoreModel model;
    @Nullable
    private final String errorMessage;
    private final boolean isRefresh;

    private ContentUiState(Status status, @Nullable FirestoreModel model,
                           @Nullable String errorMessage, boolean isRefresh) {
        this.status = status;
        this.model = model;
        this.errorMessage = errorMessage;
        this.isRefresh = isRefresh;
    }

    public static ContentUiState loading() {
        return new ContentUiState(Status.LOADING, null, null, false);
    }

    public static ContentUiState loading(boolean isRefresh) {
        return new ContentUiState(Status.LOADING, null, null, isRefresh);
    }

    public static ContentUiState success(FirestoreModel model) {
        return new ContentUiState(Status.SUCCESS, model, null, false);
    }

    public static ContentUiState success(FirestoreModel model, boolean isRefresh) {
        return new ContentUiState(Status.SUCCESS, model, null, isRefresh);
    }

    public static ContentUiState error(String message, boolean isRefresh) {
        return new ContentUiState(Status.ERROR, null, message, isRefresh);
    }

    public Status getStatus() {
        return status;
    }

    @Nullable
    public FirestoreModel getModel() {
        return model;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isRefresh() {
        return isRefresh;
    }
}
