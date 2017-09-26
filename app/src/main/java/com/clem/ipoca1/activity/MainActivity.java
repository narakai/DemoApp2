package com.clem.ipoca1.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.CircleView;
import com.bumptech.glide.Glide;
import com.clem.ipoca1.R;
import com.clem.ipoca1.adapter.NavListAdapter;
import com.clem.ipoca1.adapter.itunes.ItunesAdapter;
import com.clem.ipoca1.core.ClientConfig;
import com.clem.ipoca1.core.asynctask.FeedRemover;
import com.clem.ipoca1.core.dialog.ConfirmationDialog;
import com.clem.ipoca1.core.event.MessageEvent;
import com.clem.ipoca1.core.event.ProgressEvent;
import com.clem.ipoca1.core.event.QueueEvent;
import com.clem.ipoca1.core.feed.EventDistributor;
import com.clem.ipoca1.core.feed.Feed;
import com.clem.ipoca1.core.preferences.PlaybackPreferences;
import com.clem.ipoca1.core.preferences.UserPreferences;
import com.clem.ipoca1.core.service.download.AntennapodHttpClient;
import com.clem.ipoca1.core.service.playback.PlaybackService;
import com.clem.ipoca1.core.storage.DBReader;
import com.clem.ipoca1.core.storage.DBTasks;
import com.clem.ipoca1.core.storage.DBWriter;
import com.clem.ipoca1.core.util.FeedItemUtil;
import com.clem.ipoca1.core.util.Flavors;
import com.clem.ipoca1.core.util.StorageUtils;
import com.clem.ipoca1.dialog.RatingDialog;
import com.clem.ipoca1.dialog.RenameFeedDialog;
import com.clem.ipoca1.event.AddEvent;
import com.clem.ipoca1.event.ColorEvent;
import com.clem.ipoca1.fragment.AddFeedFragment;
import com.clem.ipoca1.fragment.ChannelFragment;
import com.clem.ipoca1.fragment.DownloadsFragment;
import com.clem.ipoca1.fragment.EpisodesFragment;
import com.clem.ipoca1.fragment.ExternalPlayerFragment;
import com.clem.ipoca1.fragment.ItemlistFragment;
import com.clem.ipoca1.fragment.PlaybackHistoryFragment;
import com.clem.ipoca1.fragment.QueueFragment;
import com.clem.ipoca1.fragment.SubscriptionFragment;
import com.clem.ipoca1.menuhandler.NavDrawerActivity;
import com.clem.ipoca1.preferences.PreferenceController;
import com.clem.ipoca1.view.SupportDialog;
import com.google.android.gms.ads.AdView;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;

/**
 * The activity that is shown when the user launches the app.
 */
public class MainActivity extends CastEnabledActivity implements NavDrawerActivity {

    private static final String TAG = "MainActivity";

    private static final int EVENTS = EventDistributor.FEED_LIST_UPDATE
            | EventDistributor.UNREAD_ITEMS_UPDATE;

    public static final String PREF_NAME = "MainActivityPrefs";
    public static final String PREF_IS_FIRST_LAUNCH = "prefMainActivityIsFirstLaunch";
    public static final String PREF_LAST_FRAGMENT_TAG = "prefMainActivityLastFragmentTag";

    public static final String EXTRA_NAV_TYPE = "nav_type";
    public static final String EXTRA_NAV_INDEX = "nav_index";
    public static final String EXTRA_FRAGMENT_TAG = "fragment_tag";
    public static final String EXTRA_FRAGMENT_ARGS = "fragment_args";
    public static final String EXTRA_FEED_ID = "fragment_feed_id";

    public static final String SAVE_BACKSTACK_COUNT = "backstackCount";
    public static final String SAVE_TITLE = "title";

    public static final String[] NAV_DRAWER_TAGS = {
            ChannelFragment.TAG,
            QueueFragment.TAG,
            EpisodesFragment.TAG,
            SubscriptionFragment.TAG,
            DownloadsFragment.TAG,
            PlaybackHistoryFragment.TAG,
            AddFeedFragment.TAG,
            SupportDialog.TAG,
            NavListAdapter.SUBSCRIPTION_LIST_TAG
    };

    private Toolbar toolbar;
    private ExternalPlayerFragment externalPlayerFragment;
    private FrameLayout playerFragment;
    private DrawerLayout drawerLayout;

