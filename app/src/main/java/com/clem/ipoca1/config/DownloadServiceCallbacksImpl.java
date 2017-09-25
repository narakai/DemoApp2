package com.clem.ipoca1.config;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.clem.ipoca1.activity.DownloadAuthenticationActivity;
import com.clem.ipoca1.activity.MainActivity;
import com.clem.ipoca1.adapter.NavListAdapter;
import com.clem.ipoca1.core.DownloadServiceCallbacks;
import com.clem.ipoca1.core.feed.Feed;
import com.clem.ipoca1.core.service.download.DownloadRequest;
import com.clem.ipoca1.fragment.DownloadsFragment;


public class DownloadServiceCallbacksImpl implements DownloadServiceCallbacks {

    @Override
    public PendingIntent getNotificationContentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_NAV_TYPE, NavListAdapter.VIEW_TYPE_NAV);
        intent.putExtra(MainActivity.EXTRA_FRAGMENT_TAG, DownloadsFragment.TAG);
        Bundle args = new Bundle();
        args.putInt(DownloadsFragment.ARG_SELECTED_TAB, DownloadsFragment.POS_RUNNING);
        intent.putExtra(MainActivity.EXTRA_FRAGMENT_ARGS, args);

        return PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public PendingIntent getAuthentificationNotificationContentIntent(Context context, DownloadRequest request) {
        final Intent activityIntent = new Intent(context.getApplicationContext(), DownloadAuthenticationActivity.class);
        activityIntent.putExtra(DownloadAuthenticationActivity.ARG_DOWNLOAD_REQUEST, request);
        activityIntent.putExtra(DownloadAuthenticationActivity.ARG_SEND_TO_DOWNLOAD_REQUESTER_BOOL, true);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);
    }

    @Override
    public PendingIntent getReportNotificationContentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_NAV_TYPE, NavListAdapter.VIEW_TYPE_NAV);
        intent.putExtra(MainActivity.EXTRA_FRAGMENT_TAG, DownloadsFragment.TAG);
        Bundle args = new Bundle();
        args.putInt(DownloadsFragment.ARG_SELECTED_TAB, DownloadsFragment.POS_LOG);
        intent.putExtra(MainActivity.EXTRA_FRAGMENT_ARGS, args);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onFeedParsed(Context context, Feed feed) {
        // do nothing
    }

    @Override
    public boolean shouldCreateReport() {
        return true;
    }
}
