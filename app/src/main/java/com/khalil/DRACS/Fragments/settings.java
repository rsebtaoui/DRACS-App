package com.khalil.DRACS.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.activity.OnBackPressedCallback;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.khalil.DRACS.BuildConfig;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;
import com.khalil.DRACS.Activities.Activity_main;

public class settings extends Fragment {
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LARGE_FONT = "large_font";
    private static final String KEY_SOUNDS = "sounds_enabled";
    private static final String KEY_LANGUAGE = "app_language";

    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial largeFontSwitch;
    private SwitchMaterial soundsSwitch;
    private MaterialButton clearCacheButton;
    private MaterialButton btnLangAr;
    private MaterialButton btnLangFr;
    private MaterialButton btnSendFeedback;
    private TextView appVersionText;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = requireContext();

        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        largeFontSwitch = view.findViewById(R.id.large_font_switch);
        soundsSwitch = view.findViewById(R.id.sounds_switch);
        clearCacheButton = view.findViewById(R.id.clear_cache_button);
        btnLangAr = view.findViewById(R.id.btn_lang_ar);
        btnLangFr = view.findViewById(R.id.btn_lang_fr);
        btnSendFeedback = view.findViewById(R.id.btn_send_feedback);
        appVersionText = view.findViewById(R.id.app_version_text);

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);

        appVersionText.setText(getString(R.string.settings_version_format, BuildConfig.VERSION_NAME));

        String language = prefs.getString(KEY_LANGUAGE, "ar");
        updateLanguageButtons(language);

        darkModeSwitch.setChecked(prefs.getBoolean(KEY_DARK_MODE, false));
        largeFontSwitch.setChecked(prefs.getBoolean(KEY_LARGE_FONT, false));
        soundsSwitch.setChecked(prefs.getBoolean(KEY_SOUNDS, true));

        btnLangAr.setOnClickListener(v -> setLanguage("ar", prefs));
        btnLangFr.setOnClickListener(v -> setLanguage("fr", prefs));

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });

        largeFontSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_LARGE_FONT, isChecked).apply();
            requireActivity().recreate();
        });

        soundsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_SOUNDS, isChecked).apply());

        clearCacheButton.setOnClickListener(v -> {
            DataPreFetcher dataPreFetcher = ((Activity_main) requireActivity()).getDataPreFetcher();
            dataPreFetcher.clearCache();
            prefs.edit().putBoolean("has_persistent_data", false).apply();
            Toast.makeText(context, R.string.settings_cache_cleared, Toast.LENGTH_SHORT).show();
        });

        btnSendFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + getString(R.string.kamily_khalil_ucd_ma)));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_feedback_subject));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, R.string.settings_no_email_app, Toast.LENGTH_SHORT).show();
            }
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

    private void setLanguage(String languageTag, SharedPreferences prefs) {
        String current = prefs.getString(KEY_LANGUAGE, "ar");
        if (languageTag.equals(current)) {
            updateLanguageButtons(languageTag);
            return;
        }
        prefs.edit().putString(KEY_LANGUAGE, languageTag).apply();
        updateLanguageButtons(languageTag);
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag));
    }

    private void updateLanguageButtons(String languageTag) {
        boolean arabic = !"fr".equals(languageTag);
        btnLangAr.setBackgroundResource(arabic ? R.drawable.bg_lang_selected : R.drawable.bg_lang_unselected);
        btnLangFr.setBackgroundResource(arabic ? R.drawable.bg_lang_unselected : R.drawable.bg_lang_selected);
        btnLangAr.setTextColor(ContextCompat.getColor(context, arabic ? R.color.white : R.color.foreground));
        btnLangFr.setTextColor(ContextCompat.getColor(context, arabic ? R.color.foreground : R.color.white));
    }
}
