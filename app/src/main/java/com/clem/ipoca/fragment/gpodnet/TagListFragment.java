package com.clem.ipoca.fragment.gpodnet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.clem.ipoca.R;
import com.clem.ipoca.activity.MainActivity;
import com.clem.ipoca.adapter.gpodnet.TagListAdapter;
import com.clem.ipoca.core.gpoddernet.GpodnetService;
import com.clem.ipoca.core.gpoddernet.GpodnetServiceException;
import com.clem.ipoca.core.gpoddernet.model.GpodnetTag;

import java.util.List;

public class TagListFragment extends ListFragment {

    private static final String TAG = "TagListFragment";
    private static final int COUNT = 50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView lv = getListView();
        lv.setClipToPadding(false);
        final int vertPadding = getResources().getDimensionPixelSize(R.dimen.list_vertical_padding);
        lv.setPadding(0, vertPadding, 0, vertPadding);
        ViewCompat.setNestedScrollingEnabled(lv, true);

        lv.setOnItemClickListener((parent, view1, position, id) -> {
            GpodnetTag tag = (GpodnetTag) getListAdapter().getItem(position);
            MainActivity activity = (MainActivity) getActivity();
            activity.loadChildFragment(TagFragment.newInstance(tag));
        });

        startLoadTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.add_feed_label);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelLoadTask();
    }

    private AsyncTask<Void, Void, List<GpodnetTag>> loadTask;

    private void cancelLoadTask() {
        if (loadTask != null && !loadTask.isCancelled()) {
            loadTask.cancel(true);
        }
    }

    private void startLoadTask() {
        cancelLoadTask();
        loadTask = new AsyncTask<Void, Void, List<GpodnetTag>>() {
            private Exception exception;

            @Override
            protected List<GpodnetTag> doInBackground(Void... params) {
                GpodnetService service = new GpodnetService();
                try {
                    return service.getTopTags(COUNT);
                } catch (GpodnetServiceException e) {
                    e.printStackTrace();
                    exception = e;
                    return null;
                } finally {
                    service.shutdown();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setListShown(false);
            }

            @Override
            protected void onPostExecute(List<GpodnetTag> gpodnetTags) {
                super.onPostExecute(gpodnetTags);
                final Context context = getActivity();
                if (context != null) {
                    if (gpodnetTags != null) {
                        setListAdapter(new TagListAdapter(context, android.R.layout.simple_list_item_1, gpodnetTags));
                    } else if (exception != null) {
                        TextView txtvError = new TextView(getActivity());
                        txtvError.setText(exception.getMessage());
                        getListView().setEmptyView(txtvError);
                    }
                    setListShown(true);

                }
            }
        };
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            loadTask.execute();
        }
    }
}

