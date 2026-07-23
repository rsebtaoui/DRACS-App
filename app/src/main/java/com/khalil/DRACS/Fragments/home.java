package com.khalil.DRACS.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.navigation.fragment.NavHostFragment;

import com.khalil.DRACS.Models.DpaOffice;
import com.khalil.DRACS.R;

public class home extends Fragment {

    private static final String PREFS_NAME = "DRACS_Prefs";
    private static final String KEY_SELECTED_DPA = "selected_dpa_office";
    private static final long DETAIL_FADE_MS = 220L;
    private TextView dpaDetailName;
    private TextView dpaDetailAddress;
    private ImageButton dpaCallButton;
    private ImageButton dpaMapsButton;
    private View dpaDetailPanel;
    private DpaOffice selectedOffice = DpaOffice.DEFAULT;

    public home() {
        // Required empty public constructor
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
            pin.setOnClickListener(v -> selectDpa(office, true));
        }

        dpaCallButton.setOnClickListener(v -> dialSelectedOffice());
        dpaMapsButton.setOnClickListener(v -> openOfficeInMaps(selectedOffice));

        selectDpa(resolveInitialDpa(), false);
    }

    @NonNull
    private DpaOffice resolveInitialDpa() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedKey = prefs.getString(KEY_SELECTED_DPA, null);
        if (!TextUtils.isEmpty(savedKey)) {
            return DpaOffice.fromKey(savedKey);
        }
        return DpaOffice.DEFAULT;
    }

    private void selectDpa(@NonNull DpaOffice office, boolean animate) {
        selectedOffice = office;
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SELECTED_DPA, office.name())
                .apply();

        updatePinSelection(requireView());
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
            pin.setImageResource(selected ? R.drawable.map_pin_blue : R.drawable.map_pin);
            pin.setSelected(selected);
            pin.setAlpha(selected ? 1f : 0.95f);
            pin.setScaleX(selected ? 1.12f : 1f);
            pin.setScaleY(selected ? 1.12f : 1f);
        }
    }

    private void updateDetailPanel(@NonNull DpaOffice office, boolean animate) {
        CharSequence name = getString(office.nameResId);
        CharSequence address = getString(office.addressResId);
        String phone = getString(office.phoneResId).trim();
        boolean canCall = !TextUtils.isEmpty(phone);

        dpaCallButton.setEnabled(canCall);
        dpaCallButton.setAlpha(canCall ? 1f : 0.4f);

        Runnable applyText = () -> {
            dpaDetailName.setText(name);
            dpaDetailAddress.setText(address);
        };

        if (!animate) {
            dpaDetailPanel.setAlpha(1f);
            applyText.run();
            return;
        }

        dpaDetailPanel.animate().cancel();
        dpaDetailPanel.animate()
                .alpha(0f)
                .setDuration(DETAIL_FADE_MS / 2)
                .withEndAction(() -> {
                    applyText.run();
                    dpaDetailPanel.animate()
                            .alpha(1f)
                            .setDuration(DETAIL_FADE_MS / 2)
                            .start();
                })
                .start();
    }

    private void dialSelectedOffice() {
        String phone = getString(selectedOffice.phoneResId).trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(requireContext(), R.string.dpa_phone_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        try {
            startActivity(dial);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), R.string.dpa_phone_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    private void openOfficeInMaps(@NonNull DpaOffice office) {
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
        try {
            startActivity(mapsIntent);
        } catch (ActivityNotFoundException e) {
            Uri webUri = Uri.parse(String.format(
                    java.util.Locale.US,
                    "https://www.google.com/maps/search/?api=1&query=%f,%f",
                    office.latitude,
                    office.longitude
            ));
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, webUri));
            } catch (ActivityNotFoundException ignored) {
                Toast.makeText(requireContext(), R.string.dpa_maps_unavailable, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateTo(int actionId) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(actionId);
    }
}
