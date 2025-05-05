package com.khalil.DRACS.Utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.cachedData = new HashMap<>();
        this.completedRequests = 0;
    }

    public void startPreFetching(OnPreFetchCompleteListener listener) {
        this.listener = listener;
        for (String pageId : PAGE_IDS) {
            fetchPageData(pageId);
        }
    }

    private void fetchPageData(String pageId) {
        db.collection("pages").document(pageId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FirestoreModel model = document.toObject(FirestoreModel.class);
                            if (model != null) {
                                cachedData.put(pageId, model);
                                Log.d(TAG, "Successfully cached page: " + pageId);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching page " + pageId + ": " + task.getException());
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
        return cachedData.get(pageId);
    }

    public boolean hasCachedData(String pageId) {
        return cachedData.containsKey(pageId);
    }

    public void cacheData(String pageId, FirestoreModel model) {
        cachedData.put(pageId, model);
    }

    public void clearCache() {
        cachedData.clear();
    }
}
