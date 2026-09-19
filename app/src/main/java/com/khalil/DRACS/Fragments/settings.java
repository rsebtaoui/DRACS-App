package com.khalil.DRACS.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.activity.OnBackPressedCallback;

import com.google.android.material.button.MaterialButton;
import com.khalil.DRACS.BuildConfig;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;
import com.khalil.DRACS.Utils.FontScaleHelper;
import com.khalil.DRACS.Utils.LocaleHelper;
import com.khalil.DRACS.Utils.ThemeHelper;
import com.khalil.DRACS.Activities.Activity_main;

public class settings extends Fragment {
    private static final String PREFS_NAME = "DRACS_Prefs";

    private MaterialButton btnThemeAuto;
    private MaterialButton btnThemeLight;
    private MaterialButton btnThemeDark;
    private TextView btnFontSmall;
    private TextView btnFontMedium;
    private TextView btnFontLarge;
    private MaterialButton clearCacheButton;
    private TextView appVersionText;
    private Context context;
    private boolean bindingUi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = requireContext();

        LocaleHelper.clearLanguagePreference(context);

        btnThemeAuto = view.findViewById(R.id.btn_theme_auto);
        btnThemeLight = view.findViewById(R.id.btn_theme_light);
        btnThemeDark = view.findViewById(R.id.btn_theme_dark);
        btnFontSmall = view.findViewById(R.id.btn_font_small);
        btnFontMedium = view.findViewById(R.id.btn_font_medium);
        btnFontLarge = view.findViewById(R.id.btn_font_large);
        clearCacheButton = view.findViewById(R.id.clear_cache_button);
        appVersionText = view.findViewById(R.id.app_version_text);

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);

        appVersionText.setText(getString(R.string.settings_version_format, BuildConfig.VERSION_NAME));

        bindingUi = true;
        updateThemeButtons(ThemeHelper.getThemeMode(context));
        updateFontButtons(FontScaleHelper.getScaleMode(context));
        bindingUi = false;

        btnThemeAuto.setOnClickListener(v -> selectTheme(ThemeHelper.MODE_SYSTEM));
        btnThemeLight.setOnClickListener(v -> selectTheme(ThemeHelper.MODE_LIGHT));
        btnThemeDark.setOnClickListener(v -> selectTheme(ThemeHelper.MODE_DARK));

        btnFontSmall.setOnClickListener(v -> selectFontScale(FontScaleHelper.SCALE_SMALL));
        btnFontMedium.setOnClickListener(v -> selectFontScale(FontScaleHelper.SCALE_MEDIUM));
        btnFontLarge.setOnClickListener(v -> selectFontScale(FontScaleHelper.SCALE_LARGE));

        clearCacheButton.setOnClickListener(v -> {
            if (!(requireActivity() instanceof Activity_main)) {
                return;
            }
            DataPreFetcher dataPreFetcher = ((Activity_main) requireActivity()).getDataPreFetcher();
            if (dataPreFetcher == null) {
                Toast.makeText(context, R.string.settings_cache_cleared, Toast.LENGTH_SHORT).show();
                return;
            }
            dataPreFetcher.clearCache();
            prefs.edit().putBoolean("has_persistent_data", false).apply();
            Toast.makeText(context, R.string.settings_cache_cleared, Toast.LENGTH_SHORT).show();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Navigation.findNavController(requireView()).navigate(R.id.home);
                    }
                });

        return view;
    }

    private void selectTheme(String themeMode) {
        if (bindingUi || !isAdded()) {
            return;
        }
        if (themeMode.equals(ThemeHelper.getThemeMode(context))) {
            updateThemeButtons(themeMode);
            return;
        }
        ThemeHelper.setThemeMode(requireContext(), themeMode);
        updateThemeButtons(themeMode);
    }

    private void selectFontScale(String scaleMode) {
        if (bindingUi || !isAdded()) {
            return;
        }
        if (scaleMode.equals(FontScaleHelper.getScaleMode(context))) {
            updateFontButtons(scaleMode);
            return;
        }
        FontScaleHelper.setScaleMode(requireContext(), scaleMode);
        updateFontButtons(scaleMode);
        // Recreate so attachBaseContext re-applies Configuration.fontScale app-wide.
        requireActivity().getWindow().getDecorView().post(() -> {
            if (isAdded()) {
                requireActivity().recreate();
            }
        });
    }

    private void updateThemeButtons(String themeMode) {
        styleThemeButton(btnThemeAuto, ThemeHelper.MODE_SYSTEM.equals(themeMode));
        styleThemeButton(btnThemeLight, ThemeHelper.MODE_LIGHT.equals(themeMode));
        styleThemeButton(btnThemeDark, ThemeHelper.MODE_DARK.equals(themeMode));
    }

    private void updateFontButtons(String scaleMode) {
        styleFontButton(btnFontSmall, FontScaleHelper.SCALE_SMALL.equals(scaleMode));
        styleFontButton(btnFontMedium, FontScaleHelper.SCALE_MEDIUM.equals(scaleMode));
        styleFontButton(btnFontLarge, FontScaleHelper.SCALE_LARGE.equals(scaleMode));
    }

    private void styleThemeButton(MaterialButton button, boolean selected) {
        button.setBackgroundResource(selected ? R.drawable.bg_lang_selected : R.drawable.bg_lang_unselected);
        button.setTextColor(ContextCompat.getColor(context,
                selected ? R.color.white : R.color.foreground));
    }

    private void styleFontButton(TextView button, boolean selected) {
        button.setBackgroundResource(selected ? R.drawable.bg_lang_selected : R.drawable.bg_lang_unselected);
        button.setTextColor(ContextCompat.getColor(context,
                selected ? R.color.white : R.color.foreground));
        button.setSelected(selected);
    }
}
