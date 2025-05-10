package com.khalil.DRACS.Utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.khalil.DRACS.Models.FirestoreModel;

import java.util.HashMap;
import java.util.Map;

public class DataPreFetcher {
    private static final String TAG = "DataPreFetcher";
    private static final String[] PAGE_IDS = {"ps", "fda", "fp", "je", "rna"};
    private final Context context;
    private final FirebaseFirestore db;
    private final Map<String, FirestoreModel> cachedData;
    private int completedRequests;
    private OnPreFetchCompleteListener listener;

    public interface OnPreFetchCompleteListener {
        void onPreFetchComplete(boolean success);
    }

    public DataPreFetcher(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        try {
            this.db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase: " + e.getMessage());
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
        this.cachedData = new HashMap<>();
        this.completedRequests = 0;
    }

    public void startPreFetching(OnPreFetchCompleteListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        this.listener = listener;
        for (String pageId : PAGE_IDS) {
            fetchPageData(pageId);
        }
    }

    private void fetchPageData(String pageId) {
        if (pageId == null || pageId.isEmpty()) {
            Log.e(TAG, "Invalid pageId provided");
            checkCompletion();
            return;
        }

        db.collection("pages").document(pageId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                FirestoreModel model = document.toObject(FirestoreModel.class);
                                if (model != null) {
                                    cachedData.put(pageId, model);
                                    Log.d(TAG, "Successfully cached page: " + pageId);
                                } else {
                                    Log.e(TAG, "Failed to convert document to FirestoreModel for page: " + pageId);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting document for page " + pageId + ": " + e.getMessage());
                            }
                        } else {
                            Log.e(TAG, "Document does not exist for page: " + pageId);
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseFirestoreException) {
                            Log.e(TAG, "Firestore error fetching page " + pageId + ": " + exception.getMessage());
                        } else {
                            Log.e(TAG, "Error fetching page " + pageId + ": " + exception.getMessage());
                        }
                    }
                    checkCompletion();
                });
    }

    private void checkCompletion() {
        completedRequests++;
        if (completedRequests >= PAGE_IDS.length) {
            if (listener != null) {
                boolean success = !cachedData.isEmpty();
                listener.onPreFetchComplete(success);
            }
        }
    }

    public FirestoreModel getCachedData(String pageId) {
        if (pageId == null || pageId.isEmpty()) {
            Log.e(TAG, "Invalid pageId provided to getCachedData");
            return null;
        }
        return cachedData.get(pageId);
    }

    public boolean hasCachedData(String pageId) {
        if (pageId == null || pageId.isEmpty()) {
            Log.e(TAG, "Invalid pageId provided to hasCachedData");
            return false;
        }
        return cachedData.containsKey(pageId);
    }

    public void cacheData(String pageId, FirestoreModel model) {
        if (pageId == null || pageId.isEmpty()) {
            Log.e(TAG, "Invalid pageId provided to cacheData");
            return;
        }
        if (model == null) {
            Log.e(TAG, "Cannot cache null model for pageId: " + pageId);
            return;
        }
        cachedData.put(pageId, model);
    }

    public void clearCache() {
        cachedData.clear();
    }

    public void clearCache(String key) {
        if (key != null) {
            cachedData.remove(key);
        } else {
            clearCache();
        }
    }
}
