package com.clem.ipocadonation1.fragment.gpodnet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clem.ipocadonation1.R;
import com.clem.ipocadonation1.activity.OnlineFeedViewActivity;
import com.clem.ipocadonation1.adapter.gpodnet.PodcastListAdapter;
import com.clem.ipocadonation1.core.gpoddernet.GpodnetService;
import com.clem.ipocadonation1.core.gpoddernet.GpodnetServiceException;
import com.clem.ipocadonation1.core.gpoddernet.model.GpodnetPodcast;
import com.clem.ipocadonation1.core.preferences.UserPreferences;
import com.clem.ipocadonation1.core.view.CornerView.CornerButton;
import com.clem.ipocadonation1.spa.ColorUtil;

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

        ViewCompat.setNestedScrollingEnabled(gridView, true);
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
