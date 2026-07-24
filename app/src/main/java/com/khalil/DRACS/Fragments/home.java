package com.khalil.DRACS.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.khalil.DRACS.Models.DpaOffice;
import com.khalil.DRACS.R;

/**
 * Home dashboard: service grid + interactive Casablanca-Settat DPA map.
 * Presentational only (no ViewModel) — selection is persisted in {@code DRACS_Prefs}.
 */
public class home extends Fragment {

    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_SELECTED_DPA = "selected_dpa_office";
    private static final String STATE_SELECTED_DPA = "state_selected_dpa";
    private static final long DETAIL_FADE_MS = 220L;

    private TextView dpaDetailName;
    private TextView dpaDetailAddress;
    private ImageButton dpaCallButton;
    private ImageButton dpaMapsButton;
    private View dpaDetailPanel;
    @NonNull
    private DpaOffice selectedOffice = DpaOffice.DEFAULT;

    public home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectedOffice = DpaOffice.fromKey(savedInstanceState.getString(STATE_SELECTED_DPA));
        } else {
            selectedOffice = resolveInitialDpa();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View rnaClick = view.findViewById(R.id.RNA);
        View psClick = view.findViewById(R.id.ps);
        View fdaClick = view.findViewById(R.id.FDA);
        View fpClick = view.findViewById(R.id.FP);
        View jeClick = view.findViewById(R.id.JE);

        rnaClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_RNA));
        psClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_PS));
        fdaClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_FDA));
        fpClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_FP));
        jeClick.setOnClickListener(v -> navigateTo(R.id.action_home_to_JE));

        bindDpaMap(view);
        selectDpa(selectedOffice, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SELECTED_DPA, selectedOffice.name());
    }

    @Override
    public void onDestroyView() {
        if (dpaDetailPanel != null) {
            dpaDetailPanel.animate().cancel();
        }
        dpaDetailPanel = null;
        dpaDetailName = null;
        dpaDetailAddress = null;
        dpaCallButton = null;
        dpaMapsButton = null;
        super.onDestroyView();
    }

    private void bindDpaMap(@NonNull View root) {
        dpaDetailPanel = root.findViewById(R.id.dpa_detail_panel);
        dpaDetailName = root.findViewById(R.id.dpa_detail_name);
        dpaDetailAddress = root.findViewById(R.id.dpa_detail_address);
        dpaCallButton = root.findViewById(R.id.dpa_call_button);
        dpaMapsButton = root.findViewById(R.id.dpa_maps_button);

        for (DpaOffice office : DpaOffice.values()) {
            if (!office.hasMapPin()) {
                continue;
            }
            ImageButton pin = root.findViewById(office.pinViewId);
            if (pin == null) {
                continue;
            }
            final DpaOffice target = office;
            pin.setOnClickListener(v -> selectDpa(target, true));
        }

        dpaCallButton.setOnClickListener(v -> dialSelectedOffice());
        dpaMapsButton.setOnClickListener(v -> openOfficeInMaps(selectedOffice));
    }

    @NonNull
    private DpaOffice resolveInitialDpa() {
        Context context = getContext();
        if (context == null) {
            return DpaOffice.DEFAULT;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedKey = prefs.getString(KEY_SELECTED_DPA, null);
        if (!TextUtils.isEmpty(savedKey)) {
            return DpaOffice.fromKey(savedKey);
        }
        return DpaOffice.DEFAULT;
    }

    private void selectDpa(@NonNull DpaOffice office, boolean animate) {
        if (!isAdded()) {
            return;
        }
        selectedOffice = office;
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SELECTED_DPA, office.name())
                .apply();

        View root = getView();
        if (root == null) {
            return;
        }
        updatePinSelection(root);
        updateDetailPanel(office, animate);
    }

    private void updatePinSelection(@NonNull View root) {
        for (DpaOffice office : DpaOffice.values()) {
            if (!office.hasMapPin()) {
                continue;
            }
            ImageButton pin = root.findViewById(office.pinViewId);
            if (pin == null) {
                continue;
            }
            boolean selected = office == selectedOffice;
            // DRA siège (regional HQ) stays blue to remain distinct; others turn blue when selected.
            boolean useBlue = selected || office == DpaOffice.DRA_SIEGE;
            pin.setImageResource(useBlue ? R.drawable.map_pin_blue : R.drawable.map_pin);
            pin.setSelected(selected);
            pin.setAlpha(selected ? 1f : 0.95f);
            pin.setScaleX(selected ? 1.12f : 1f);
            pin.setScaleY(selected ? 1.12f : 1f);
        }
    }

    private void updateDetailPanel(@NonNull DpaOffice office, boolean animate) {
        if (dpaDetailPanel == null || dpaDetailName == null || dpaDetailAddress == null
                || dpaCallButton == null) {
            return;
        }

        CharSequence name = getString(office.nameResId);
        CharSequence address = getString(office.addressResId);
        String phone = getString(office.phoneResId).trim();
        boolean canCall = !TextUtils.isEmpty(phone);

        dpaCallButton.setEnabled(canCall);
        dpaCallButton.setAlpha(canCall ? 1f : 0.4f);

        Runnable applyText = () -> {
            if (!isAdded() || dpaDetailName == null || dpaDetailAddress == null) {
                return;
            }
            dpaDetailName.setText(name);
            dpaDetailAddress.setText(address);
        };

        dpaDetailPanel.animate().cancel();
        if (!animate) {
            dpaDetailPanel.setAlpha(1f);
            applyText.run();
            return;
        }

        dpaDetailPanel.animate()
                .alpha(0f)
                .setDuration(DETAIL_FADE_MS / 2)
                .withEndAction(() -> {
                    applyText.run();
                    if (!isAdded() || dpaDetailPanel == null) {
                        return;
                    }
                    dpaDetailPanel.animate()
                            .alpha(1f)
                            .setDuration(DETAIL_FADE_MS / 2)
                            .start();
                })
                .start();
    }

    private void dialSelectedOffice() {
        if (!isAdded()) {
            return;
        }
        String phone = getString(selectedOffice.phoneResId).trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(requireContext(), R.string.dpa_phone_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone)));
        safeStartActivity(dial, R.string.dpa_phone_unavailable);
    }

    private void openOfficeInMaps(@NonNull DpaOffice office) {
        if (!isAdded()) {
            return;
        }
        String label = office.geoLabel != null
                ? office.geoLabel
                : Uri.encode(getString(office.nameResId));
        Uri geoUri = Uri.parse(String.format(
                java.util.Locale.US,
                "geo:%f,%f?q=%f,%f(%s)",
                office.latitude,
                office.longitude,
                office.latitude,
                office.longitude,
                label
        ));
        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        mapsIntent.setPackage("com.google.android.apps.maps");

        if (canHandle(mapsIntent)) {
            try {
                startActivity(mapsIntent);
                return;
            } catch (ActivityNotFoundException ignored) {
                // Fall through to browser / generic geo handler.
            }
        }

        Intent genericGeo = new Intent(Intent.ACTION_VIEW, geoUri);
        if (canHandle(genericGeo)) {
            safeStartActivity(genericGeo, R.string.dpa_maps_unavailable);
            return;
        }

        Uri webUri = Uri.parse(String.format(
                java.util.Locale.US,
                "https://www.google.com/maps/search/?api=1&query=%f,%f",
                office.latitude,
                office.longitude
        ));
        safeStartActivity(new Intent(Intent.ACTION_VIEW, webUri), R.string.dpa_maps_unavailable);
    }

    private boolean canHandle(@NonNull Intent intent) {
        Context context = getContext();
        if (context == null) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        return intent.resolveActivity(pm) != null;
    }

    private void safeStartActivity(@NonNull Intent intent, int errorMessageRes) {
        if (!isAdded()) {
            return;
        }
        if (!canHandle(intent)) {
            Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateTo(int actionId) {
        if (!isAdded()) {
            return;
        }
        try {
            NavController navController = NavHostFragment.findNavController(this);
            NavDestination current = navController.getCurrentDestination();
            if (current != null && current.getAction(actionId) == null) {
                return;
            }
            navController.navigate(actionId);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            // Fragment not attached to the graph / already navigating away.
        }
    }
}
