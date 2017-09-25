package com.clem.ipoca1.preferences;

import com.clem.ipoca1.core.preferences.UserPreferences;

/**
 * Implements functions from PreferenceController that are flavor dependent.
 */
public class PreferenceControllerFlavorHelper {

    static void setupFlavoredUI(PreferenceController.PreferenceUI ui) {
        ui.findPreference(UserPreferences.PREF_CAST_ENABLED).setEnabled(false);
    }
}
