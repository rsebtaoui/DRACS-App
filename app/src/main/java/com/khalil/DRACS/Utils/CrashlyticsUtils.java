package com.khalil.DRACS.Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.net.UnknownHostException;

public class CrashlyticsUtils {
    private static final String TAG = "CrashlyticsUtils";

    /**
     * Log a non-fatal error to Crashlytics
     * @param throwable The exception to log
     * @param message Optional message to include with the error
     */
    public static void logError(Throwable throwable, String message) {
        try {
            FirebaseCrashlytics.getInstance().recordException(throwable);
            if (message != null && !message.isEmpty()) {
                FirebaseCrashlytics.getInstance().log(message);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to log error to Crashlytics", e);
        }
    }

    /**
     * Log a custom key-value pair to Crashlytics
     * @param key The key to store
     * @param value The value to store
     */
    public static void setCustomKey(String key, String value) {
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set custom key in Crashlytics", e);
        }
    }

    /**
     * Log a user identifier to Crashlytics
     * @param userId The user ID to set
     */
    public static void setUserId(String userId) {
        try {
            FirebaseCrashlytics.getInstance().setUserId(userId);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set user ID in Crashlytics", e);
        }
    }

    /**
     * Log a custom message to Crashlytics
     * @param message The message to log
     */
    public static void log(String message) {
        try {
            FirebaseCrashlytics.getInstance().log(message);
        } catch (Exception e) {
            Log.e(TAG, "Failed to log message to Crashlytics", e);
        }
    }

    /**
     * Handle network-related exceptions
     * @param e The exception to handle
     * @param context Context for showing toast messages
     * @param operationName Name of the operation that failed
     */
    public static void handleNetworkException(Exception e, Context context, String operationName) {
        String errorMessage;
        if (e instanceof UnknownHostException) {
            errorMessage = "No internet connection";
        } else if (e instanceof IOException) {
            errorMessage = "Network error occurred";
        } else {
            errorMessage = "An error occurred during " + operationName;
        }
        
        logError(e, "Network error during " + operationName);
        setCustomKey("operation", operationName);
        setCustomKey("error_type", "network");
        
        if (context != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle data parsing exceptions
     * @param e The exception to handle
     * @param context Context for showing toast messages
     * @param dataType Type of data being parsed
     */
    public static void handleParsingException(Exception e, Context context, String dataType) {
        String errorMessage = "Error parsing " + dataType;
        
        logError(e, "Parsing error for " + dataType);
        setCustomKey("data_type", dataType);
        setCustomKey("error_type", "parsing");
        
        if (context != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle Firestore exceptions
     * @param e The Firestore exception to handle
     * @param context Context for showing toast messages
     * @param operationName Name of the Firestore operation that failed
     */
    public static void handleFirestoreException(FirebaseFirestoreException e, Context context, String operationName) {
        String errorMessage = "Database error occurred";
        
        logError(e, "Firestore error during " + operationName);
        setCustomKey("operation", operationName);
        setCustomKey("error_type", "firestore");
        setCustomKey("error_code", String.valueOf(e.getCode()));
        
        if (context != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle UI-related exceptions
     * @param e The exception to handle
     * @param context Context for showing toast messages
     * @param viewName Name of the view or fragment where error occurred
     */
    public static void handleUIException(Exception e, Context context, String viewName) {
        String errorMessage = "UI error occurred";
        
        logError(e, "UI error in " + viewName);
        setCustomKey("view_name", viewName);
        setCustomKey("error_type", "ui");
        
        if (context != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle file operation exceptions
     * @param e The exception to handle
     * @param context Context for showing toast messages
     * @param operationName Name of the file operation that failed
     */
    public static void handleFileException(Exception e, Context context, String operationName) {
        String errorMessage = "File operation failed";
        
        logError(e, "File error during " + operationName);
        setCustomKey("operation", operationName);
        setCustomKey("error_type", "file");
        
        if (context != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
} 