package com.clem.ipoca.spa;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.clem.ipoca.R;
import com.clem.ipoca.core.preferences.UserPreferences;

/**
 * Created by laileon on 2017/9/21.
 */

public class ColorUtil {

    public static int getThemeColor(Context context) {
        final int theme = UserPreferences.getTheme();
        int otherTheme;
        if(theme == com.clem.ipoca.core.R.style.Theme_AntennaPod_Light) {
            otherTheme =  ContextCompat.getColor(context, R.color.black);
        } else {
            otherTheme =  ContextCompat.getColor(context, R.color.white);
        }
        return otherTheme;
    }


}
