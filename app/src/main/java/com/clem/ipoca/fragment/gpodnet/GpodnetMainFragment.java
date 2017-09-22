package com.clem.ipoca.fragment.gpodnet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.clem.ipoca.R;
import com.clem.ipoca.activity.MainActivity;
import com.clem.ipoca.menuhandler.MenuItemUtils;

/**
 * Main navigation hub for gpodder.net podcast directory
 */
public class GpodnetMainFragment extends Fragment {

    public static final String TAG = "GpodnetMainFragment";

    private static final String PREF_LAST_TAB_POSITION = "tab_position";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.pager_fragment, container, false);

        viewPager = (ViewPager)root.findViewById(R.id.viewpager);
        GpodnetPagerAdapter pagerAdapter = new GpodnetPagerAdapter(getChildFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.gpodder_podcasts, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemUtils.adjustTextColor(getActivity(), sv);
        sv.setQueryHint(getString(R.string.gpodnet_search_hint));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Activity activity = getActivity();
                if (activity != null) {
                    sv.clearFocus();
                    ((MainActivity) activity).loadChildFragment(SearchListFragment.newInstance(s));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // save our tab selection
        SharedPreferences prefs = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_LAST_TAB_POSITION, tabLayout.getSelectedTabPosition());
        editor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();

        // restore our last position
        SharedPreferences prefs = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        int lastPosition = prefs.getInt(PREF_LAST_TAB_POSITION, 0);
        viewPager.setCurrentItem(lastPosition);
    }

    public class GpodnetPagerAdapter extends FragmentPagerAdapter {


        private static final int NUM_PAGES = 2;
        private static final int POS_TOPLIST = 0;
        private static final int POS_TAGS = 1;
        private static final int POS_SUGGESTIONS = 2;

        Resources resources;

        public GpodnetPagerAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            this.resources = resources;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case POS_TOPLIST:
                    return new PodcastTopListFragment();
                case POS_TAGS:
                    return new TagListFragment();
                case POS_SUGGESTIONS:
                    return new SuggestionListFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POS_TAGS:
                    return getString(R.string.gpodnet_taglist_header);
                case POS_TOPLIST:
                    return getString(R.string.gpodnet_toplist_header);
                case POS_SUGGESTIONS:
                    return getString(R.string.gpodnet_suggestions_header);
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
