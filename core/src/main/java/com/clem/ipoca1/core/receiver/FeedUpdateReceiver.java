package com.clem.ipoca1.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clem.ipoca1.core.ClientConfig;
import com.clem.ipoca1.core.preferences.UserPreferences;
import com.clem.ipoca1.core.storage.DBTasks;
import com.clem.ipoca1.core.util.NetworkUtils;

/**
 * Refreshes all feeds when it receives an intent
 */
public class FeedUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "FeedUpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent");
        ClientConfig.initialize(context);
        if (NetworkUtils.networkAvailable() && NetworkUtils.isDownloadAllowed()) {
            DBTasks.refreshAllFeeds(context, null);
        } else {
            Log.d(TAG, "Blocking automatic update: no wifi available / no mobile updates allowed");
        }
        UserPreferences.restartUpdateAlarm(false);
    }

}
