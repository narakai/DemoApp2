package com.clem.ipoca.fragment.gpodnet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clem.ipoca.R;
import com.clem.ipoca.activity.MainActivity;
import com.clem.ipoca.activity.OnlineFeedViewActivity;
import com.clem.ipoca.adapter.gpodnet.PodcastListAdapter;
import com.clem.ipoca.core.gpoddernet.GpodnetService;
import com.clem.ipoca.core.gpoddernet.GpodnetServiceException;
import com.clem.ipoca.core.gpoddernet.model.GpodnetPodcast;
import com.clem.ipoca.core.preferences.UserPreferences;
import com.clem.ipoca.core.view.CornerView.CornerButton;
import com.clem.ipoca.menuhandler.MenuItemUtils;
import com.clem.ipoca.spa.ColorUtil;

import java.util.List;

/**
 * Displays a list of GPodnetPodcast-Objects in a GridView
 */
public abstract class PodcastListFragment extends Fragment {

    private static final String TAG = "PodcastListFragment";

    private GridView gridView;
    private ProgressBar progressBar;
    private TextView txtvError;
    private CornerButton butRetry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.gpodder_podcasts, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemUtils.adjustTextColor(getActivity(), sv);
        sv.setQueryHint(getString(R.string.gpodnet_search_hint));
        sv.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                sv.clearFocus();
                MainActivity activity = (MainActivity)getActivity();
                if (activity != null) {
                    activity.loadChildFragment(SearchListFragment.newInstance(s));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.gpodnet_podcast_list, container, false);

        gridView = (GridView) root.findViewById(R.id.gridView);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        txtvError = (TextView) root.findViewById(R.id.txtvError);
        butRetry = (CornerButton) root.findViewById(R.id.butRetry);
        butRetry.setBackgroundColor(UserPreferences.getPrefColor());
        butRetry.setTextColor(ColorUtil.getThemeColor(this.getActivity().getApplicationContext()));

        gridView.setOnItemClickListener((parent, view, position, id) ->
                onPodcastSelected((GpodnetPodcast) gridView.getAdapter().getItem(position)));
        butRetry.setOnClickListener(v -> loadData());

        loadData();
        return root;
    }

    protected void onPodcastSelected(GpodnetPodcast selection) {
        Log.d(TAG, "Selected podcast: " + selection.toString());
        Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
        intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, selection.getUrl());
        intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, getString(R.string.gpodnet_main_label));
        startActivity(intent);
    }

    protected abstract List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException;

    protected final void loadData() {
        AsyncTask<Void, Void, List<GpodnetPodcast>> loaderTask = new AsyncTask<Void, Void, List<GpodnetPodcast>>() {
            volatile Exception exception = null;

            @Override
            protected List<GpodnetPodcast> doInBackground(Void... params) {
                GpodnetService service = null;
                try {
                    service = new GpodnetService();
                    return loadPodcastData(service);
                } catch (GpodnetServiceException e) {
                    exception = e;
                    e.printStackTrace();
                    return null;
                } finally {
                    if (service != null) {
                        service.shutdown();
                    }
                }
            }

            @Override
            protected void onPostExecute(List<GpodnetPodcast> gpodnetPodcasts) {
                super.onPostExecute(gpodnetPodcasts);
                final Context context = getActivity();
                if (context != null && gpodnetPodcasts != null && gpodnetPodcasts.size() > 0) {
                    PodcastListAdapter listAdapter = new PodcastListAdapter(context, 0, gpodnetPodcasts);
                    gridView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                    txtvError.setVisibility(View.GONE);
                    butRetry.setVisibility(View.GONE);
                } else if (context != null && gpodnetPodcasts != null) {
                    gridView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    txtvError.setText(getString(R.string.search_status_no_results));
                    txtvError.setVisibility(View.VISIBLE);
                    butRetry.setVisibility(View.GONE);
                } else if (context != null) {
                    gridView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    txtvError.setText(getString(R.string.error_msg_prefix) + exception.getMessage());
                    txtvError.setVisibility(View.VISIBLE);
                    butRetry.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                gridView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtvError.setVisibility(View.GONE);
                butRetry.setVisibility(View.GONE);
            }
        };

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            loaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            loaderTask.execute();
        }
    }
}
