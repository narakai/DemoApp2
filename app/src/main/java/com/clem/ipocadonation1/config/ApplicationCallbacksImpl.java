package com.clem.ipocadonation1.config;


import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.clem.ipocadonation1.PodcastApp;
import com.clem.ipocadonation1.activity.StorageErrorActivity;
import com.clem.ipocadonation1.core.ApplicationCallbacks;

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
