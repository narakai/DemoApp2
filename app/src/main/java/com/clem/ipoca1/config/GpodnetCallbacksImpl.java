package com.clem.ipoca1.config;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.clem.ipoca1.activity.MainActivity;
import com.clem.ipoca1.core.GpodnetCallbacks;


public class GpodnetCallbacksImpl implements GpodnetCallbacks {
    @Override
    public boolean gpodnetEnabled() {
        return true;
    }

    @Override
    public PendingIntent getGpodnetSyncServiceErrorNotificationPendingIntent(Context context) {
        return PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
