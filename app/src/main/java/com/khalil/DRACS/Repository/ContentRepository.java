package com.khalil.DRACS.Repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khalil.DRACS.Database.AppDatabase;
import com.khalil.DRACS.Database.FavoriteDao;
import com.khalil.DRACS.Database.FavoriteSection;
import com.khalil.DRACS.Models.FirestoreModel;
import com.khalil.DRACS.Utils.ConnectionUtils;
import com.khalil.DRACS.Utils.DataPreFetcher;

import java.util.List;

/**
 * Cache-first data access for Firestore content pages.
 * Delegates in-memory caching to {@link DataPreFetcher}; does not duplicate cache maps.
 * Room favorites are accessed via a single-thread executor; LiveData queries are Room-managed.
 */
public class ContentRepository {

    private static final String TAG = "ContentRepository";
    private static final String COLLECTION_PAGES = "pages";
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_HAS_PERSISTENT_DATA = "has_persistent_data";

    private final Context appContext;
    private final DataPreFetcher dataPreFetcher;
    private final FirebaseFirestore firestore;
    private final FavoriteDao favoriteDao;

    public ContentRepository(Context context, DataPreFetcher dataPreFetcher) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (dataPreFetcher == null) {
            throw new IllegalArgumentException("DataPreFetcher cannot be null");
        }
        this.appContext = context.getApplicationContext();
        this.dataPreFetcher = dataPreFetcher;
        this.firestore = FirebaseFirestore.getInstance();
        this.favoriteDao = AppDatabase.getInstance(appContext).favoriteDao();
    }

    public void getPage(String pageId, ContentCallback callback) {
        if (!validateInputs(pageId, callback)) {
            return;
        }

        FirestoreModel cached = dataPreFetcher.getCachedData(pageId);
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        fetchFromFirestore(pageId, callback);
    }

    public void refreshPage(String pageId, ContentCallback callback) {
        if (!validateInputs(pageId, callback)) {
            return;
        }

        if (!ConnectionUtils.isNetworkAvailable(appContext)) {
            callback.onError("تحتاج إلى اتصال بالإنترنت");
            return;
        }

        clearPage(pageId);
        fetchFromFirestore(pageId, callback);
    }

    public void clearPage(String pageId) {
        if (pageId != null && !pageId.isEmpty()) {
            dataPreFetcher.clearCache(pageId);
        }
    }

    public boolean isCached(String pageId) {
        return dataPreFetcher.hasCachedData(pageId);
    }

    public void startFullPrefetch(DataPreFetcher.OnPreFetchCompleteListener listener) {
        dataPreFetcher.startPreFetching(listener);
    }

    public boolean hasPersistentData() {
        return appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_HAS_PERSISTENT_DATA, false);
    }

    public void markPersistentDataAvailable() {
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_HAS_PERSISTENT_DATA, true)
                .apply();
    }

    // --- Favorites (Room) ---

    public LiveData<List<FavoriteSection>> observeFavoritesForPage(String pageId) {
        return favoriteDao.getFavoritesForPage(pageId);
    }

    public LiveData<List<FavoriteSection>> observeAllFavorites() {
        return favoriteDao.getAllFavorites();
    }

    public void addFavorite(String pageId, String sectionId, String title) {
        AppDatabase.getDatabaseExecutor().execute(() ->
                favoriteDao.insert(new FavoriteSection(
                        pageId,
                        sectionId,
                        title,
                        System.currentTimeMillis()))
        );
    }

    public void removeFavorite(String pageId, String sectionId) {
        AppDatabase.getDatabaseExecutor().execute(() ->
                favoriteDao.deleteByPageAndSection(pageId, sectionId)
        );
    }

    public void toggleFavorite(String pageId, String sectionId, String title) {
        AppDatabase.getDatabaseExecutor().execute(() -> {
            if (favoriteDao.isFavorite(pageId, sectionId)) {
                favoriteDao.deleteByPageAndSection(pageId, sectionId);
            } else {
                favoriteDao.insert(new FavoriteSection(
                        pageId,
                        sectionId,
                        title,
                        System.currentTimeMillis()));
            }
        });
    }

    public void isFavorite(String pageId, String sectionId, FavoriteCheckCallback callback) {
        AppDatabase.getDatabaseExecutor().execute(() -> {
            boolean favorited = favoriteDao.isFavorite(pageId, sectionId);
            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(favorited));
            }
        });
    }

    private void fetchFromFirestore(String pageId, ContentCallback callback) {
        firestore.collection(COLLECTION_PAGES)
                .document(pageId)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        if (exception != null) {
                            FirebaseCrashlytics.getInstance().recordException(exception);
                            FirebaseCrashlytics.getInstance().log(
                                    "ContentRepository: Firestore error for page " + pageId);
                            Log.e(TAG, "Firestore error for page " + pageId, exception);
                        }
                        callback.onError("Failed to load content");
                        return;
                    }

                    DocumentSnapshot document = task.getResult();
                    if (document == null || !document.exists()) {
                        FirebaseCrashlytics.getInstance().log(
                                "ContentRepository: document missing for page " + pageId);
                        callback.onError("Document not found");
                        return;
                    }

                    FirestoreModel model = document.toObject(FirestoreModel.class);
                    if (model == null) {
                        FirebaseCrashlytics.getInstance().log(
                                "ContentRepository: deserialization failed for page " + pageId);
                        callback.onError("Failed to parse content");
                        return;
                    }

                    dataPreFetcher.cacheData(pageId, model);
                    callback.onSuccess(model);
                });
    }

    private boolean validateInputs(String pageId, ContentCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("ContentCallback cannot be null");
        }
        if (pageId == null || pageId.isEmpty()) {
            callback.onError("Invalid page ID");
            return false;
        }
        return true;
    }

    public interface FavoriteCheckCallback {
        void onResult(boolean isFavorite);
    }
}