    private View navDrawer;
    private ListView navList;
    private NavListAdapter navAdapter;
    private int mPosition = -1;

    private ActionBarDrawerToggle drawerToggle;

    private CharSequence currentTitle;

    private ProgressDialog pd;

    private Subscription subscription;
    private int primaryPreselect;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(UserPreferences.getNoTitleTheme());
        StorageUtils.checkStorageAvailability(this);
        setContentView(R.layout.main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            findViewById(R.id.shadow).setVisibility(View.GONE);
//            int elevation = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
//                    getResources().getDisplayMetrics());
//            getSupportActionBar().setElevation(elevation);
//        }

        currentTitle = getTitle();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.nav_list);
        navDrawer = findViewById(R.id.nav_layout);
        mAdView = (AdView) findViewById(R.id.adView);
        playerFragment = (FrameLayout) findViewById(R.id.playerFragment);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        if (savedInstanceState != null) {
            int backstackCount = savedInstanceState.getInt(SAVE_BACKSTACK_COUNT, 0);
            drawerToggle.setDrawerIndicatorEnabled(backstackCount == 0);
        }
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        final FragmentManager fm = getSupportFragmentManager();

        fm.addOnBackStackChangedListener(() -> drawerToggle.setDrawerIndicatorEnabled(fm.getBackStackEntryCount() == 0));

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        navAdapter = new NavListAdapter(itemAccess, this);
        navList.setAdapter(navAdapter);
        navList.setOnItemClickListener(navListClickListener);
        navList.setOnItemLongClickListener(newListLongClickListener);
        registerForContextMenu(navList);

        navAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                selectedNavListIndex = getSelectedNavListIndex();
            }
        });

        View view = findViewById(R.id.nav_settings);
        if (view != null){
            view.setOnClickListener(v -> {
                drawerLayout.closeDrawer(navDrawer);
                startActivity(new Intent(MainActivity.this, PreferenceController.getPreferenceActivity()));
            });
        }

        FragmentTransaction transaction = fm.beginTransaction();

        Fragment mainFragment = fm.findFragmentByTag("main");
        if (mainFragment != null) {
            transaction.replace(R.id.main_view, mainFragment);
        } else {
            String lastFragment = getLastNavFragment();
            if (ArrayUtils.contains(NAV_DRAWER_TAGS, lastFragment)) {
                loadFragment(lastFragment, null);
            } else {
                try {
                    loadFeedFragmentById(Integer.parseInt(lastFragment), null);
                } catch (NumberFormatException e) {
                    // it's not a number, this happens if we removed
                    // a label from the NAV_DRAWER_TAGS
                    // give them a nice default...
                    loadFragment(QueueFragment.TAG, null);
                }
            }
            externalPlayerFragment = new ExternalPlayerFragment();
            transaction.replace(R.id.playerFragment, externalPlayerFragment, ExternalPlayerFragment.TAG);
            transaction.commit();
//            if (lastFragment.equals(QueueFragment.TAG) || lastFragment.equals(EpisodesFragment.TAG)) {
//                playerFragment.setVisibility(View.VISIBLE);
//            } else {
//                playerFragment.setVisibility(GONE);
//            }
        }

        checkFirstLaunch();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this.getWindow() != null) {
                this.getWindow().setStatusBarColor(getResources().getColor(R.color.black));
            }
        }
        primaryPreselect = UserPreferences.getPrefColor();
        ColorDrawable drawable = new ColorDrawable(primaryPreselect);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(drawable);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(primaryPreselect));
            getWindow().setNavigationBarColor(primaryPreselect);
        }
        mAdView.setVisibility(GONE);
