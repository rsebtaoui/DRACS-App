package com.khalil.DRACS.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistentDataUtils {
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_HAS_PERSISTENT_DATA = "has_persistent_data";

    public static void setHasPersistentData(Context context, boolean hasData) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_HAS_PERSISTENT_DATA, hasData).apply();
    }

    public static boolean hasPersistentData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_HAS_PERSISTENT_DATA, false);
    }
}
