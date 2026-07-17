package com.khalil.DRACS.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.activity.OnBackPressedCallback;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.khalil.DRACS.BuildConfig;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;
import com.khalil.DRACS.Utils.LocaleHelper;
import com.khalil.DRACS.Activities.Activity_main;

public class settings extends Fragment {
    private static final String TAG = "SettingsFragment";
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LARGE_FONT = "large_font";
    private static final String KEY_SOUNDS = "sounds_enabled";

    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial largeFontSwitch;
    private SwitchMaterial soundsSwitch;
    private MaterialButton clearCacheButton;
    private MaterialButton btnSendFeedback;
    private TextView appVersionText;
    private Context context;
    private boolean bindingUi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = requireContext();

        LocaleHelper.clearLanguagePreference(context);

        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        largeFontSwitch = view.findViewById(R.id.large_font_switch);
        soundsSwitch = view.findViewById(R.id.sounds_switch);
        clearCacheButton = view.findViewById(R.id.clear_cache_button);
        btnSendFeedback = view.findViewById(R.id.btn_send_feedback);
        appVersionText = view.findViewById(R.id.app_version_text);

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);

        appVersionText.setText(getString(R.string.settings_version_format, BuildConfig.VERSION_NAME));

        bindingUi = true;
        darkModeSwitch.setChecked(prefs.getBoolean(KEY_DARK_MODE, false));
        largeFontSwitch.setChecked(prefs.getBoolean(KEY_LARGE_FONT, false));
        soundsSwitch.setChecked(prefs.getBoolean(KEY_SOUNDS, true));
        bindingUi = false;

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingUi || !isAdded()) {
                return;
            }
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        largeFontSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingUi || !isAdded()) {
                return;
            }
            prefs.edit().putBoolean(KEY_LARGE_FONT, isChecked).apply();
            requireActivity().getWindow().getDecorView().post(() -> {
                if (isAdded()) {
                    requireActivity().recreate();
                }
            });
        });

        soundsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingUi) {
                return;
            }
            prefs.edit().putBoolean(KEY_SOUNDS, isChecked).apply();
        });

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

        btnSendFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + getString(R.string.kamily_khalil_ucd_ma)));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_feedback_subject));
            try {
                startActivity(Intent.createChooser(intent, getString(R.string.settings_send_feedback)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, R.string.settings_no_email_app, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Failed to open feedback email", e);
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
}
