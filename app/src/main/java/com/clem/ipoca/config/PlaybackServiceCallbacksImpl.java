package com.clem.ipoca.config;

import android.content.Context;
import android.content.Intent;

import com.clem.ipoca.R;
import com.clem.ipoca.activity.AudioplayerActivity;
import com.clem.ipoca.activity.CastplayerActivity;
import com.clem.ipoca.activity.VideoplayerActivity;
import com.clem.ipoca.core.PlaybackServiceCallbacks;
import com.clem.ipoca.core.feed.MediaType;


public class PlaybackServiceCallbacksImpl implements PlaybackServiceCallbacks {
    @Override
    public Intent getPlayerActivityIntent(Context context, MediaType mediaType, boolean remotePlayback) {
        if (remotePlayback) {
            return new Intent(context, CastplayerActivity.class);
        }
        if (mediaType == MediaType.VIDEO) {
            return new Intent(context, VideoplayerActivity.class);
        } else {
            return new Intent(context, AudioplayerActivity.class);
        }
    }

    @Override
    public boolean useQueue() {
        return true;
    }

    @Override
    public int getNotificationIconResource(Context context) {
        return R.drawable.ic_launcher;
    }
}