//        MobileAds.initialize(this, "ca-app-pub-8223458858460367~1304956702");
        //        if (!UserPreferences.getBuyme()) {
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
//        } else {
//            mAdView.setVisibility(GONE);
//        }
    }


    private void saveLastNavFragment(String tag) {
        Log.d(TAG, "saveLastNavFragment(tag: " + tag + ")");
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        if (tag != null) {
            edit.putString(PREF_LAST_FRAGMENT_TAG, tag);
        } else {
            edit.remove(PREF_LAST_FRAGMENT_TAG);
        }
        edit.apply();
    }

    private String getLastNavFragment() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String lastFragment = prefs.getString(PREF_LAST_FRAGMENT_TAG, ChannelFragment.TAG);
        Log.d(TAG, "getLastNavFragment() -> " + lastFragment);
        return lastFragment;
    }

    private void checkFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(PREF_IS_FIRST_LAUNCH, true)) {
//            new Handler().postDelayed(() -> drawerLayout.openDrawer(navDrawer), 1500);

            // for backward compatibility, we only change defaults for fresh installs
            UserPreferences.setUpdateInterval(12);

            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(PREF_IS_FIRST_LAUNCH, false);
            edit.apply();
        }
    }

    public void showDrawerPreferencesDialog() {
        final List<String> hiddenDrawerItems = UserPreferences.getHiddenDrawerItems();
        String[] navLabels = new String[NAV_DRAWER_TAGS.length];
        final boolean[] checked = new boolean[NAV_DRAWER_TAGS.length];
        for (int i = 0; i < NAV_DRAWER_TAGS.length; i++) {
            String tag = NAV_DRAWER_TAGS[i];
            navLabels[i] = navAdapter.getLabel(tag);
            if (!hiddenDrawerItems.contains(tag)) {
                checked[i] = true;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.drawer_preferences);
        builder.setMultiChoiceItems(navLabels, checked, (dialog, which, isChecked) -> {
            if (isChecked) {
                hiddenDrawerItems.remove(NAV_DRAWER_TAGS[which]);
            } else {
                hiddenDrawerItems.add(NAV_DRAWER_TAGS[which]);
            }
        });
        builder.setPositiveButton(R.string.confirm_label, (dialog, which) -> UserPreferences.setHiddenDrawerItems(hiddenDrawerItems));
        builder.setNegativeButton(R.string.cancel_label, null);
        builder.create().show();
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && navDrawer != null && drawerLayout.isDrawerOpen(navDrawer);
    }

    public List<Feed> getFeeds() {
        return (navDrawerData != null) ? navDrawerData.feeds : null;
    }

    public void loadFragment(int index, Bundle args) {
        Log.d(TAG, "loadFragment(index: " + index + ", args: " + args + ")");
        if (index < navAdapter.getSubscriptionOffset()) {
            String tag = navAdapter.getTags().get(index);
            loadFragment(tag, args);
        } else {
            int pos = index - navAdapter.getSubscriptionOffset();
            loadFeedFragmentByPosition(pos, args);
        }
    }

    public void loadFragment(String tag, Bundle args) {
        Log.d(TAG, "loadFragment(tag: " + tag + ", args: " + args + ")");
        Fragment fragment = null;
        switch (tag) {
            case ChannelFragment.TAG:
                fragment = new ChannelFragment();
                break;
            case QueueFragment.TAG:
                fragment = new QueueFragment();
                break;
            case EpisodesFragment.TAG:
                fragment = new EpisodesFragment();
                break;
            case SubscriptionFragment.TAG:
                SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
                fragment = subscriptionFragment;
                break;
            case DownloadsFragment.TAG:
                fragment = new DownloadsFragment();
                break;
            case PlaybackHistoryFragment.TAG:
                fragment = new PlaybackHistoryFragment();
                break;
            case AddFeedFragment.TAG:
                fragment = new AddFeedFragment();
                break;
            case SupportDialog.TAG:
                SupportDialog.show(MainActivity.this);
                break;
            default:
                // default to the queue
                tag = ChannelFragment.TAG;
                fragment = new ChannelFragment();
                args = null;
                break;
        }
        if (tag.equals(SupportDialog.TAG)) return;
        currentTitle = navAdapter.getLabel(tag);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(currentTitle);
        saveLastNavFragment(tag);
        if (args != null) {
            fragment.setArguments(args);
        }
        loadFragment(fragment);
    }

    private void loadFeedFragmentByPosition(int relPos, Bundle args) {
        if (relPos < 0) {
            return;
        }
        Feed feed = itemAccess.getItem(relPos);
        loadFeedFragmentById(feed.getId(), args);
    }

    public void loadFeedFragmentById(long feedId, Bundle args) {
        Fragment fragment = ItemlistFragment.newInstance(feedId);
        if (args != null) {
            fragment.setArguments(args);
        }
        saveLastNavFragment(String.valueOf(feedId));
        currentTitle = "";
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(currentTitle);
        loadFragment(fragment);
    }

    private void loadFragment(Fragment fragment) {
//        if (fragment.getTag().equals(QueueFragment.TAG) || )
        FragmentManager fragmentManager = getSupportFragmentManager();
        // clear back stack
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(R.id.main_view, fragment, "main");
        fragmentManager.popBackStack();
        // TODO: we have to allow state loss here
        // since this function can get called from an AsyncTask which
        // could be finishing after our app has already committed state
        // and is about to get shutdown.  What we *should* do is
        // not commit anything in an AsyncTask, but that's a bigger
        // change than we want now.
        t.commitAllowingStateLoss();
        if (navAdapter != null) {
            navAdapter.notifyDataSetChanged();
        }
    }

    public void loadChildFragment(Fragment fragment) {
        Log.d(TAG, "loadChildFragment: " + fragment.getTag());
        Validate.notNull(fragment);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.main_view, fragment, "main")
                .addToBackStack(null)
                .commit();
    }

    public void dismissChildFragment() {
        getSupportFragmentManager().popBackStack();
    }

    private int getSelectedNavListIndex() {
        String currentFragment = getLastNavFragment();
        if (currentFragment == null) {
            // should not happen, but better safe than sorry
            return -1;
        }
        int tagIndex = navAdapter.getTags().indexOf(currentFragment);
        if (tagIndex >= 0) {
            return tagIndex;
        } else if (ArrayUtils.contains(NAV_DRAWER_TAGS, currentFragment)) {
            // the fragment was just hidden
            return -1;
        } else { // last fragment was not a list, but a feed
            long feedId = Long.parseLong(currentFragment);
            if (navDrawerData != null) {
                List<Feed> feeds = navDrawerData.feeds;
                for (int i = 0; i < feeds.size(); i++) {
                    if (feeds.get(i).getId() == feedId) {
                        return i + navAdapter.getSubscriptionOffset();
                    }
                }
            }
            return -1;
        }
    }

    private AdapterView.OnItemClickListener navListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int viewType = parent.getAdapter().getItemViewType(position);
            if (viewType != NavListAdapter.VIEW_TYPE_SECTION_DIVIDER && position != selectedNavListIndex) {
                loadFragment(position, null);
            }
            drawerLayout.closeDrawer(navDrawer);
        }
    };

    private AdapterView.OnItemLongClickListener newListLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < navAdapter.getTags().size()) {
                showDrawerPreferencesDialog();
                return true;
            } else {
                mPosition = position;
                return false;
            }
        }
    };


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        if (savedInstanceState != null) {
            currentTitle = savedInstanceState.getString(SAVE_TITLE);
            if (!drawerLayout.isDrawerOpen(navDrawer)) {
                getSupportActionBar().setTitle(currentTitle);
            }
            selectedNavListIndex = getSelectedNavListIndex();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getSupportActionBar() != null){
            outState.putString(SAVE_TITLE, getSupportActionBar().getTitle().toString());
        }
        outState.putInt(SAVE_BACKSTACK_COUNT, getSupportFragmentManager().getBackStackEntryCount());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventDistributor.getInstance().register(contentUpdate);
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        RatingDialog.init(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StorageUtils.checkStorageAvailability(this);
        DBTasks.checkShouldRefreshFeeds(getApplicationContext());

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_FEED_ID) ||
                (navDrawerData != null && intent.hasExtra(EXTRA_NAV_TYPE) &&
                        (intent.hasExtra(EXTRA_NAV_INDEX) || intent.hasExtra(EXTRA_FRAGMENT_TAG)))) {
            handleNavIntent();
        }
        loadData();
        RatingDialog.check();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventDistributor.getInstance().unregister(contentUpdate);
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (pd != null) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean retVal = super.onCreateOptionsMenu(menu);
        if (Flavors.FLAVOR == Flavors.PLAY) {
            switch (getLastNavFragment()) {
                case QueueFragment.TAG:
                case EpisodesFragment.TAG:
                    requestCastButton(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    return retVal;
                case DownloadsFragment.TAG:
                case PlaybackHistoryFragment.TAG:
                case AddFeedFragment.TAG:
                case SubscriptionFragment.TAG:
                case ChannelFragment.TAG:
                    return retVal;
                default:
                    requestCastButton(MenuItem.SHOW_AS_ACTION_NEVER);
                    return retVal;
            }
        } else {
            return retVal;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                dismissChildFragment();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() != R.id.nav_list) {
            return;
        }
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = adapterInfo.position;
        if (position < navAdapter.getSubscriptionOffset()) {
            return;
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_feed_context, menu);
        Feed feed = navDrawerData.feeds.get(position - navAdapter.getSubscriptionOffset());
        menu.setHeaderTitle(feed.getTitle());
        // episodes are not loaded, so we cannot check if the podcast has new or unplayed ones!
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = mPosition;
        mPosition = -1; // reset
        if (position < 0) {
            return false;
        }
        Feed feed = navDrawerData.feeds.get(position - navAdapter.getSubscriptionOffset());
        switch (item.getItemId()) {
            case R.id.mark_all_seen_item:
                DBWriter.markFeedSeen(feed.getId());
                return true;
            case R.id.mark_all_read_item:
                DBWriter.markFeedRead(feed.getId());
                return true;
            case R.id.rename_item:
                new RenameFeedDialog(this, feed).show();
                return true;
            case R.id.remove_item:
                final FeedRemover remover = new FeedRemover(this, feed) {
                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        if (getSelectedNavListIndex() == position) {
                            loadFragment(EpisodesFragment.TAG, null);
                        }
                    }
                };
                ConfirmationDialog conDialog = new ConfirmationDialog(this,
                        R.string.remove_feed_label,
                        getString(R.string.feed_delete_confirmation_msg, feed.getTitle())) {
                    @Override
                    public void onConfirmButtonPressed(
                            DialogInterface dialog) {
                        dialog.dismiss();
                        long mediaId = PlaybackPreferences.getCurrentlyPlayingFeedMediaId();
                        if (mediaId > 0 &&
                                FeedItemUtil.indexOfItemWithMediaId(feed.getItems(), mediaId) >= 0) {
                            Log.d(TAG, "Currently playing episode is about to be deleted, skipping");
                            remover.skipOnCompletion = true;
                            int playerStatus = PlaybackPreferences.getCurrentPlayerStatus();
                            if (playerStatus == PlaybackPreferences.PLAYER_STATUS_PLAYING) {
                                sendBroadcast(new Intent(
                                        PlaybackService.ACTION_PAUSE_PLAY_CURRENT_EPISODE));
                            }
                        }
                        remover.executeAsync();
                    }
                };
                conDialog.createNewDialog().show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            drawerLayout.closeDrawer(navDrawer);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), R.string
                            .confirm_exit_app, Snackbar.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
                return;
            }
            super.onBackPressed();
        }
    }

    private DBReader.NavDrawerData navDrawerData;
    private int selectedNavListIndex = 0;

    private NavListAdapter.ItemAccess itemAccess = new NavListAdapter.ItemAccess() {
        @Override
        public int getCount() {
            if (navDrawerData != null) {
                return navDrawerData.feeds.size();
            } else {
                return 0;
            }
        }

        @Override
        public Feed getItem(int position) {
            if (navDrawerData != null && 0 <= position && position < navDrawerData.feeds.size()) {
                return navDrawerData.feeds.get(position);
            } else {
                return null;
            }
        }

        @Override
        public int getSelectedItemIndex() {
            return selectedNavListIndex;
        }

        @Override
        public int getQueueSize() {
            return (navDrawerData != null) ? navDrawerData.queueSize : 0;
        }

        @Override
        public int getNumberOfNewItems() {
            return (navDrawerData != null) ? navDrawerData.numNewItems : 0;
        }

        @Override
        public int getNumberOfDownloadedItems() {
            return (navDrawerData != null) ? navDrawerData.numDownloadedItems : 0;
        }

        @Override
        public int getReclaimableItems() {
            return (navDrawerData != null) ? navDrawerData.reclaimableSpace : 0;
        }

        @Override
        public int getFeedCounter(long feedId) {
            return navDrawerData != null ? navDrawerData.feedCounters.get(feedId) : 0;
        }

        @Override
        public int getFeedCounterSum() {
            if (navDrawerData == null) {
                return 0;
            }
            int sum = 0;
            for (int counter : navDrawerData.feedCounters.values()) {
                sum += counter;
            }
            return sum;
        }

    };

    private void loadData() {
        subscription = Observable.fromCallable(DBReader::getNavDrawerData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    boolean handleIntent = (navDrawerData == null);

                    navDrawerData = result;
                    navAdapter.notifyDataSetChanged();

                    if (handleIntent) {
                        handleNavIntent();
                    }
                }, error -> Log.e(TAG, Log.getStackTraceString(error)));
    }

    @Subscribe
    public void onEvent(QueueEvent event) {
        Log.d(TAG, "onEvent(" + event + ")");
        // we are only interested in the number of queue items, not download status or position
        if (event.action == QueueEvent.Action.DELETED_MEDIA ||
                event.action == QueueEvent.Action.SORTED ||
                event.action == QueueEvent.Action.MOVED) {
            return;
        }
        loadData();
    }

    @Subscribe
    public void onEventMainThread(ProgressEvent event) {
        Log.d(TAG, "onEvent(" + event + ")");
        switch (event.action) {
            case START:
                pd = new ProgressDialog(this);
                pd.setMessage(event.message);
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();
                break;
            case END:
                if (pd != null) {
                    pd.dismiss();
                }
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        Log.d(TAG, "onEvent(" + event + ")");
        View parentLayout = findViewById(R.id.drawer_layout);
        Snackbar snackbar = Snackbar.make(parentLayout, event.message, Snackbar.LENGTH_SHORT);
        if (event.action != null) {
            snackbar.setAction(getString(R.string.undo), v -> {
                event.action.run();
            });
        }
        snackbar.show();
    }

    @Subscribe
    public void onColorEvent(ColorEvent event) {
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

    @Subscribe
    public void onAddEvent(AddEvent event) {
        Log.d(TAG, "onAddEvent: ");
        ItunesAdapter.Podcast podcast = event.mPodcast;
        if (!podcast.feedUrl.contains("itunes.apple.com")) {
            Log.d(TAG, "onAddEvent: 1");
            Intent intent = new Intent(this, OnlineFeedViewActivity.class);
            intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, podcast.feedUrl);
            intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, "iTunes");
            startActivity(intent);
        } else {
            Log.d(TAG, "onAddEvent: 2");
            subscription = Observable.create((Observable.OnSubscribe<String>) subscriber -> {
                OkHttpClient client = AntennapodHttpClient.getHttpClient();
                Request.Builder httpReq = new Request.Builder()
                        .url(podcast.feedUrl)
                        .header("User-Agent", ClientConfig.USER_AGENT);
                try {
                    Response response = client.newCall(httpReq.build()).execute();
                    if (response.isSuccessful()) {
                        String resultString = response.body().string();
                        JSONObject result = new JSONObject(resultString);
                        JSONObject results = result.getJSONArray("results").getJSONObject(0);
                        String feedUrl = results.getString("feedUrl");
                        subscriber.onNext(feedUrl);
                    } else {
                        String prefix = getString(R.string.error_msg_prefix);
                        subscriber.onError(new IOException(prefix + response));
                    }
                } catch (IOException | JSONException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(feedUrl -> {
                        Intent intent = new Intent(this, OnlineFeedViewActivity.class);
                        intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, feedUrl);
                        intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, "iTunes");
                        startActivity(intent);
                    }, error -> {
                        Log.e(TAG, Log.getStackTraceString(error));
                        String prefix = getString(R.string.error_msg_prefix);
                        new MaterialDialog.Builder(this)
                                .content(prefix + " " + error.getMessage())
                                .neutralText(android.R.string.ok)
                                .show();
                    });
        }
    }


    private EventDistributor.EventListener contentUpdate = new EventDistributor.EventListener() {

        @Override
        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((EVENTS & arg) != 0) {
                Log.d(TAG, "Received contentUpdate Intent.");
                loadData();
            }
        }
    };

    private void handleNavIntent() {
        Log.d(TAG, "handleNavIntent()");
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_FEED_ID) ||
                (intent.hasExtra(EXTRA_NAV_TYPE) &&
                        (intent.hasExtra(EXTRA_NAV_INDEX) || intent.hasExtra(EXTRA_FRAGMENT_TAG)))) {
            int index = intent.getIntExtra(EXTRA_NAV_INDEX, -1);
            String tag = intent.getStringExtra(EXTRA_FRAGMENT_TAG);
            Bundle args = intent.getBundleExtra(EXTRA_FRAGMENT_ARGS);
            long feedId = intent.getLongExtra(EXTRA_FEED_ID, 0);
            if (index >= 0) {
                loadFragment(index, args);
            } else if (tag != null) {
                loadFragment(tag, args);
            } else if (feedId > 0) {
                loadFeedFragmentById(feedId, args);
            }
        }
        setIntent(new Intent(MainActivity.this, MainActivity.class)); // to avoid handling the intent twice when the configuration changes
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
