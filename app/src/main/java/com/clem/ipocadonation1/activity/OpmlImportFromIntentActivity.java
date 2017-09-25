package com.clem.ipocadonation1.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.afollestad.materialdialogs.color.CircleView;
import com.clem.ipocadonation1.core.preferences.UserPreferences;

/**
 * Lets the user start the OPML-import process.
 */
public class OpmlImportFromIntentActivity extends OpmlImportBaseActivity {

    private static final String TAG = "OpmlImportFromIntentAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith("/")) {
            uri = Uri.parse("file://" + uri.toString());
        } else {
            uri = Uri.parse(getIntent().getStringExtra(Intent.EXTRA_TEXT));
        }
        importUri(uri);
        int primaryPreselect = UserPreferences.getPrefColor();
        ColorDrawable drawable = new ColorDrawable(primaryPreselect);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(drawable);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(primaryPreselect));
            getWindow().setNavigationBarColor(primaryPreselect);
        }
    }

    @Override
    protected boolean finishWhenCanceled() {
        return true;
    }

}
