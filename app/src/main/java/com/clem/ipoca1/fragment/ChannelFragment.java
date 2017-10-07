package com.clem.ipoca1.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.clem.ipoca1.PodcastApp;
import com.clem.ipoca1.R;
import com.clem.ipoca1.activity.MainActivity;
import com.clem.ipoca1.adapter.itunes.ItunesAdapter;
import com.clem.ipoca1.core.ClientConfig;
import com.clem.ipoca1.core.glide.ApGlideSettings;
import com.clem.ipoca1.core.preferences.UserPreferences;
import com.clem.ipoca1.core.service.download.AntennapodHttpClient;
import com.clem.ipoca1.event.AddEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by laileon on 2017/9/14.
 */

public class ChannelFragment extends Fragment {
    public static final String TAG = "ChannelFragment";

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;

    private List<ItunesAdapter.Podcast> topList;
    private Subscription subscription;
    private HomeAdapter mHomeAdapter;
    private String genre;

    public ChannelFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isAdded()) {
            return;
        }
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.channel, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.arts:
                    loadToplist("1301");
                    return true;
                case R.id.game:
                    loadToplist("1323");
                    return true;
                case R.id.culture:
                    loadToplist("1324");
                    return true;
                case R.id.business:
                    loadToplist("1321");
                    return true;
                case R.id.tv:
                    loadToplist("1309");
                    return true;
                case R.id.music:
                    loadToplist("1310");
                    return true;
                case R.id.news:
                    loadToplist("1311");
                    return true;
                case R.id.science:
                    loadToplist("1315");
                    return true;
                case R.id.sports:
                    loadToplist("1316");
                    return true;
                case R.id.tech:
                    loadToplist("1318");
                    return true;
                case R.id.comedy:
                    loadToplist("1303");
                    return true;
                case R.id.education:
                    loadToplist("1304");
                    return true;
                case R.id.kids:
                    loadToplist("1305");
                    return true;
                case R.id.health:
                    loadToplist("1307");
                    return true;
                case R.id.gov:
                    loadToplist("1325");
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (isAdded()) setHasOptionsMenu(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.channel_label);

        View rootView = inflater.inflate(R.layout.channel_fragment, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
        mRecyclerView.setHasFixedSize(true);
        mHomeAdapter = new HomeAdapter();
        mRecyclerView.setAdapter(mHomeAdapter);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    @Override
    public void onResume() {
        setData();
        renderView();
        super.onResume();
    }

    private void setData() {
        genre = UserPreferences.getPrefGenre();
        loadToplist(genre);
    }

    private void loadToplist(String genre) {
        String locale = TextUtils.isEmpty(UserPreferences.getPrefCountry()) ? Locale.getDefault().getCountry() : UserPreferences.getPrefCountry();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        mRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        subscription = Observable.create((Observable.OnSubscribe<List<ItunesAdapter.Podcast>>) subscriber -> {
            //test
            String url = "https://itunes.apple.com/" + locale + "/rss/toppodcasts/limit=50/explicit=true/genre=" + genre + "/json";
            OkHttpClient client = AntennapodHttpClient.getHttpClient();
            Request.Builder httpReq = new Request.Builder()
                    .url(url)
                    .header("User-Agent", ClientConfig.USER_AGENT);
            List<ItunesAdapter.Podcast> results = new ArrayList<>();
            try {
                Response response = client.newCall(httpReq.build()).execute();
                if (!response.isSuccessful()) {
                    // toplist for language does not exist, fall back to united states
                    url = "https://itunes.apple.com/us/rss/toppodcasts/limit=50/explicit=true/json";
                    httpReq = new Request.Builder()
                            .url(url)
                            .header("User-Agent", ClientConfig.USER_AGENT);
                    response = client.newCall(httpReq.build()).execute();
                }
                if (response.isSuccessful()) {
                    String resultString = response.body().string();
                    JSONObject result = new JSONObject(resultString);
                    JSONObject feed = result.getJSONObject("feed");
                    JSONArray entries = feed.getJSONArray("entry");

                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject json = entries.getJSONObject(i);
                        ItunesAdapter.Podcast podcast = ItunesAdapter.Podcast.fromToplist(json);
                        results.add(podcast);
                    }
                } else {
                    String prefix = getString(R.string.error_msg_prefix);
                    subscriber.onError(new IOException(prefix + response));
                }
            } catch (IOException | JSONException e) {
                subscriber.onError(e);
            }
            subscriber.onNext(results);
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(podcasts -> {
                    progressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    topList = podcasts;
                    Log.d(TAG, "loadToplist: " + topList.size());
                    updateData(topList);
                }, error -> {
                    Log.e(TAG, Log.getStackTraceString(error));
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void updateData(List<ItunesAdapter.Podcast> topList) {
        this.topList = topList;
        mHomeAdapter.notifyDataSetChanged();
    }

    private void renderView() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    getActivity()).inflate(R.layout.item_home, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Glide.with(getActivity())
                    .load(topList.get(position).imageUrl)
                    .diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY)
                    .centerCrop()
                    .dontAnimate()
                    .into(holder.mImageView);
            holder.mFrameLayout.setOnClickListener(v -> {
                if (!PodcastApp.isDoubleRequest()) {
                    EventBus.getDefault().post(new AddEvent(topList.get(position)));
                }
            });
        }

        @Override
        public int getItemCount() {
            return topList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView mImageView;
            FrameLayout mFrameLayout;

            public MyViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.image_iv);
                mFrameLayout = (FrameLayout) view.findViewById(R.id.item_fl);
            }
        }
    }
}
