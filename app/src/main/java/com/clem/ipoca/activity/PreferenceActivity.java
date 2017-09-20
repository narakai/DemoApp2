package com.clem.ipoca.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.clem.ipoca.R;
import com.clem.ipoca.core.preferences.UserPreferences;
import com.clem.ipoca.event.ColorEvent;
import com.clem.ipoca.preferences.PreferenceController;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * PreferenceActivity for API 11+. In order to change the behavior of the preference UI, see
 * PreferenceController.
 */
public class PreferenceActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback{

    private static WeakReference<PreferenceActivity> instance;
    private PreferenceController preferenceController;
    private MainFragment prefFragment;
    private final PreferenceController.PreferenceUI preferenceUI = new PreferenceController.PreferenceUI() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public Preference findPreference(CharSequence key) {
            return prefFragment.findPreference(key);
        }

        @Override
        public Activity getActivity() {
            return PreferenceActivity.this;
        }
    };
    private int primaryPreselect;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This must be the FIRST thing we do, otherwise other code may not have the
        // reference it needs
        instance = new WeakReference<>(this);

        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // set up layout
        FrameLayout root = new FrameLayout(this);
        root.setId(R.id.content);
        root.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);

        // we need to create the PreferenceController before the MainFragment
        // since the MainFragment depends on the preferenceController already being created
        preferenceController = new PreferenceController(preferenceUI);

        prefFragment = new MainFragment();
        getFragmentManager().beginTransaction().replace(R.id.content, prefFragment).commit();
        primaryPreselect = UserPreferences.getPrefColor();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        preferenceController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.select_color:
                new ColorChooserDialog.Builder(this, R.string.color_label)
                        .doneButton(R.string.ok)
                        .backButton(R.string.cancel)
                        .cancelButton(R.string.cancel)
                        .customButton(R.string.custom)
                        .presetsButton(R.string.presets)
                        .preselect(primaryPreselect)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prefer, menu);
        return true;
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {
        primaryPreselect = i;
        UserPreferences.setPrefColor(i);
        ColorDrawable drawable = new ColorDrawable(primaryPreselect);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(drawable);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(primaryPreselect));
            getWindow().setNavigationBarColor(primaryPreselect);
        }
        EventBus.getDefault().post(new ColorEvent());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MainFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            addPreferencesFromResource(R.xml.preferences);
            PreferenceActivity activity = instance.get();
            if(activity != null && activity.preferenceController != null) {
                activity.preferenceController.onCreate();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            PreferenceActivity activity = instance.get();
            if(activity != null && activity.preferenceController != null) {
                activity.preferenceController.onResume();
            }
        }

        @Override
        public void onPause() {
            PreferenceActivity activity = instance.get();
            if(activity != null && activity.preferenceController != null) {
                activity.preferenceController.onPause();
            }
            super.onPause();
        }

        @Override
        public void onStop() {
            PreferenceActivity activity = instance.get();
            if(activity != null && activity.preferenceController != null) {
                activity.preferenceController.onStop();
            }
            super.onStop();
        }
    }
}
