package com.clem.ipoca1.core.util;

import android.util.Log;

import com.clem.ipoca1.core.R;
import com.clem.ipoca1.core.preferences.UserPreferences;

public class ThemeUtils {
    private static final String TAG = "ThemeUtils";

    public static int getSelectionBackgroundColor() {
        int theme = UserPreferences.getTheme();
        if (theme == R.style.Theme_AntennaPod_Dark) {
            return R.color.selection_background_color_dark;
        } else if (theme == R.style.Theme_AntennaPod_Light) {
            return R.color.selection_background_color_light;
        } else {
            Log.e(TAG,
                    "getSelectionBackgroundColor could not match the current theme to any color!");
            return R.color.selection_background_color_light;
        }
    }
}
