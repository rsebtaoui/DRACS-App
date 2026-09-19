package com.khalil.DRACS.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

/**
 * App-wide text scale: Small (0.85), Medium (1.0), Large (1.15).
 * Applied via {@code Configuration.fontScale} in Activity {@code attachBaseContext}.
 */
public final class FontScaleHelper {
    public static final String PREFS_NAME = "DRACS_Prefs";
    public static final String KEY_FONT_SCALE = "font_scale";
    /** Legacy boolean from the old “larger font” switch. */
    public static final String KEY_LARGE_FONT = "large_font";

    public static final String SCALE_SMALL = "small";
    public static final String SCALE_MEDIUM = "medium";
    public static final String SCALE_LARGE = "large";

    public static final float VALUE_SMALL = 0.85f;
    public static final float VALUE_MEDIUM = 1.0f;
    public static final float VALUE_LARGE = 1.15f;

    private FontScaleHelper() {
    }

    @NonNull
    public static String getScaleMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String mode = prefs.getString(KEY_FONT_SCALE, null);
        if (SCALE_SMALL.equals(mode) || SCALE_MEDIUM.equals(mode) || SCALE_LARGE.equals(mode)) {
            return mode;
        }
        // Migrate legacy large_font switch → large; otherwise medium.
        if (prefs.getBoolean(KEY_LARGE_FONT, false)) {
            return SCALE_LARGE;
        }
        return SCALE_MEDIUM;
    }

    public static float getFontScale(Context context) {
        switch (getScaleMode(context)) {
            case SCALE_SMALL:
                return VALUE_SMALL;
            case SCALE_LARGE:
                return VALUE_LARGE;
            case SCALE_MEDIUM:
            default:
                return VALUE_MEDIUM;
        }
    }

    public static void setScaleMode(Context context, @NonNull String scaleMode) {
        String normalized;
        switch (scaleMode) {
            case SCALE_SMALL:
            case SCALE_LARGE:
                normalized = scaleMode;
                break;
            case SCALE_MEDIUM:
            default:
                normalized = SCALE_MEDIUM;
                break;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_FONT_SCALE, normalized)
                .putBoolean(KEY_LARGE_FONT, SCALE_LARGE.equals(normalized))
                .apply();
    }

    @NonNull
    public static Context applyFontScale(@NonNull Context context) {
        float fontScale = getFontScale(context);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        if (Math.abs(config.fontScale - fontScale) < 0.001f) {
            return context;
        }
        config.fontScale = fontScale;
        return context.createConfigurationContext(config);
    }
}
