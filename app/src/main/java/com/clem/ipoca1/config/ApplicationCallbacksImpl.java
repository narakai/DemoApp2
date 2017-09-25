package com.clem.ipoca1.config;


import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.clem.ipoca1.PodcastApp;
import com.clem.ipoca1.activity.StorageErrorActivity;
import com.clem.ipoca1.core.ApplicationCallbacks;

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
