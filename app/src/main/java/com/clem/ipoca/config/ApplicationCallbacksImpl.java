package com.clem.ipoca.config;


import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.clem.ipoca.PodcastApp;
import com.clem.ipoca.activity.StorageErrorActivity;
import com.clem.ipoca.core.ApplicationCallbacks;

public class ApplicationCallbacksImpl implements ApplicationCallbacks {

    @Override
    public Application getApplicationInstance() {
        return PodcastApp.getInstance();
    }

    @Override
    public Intent getStorageErrorActivity(Context context) {
        return new Intent(context, StorageErrorActivity.class);
    }

}
