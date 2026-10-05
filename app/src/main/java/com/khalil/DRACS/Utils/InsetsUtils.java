package com.khalil.DRACS.Utils;

import android.view.View;
import android.view.Window;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Window inset helpers for the edge-to-edge layout that API 36 enforces.
 * Each helper keeps the padding declared in XML and adds the system bar insets on top of it.
 */
public final class InsetsUtils {

    private static final int BAR_TYPES =
            WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout();

    /** Reports the bottom system bar inset so callers can re-apply it when a view is hidden. */
    public interface BottomInsetCallback {
        void onBottomInset(int bottomInset);
    }

    private InsetsUtils() {
    }

    /** Lets the window draw behind the system bars on every supported API level. */
    public static void enableEdgeToEdge(Window window) {
        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

    /**
     * Picks the system bar icon contrast. Pass true where the bar sits on a light surface.
     */
    public static void applyBarAppearance(Window window, boolean lightStatusBars,
                                          boolean lightNavigationBars) {
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(lightStatusBars);
        controller.setAppearanceLightNavigationBars(lightNavigationBars);
    }

    /** Keeps a top bar clear of the status bar and of a side nav bar or cutout in landscape. */
    public static void padTopAndSides(View view) {
        if (view == null) {
            return;
        }
        final int left = view.getPaddingLeft();
        final int top = view.getPaddingTop();
        final int right = view.getPaddingRight();
        final int bottom = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(BAR_TYPES);
            v.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }

    /** Keeps a bottom bar clear of the nav/gesture bar while its background still fills the inset. */
    public static void padBottomAndSides(View view, BottomInsetCallback callback) {
        if (view == null) {
            return;
        }
        final int left = view.getPaddingLeft();
        final int top = view.getPaddingTop();
        final int right = view.getPaddingRight();
        final int bottom = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(BAR_TYPES);
            v.setPadding(left + bars.left, top, right + bars.right, bottom + bars.bottom);
            if (callback != null) {
                callback.onBottomInset(bars.bottom);
            }
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }

    /** Keeps full-screen content (splash) inside every system bar. */
    public static void padAllBars(View view) {
        if (view == null) {
            return;
        }
        final int left = view.getPaddingLeft();
        final int top = view.getPaddingTop();
        final int right = view.getPaddingRight();
        final int bottom = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(BAR_TYPES);
            v.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }
}
