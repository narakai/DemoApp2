package com.clem.ipocadonation1.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.clem.ipocadonation1.R;
import com.clem.ipocadonation1.activity.MainActivity;
import com.clem.ipocadonation1.activity.OnlineFeedViewActivity;
import com.clem.ipocadonation1.activity.OpmlImportFromPathActivity;
import com.clem.ipocadonation1.core.preferences.UserPreferences;
import com.clem.ipocadonation1.core.view.CornerView.CornerButton;
import com.clem.ipocadonation1.fragment.gpodnet.GpodnetMainFragment;
import com.clem.ipocadonation1.spa.ColorUtil;

/**
 * Provides actions for adding new podcast subscriptions
 */
public class AddFeedFragment extends Fragment {

    public static final String TAG = "AddFeedFragment";

    /**
     * Preset value for url text field.
     */
    public static final String ARG_FEED_URL = "feedurl";
    private CornerButton butSearchITunes;
    private CornerButton butBrowserGpoddernet;
    private CornerButton butSearchFyyd;
    private CornerButton butOpmlImport;
    private CornerButton butConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.addfeed, container, false);

        final EditText etxtFeedurl = (EditText) root.findViewById(R.id.etxtFeedurl);

        Bundle args = getArguments();
        if (args != null && args.getString(ARG_FEED_URL) != null) {
            etxtFeedurl.setText(args.getString(ARG_FEED_URL));
        }

        butSearchITunes = (CornerButton) root.findViewById(R.id.butSearchItunes);
        butSearchITunes.setBackgroundColor(UserPreferences.getPrefColor());
        butSearchITunes.setTextColor(ColorUtil.getThemeColor(getContext()));
        butBrowserGpoddernet = (CornerButton) root.findViewById(R.id.butBrowseGpoddernet);
        butBrowserGpoddernet.setBackgroundColor(UserPreferences.getPrefColor());
        butBrowserGpoddernet.setTextColor(ColorUtil.getThemeColor(getContext()));
        butSearchFyyd = (CornerButton) root.findViewById(R.id.butSearchFyyd);
        butSearchFyyd.setBackgroundColor(UserPreferences.getPrefColor());
        butSearchFyyd.setTextColor(ColorUtil.getThemeColor(getContext()));
        butOpmlImport = (CornerButton) root.findViewById(R.id.butOpmlImport);
        butOpmlImport.setBackgroundColor(UserPreferences.getPrefColor());
        butOpmlImport.setTextColor(ColorUtil.getThemeColor(getContext()));
        butConfirm = (CornerButton) root.findViewById(R.id.butConfirm);
        butConfirm.setBackgroundColor(UserPreferences.getPrefColor());
        butConfirm.setTextColor(ColorUtil.getThemeColor(getContext()));

        final MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.add_feed_label);

        butSearchITunes.setOnClickListener(v -> activity.loadChildFragment(new ItunesSearchFragment()));

        butBrowserGpoddernet.setOnClickListener(v -> activity.loadChildFragment(new GpodnetMainFragment()));

        butSearchFyyd.setOnClickListener(v -> activity.loadChildFragment(new FyydSearchFragment()));

        butOpmlImport.setOnClickListener(v -> startActivity(new Intent(getActivity(),
                OpmlImportFromPathActivity.class)));

        butConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
            intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, etxtFeedurl.getText().toString());
            intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, getString(R.string.add_feed_label));
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        butSearchITunes.setBackgroundColor(UserPreferences.getPrefColor());
        butSearchITunes.setTextColor(ColorUtil.getThemeColor(getContext()));
        butBrowserGpoddernet.setBackgroundColor(UserPreferences.getPrefColor());
        butBrowserGpoddernet.setTextColor(ColorUtil.getThemeColor(getContext()));
        butSearchFyyd.setBackgroundColor(UserPreferences.getPrefColor());
        butSearchFyyd.setTextColor(ColorUtil.getThemeColor(getContext()));
        butOpmlImport.setBackgroundColor(UserPreferences.getPrefColor());
        butOpmlImport.setTextColor(ColorUtil.getThemeColor(getContext()));
        butConfirm.setBackgroundColor(UserPreferences.getPrefColor());
        butConfirm.setTextColor(ColorUtil.getThemeColor(getContext()));
    }
}
