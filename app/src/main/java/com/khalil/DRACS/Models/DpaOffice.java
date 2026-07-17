package com.khalil.DRACS.Models;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.khalil.DRACS.R;

/**
 * Official DRA siège + DPA offices for Casablanca-Settat.
 * Pin view IDs must match {@code fragment_home.xml}. DRA has no map pin (detail-only).
 */
public enum DpaOffice {
    /** Regional Direction siège — shown in contact card; no map pin. */
    DRA_SIEGE(
            0,
            R.string.dra_siege_name,
            R.string.dra_siege_address,
            R.string.dra_siege_phone,
            33.2439330,
            -8.4945140,
            "DRA+Siege+El+Jadida"
    ),
    EL_JADIDA(
            R.id.pin_el_jadida,
            R.string.dpa_el_jadida_name,
            R.string.dpa_el_jadida_address,
            R.string.dpa_el_jadida_phone,
            33.2481510,
            -8.5023600,
            null
    ),
    SIDI_BENNOUR(
            R.id.pin_sidi_bennour,
            R.string.dpa_sidi_bennour_name,
            R.string.dpa_sidi_bennour_address,
            R.string.dpa_sidi_bennour_phone,
            32.6495,
            -8.4272,
            null
    ),
    SETTAT(
            R.id.pin_settat,
            R.string.dpa_settat_name,
            R.string.dpa_settat_address,
            R.string.dpa_settat_phone,
            33.0100280,
            -7.6162690,
            null
    ),
    CASABLANCA(
            R.id.pin_casablanca,
            R.string.dpa_casablanca_name,
            R.string.dpa_casablanca_address,
            R.string.dpa_casablanca_phone,
            33.5835140,
            -7.6108030,
            null
    ),
    BENSLIMANE(
            R.id.pin_benslimane,
            R.string.dpa_benslimane_name,
            R.string.dpa_benslimane_address,
            R.string.dpa_benslimane_phone,
            33.6090340,
            -7.1259160,
            null
    ),
    BERRECHID(
            R.id.pin_berrechid,
            R.string.dpa_berrechid_name,
            R.string.dpa_berrechid_address,
            R.string.dpa_berrechid_phone,
            33.264458,
            -7.581894,
            null
    );

    /** Default selection — regional DRA siège contact card. */
    public static final DpaOffice DEFAULT = DRA_SIEGE;

    /** {@code 0} means this office has no pin on the map. */
    @IdRes
    public final int pinViewId;
    @StringRes
    public final int nameResId;
    @StringRes
    public final int addressResId;
    @StringRes
    public final int phoneResId;
    public final double latitude;
    public final double longitude;
    @Nullable
    public final String geoLabel;

    DpaOffice(int pinViewId,
              @StringRes int nameResId,
              @StringRes int addressResId,
              @StringRes int phoneResId,
              double latitude,
              double longitude,
              @Nullable String geoLabel) {
        this.pinViewId = pinViewId;
        this.nameResId = nameResId;
        this.addressResId = addressResId;
        this.phoneResId = phoneResId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geoLabel = geoLabel;
    }

    public boolean hasMapPin() {
        return pinViewId != 0;
    }

    public static DpaOffice fromPinViewId(@IdRes int viewId) {
        for (DpaOffice office : values()) {
            if (office.hasMapPin() && office.pinViewId == viewId) {
                return office;
            }
        }
        return DEFAULT;
    }

    public static DpaOffice fromKey(String key) {
        if (key == null || key.isEmpty()) {
            return DEFAULT;
        }
        if ("CASABLANCA_MOHAMMEDIA".equals(key)) {
            return CASABLANCA;
        }
        try {
            return DpaOffice.valueOf(key);
        } catch (IllegalArgumentException ignored) {
            return DEFAULT;
        }
    }
}
