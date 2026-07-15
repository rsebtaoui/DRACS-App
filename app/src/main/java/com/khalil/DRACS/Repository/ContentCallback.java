package com.khalil.DRACS.Repository;

import com.khalil.DRACS.Models.FirestoreModel;

/**
 * Async result callback for {@link ContentRepository} page loads.
 */
public interface ContentCallback {

    void onSuccess(FirestoreModel model);

    void onError(String message);
}
