package com.khalil.DRACS.Fragments;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.activity.OnBackPressedCallback;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.button.MaterialButton;
import com.khalil.DRACS.R;
import com.khalil.DRACS.Utils.DataPreFetcher;
import com.khalil.DRACS.Avtivities.Activity_main;

public class settings extends Fragment {
    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    private SwitchMaterial darkModeSwitch;
    private MaterialButton clearCacheButton;
    private View contactHeader;
    private ImageView contactExpandIcon;
    private LinearLayout contactDetails;
    private boolean isContactExpanded = false;
    private TextView phoneNumber;
    private TextView developerEmail;
    private ClipboardManager clipboard;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = requireContext();
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        // Initialize views
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        clearCacheButton = view.findViewById(R.id.clear_cache_button);
        contactHeader = view.findViewById(R.id.contact_header);
        contactExpandIcon = view.findViewById(R.id.contact_expand_icon);
        contactDetails = view.findViewById(R.id.contact_details);
        phoneNumber = view.findViewById(R.id.phone_number);
        developerEmail = view.findViewById(R.id.developer_email);

        // Set up phone number click listener
        phoneNumber.setOnClickListener(v -> {
            ClipData clip = ClipData.newPlainText("Phone Number", phoneNumber.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "تم نسخ رقم الهاتف", Toast.LENGTH_SHORT).show();
        });

        // Set up developer email click listener
        developerEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + developerEmail.getText().toString()));
            intent.putExtra(Intent.EXTRA_SUBJECT, "تطبيق DRACS");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "لا يوجد تطبيق بريد إلكتروني مثبت", Toast.LENGTH_SHORT).show();
            }
        });

        // Load saved preferences
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        darkModeSwitch.setChecked(prefs.getBoolean(KEY_DARK_MODE, false));

        // Set up dark mode switch listener
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_DARK_MODE, isChecked);
            editor.apply();

            // Update the theme
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Restart the activity to apply theme changes
            requireActivity().recreate();
        });

        // Set up clear cache button listener
        clearCacheButton.setOnClickListener(v -> {
            // Clear the cache
            DataPreFetcher dataPreFetcher = ((Activity_main) requireActivity()).getDataPreFetcher();
            dataPreFetcher.clearCache();
            
            // Clear persistent data flag
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("has_persistent_data", false);
            editor.apply();

            // Show success toast
            Toast.makeText(context, "تم مسح ذاكرة التخزين المؤقت بنجاح", Toast.LENGTH_SHORT).show();
        });

        // Set up contact section expansion
        contactHeader.setOnClickListener(v -> toggleContactExpansion());

        // Set up back press handling
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to home
                Navigation.findNavController(requireView()).navigate(R.id.home);
            }
        });

        return view;
    }

    private void toggleContactExpansion() {
        isContactExpanded = !isContactExpanded;
        
        // Animate the expand/collapse icon
        RotateAnimation rotateAnimation = new RotateAnimation(
            isContactExpanded ? 0 : 180,
            isContactExpanded ? 180 : 0,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        contactExpandIcon.startAnimation(rotateAnimation);

        // Show/hide contact details
        contactDetails.setVisibility(isContactExpanded ? View.VISIBLE : View.GONE);
    }
}