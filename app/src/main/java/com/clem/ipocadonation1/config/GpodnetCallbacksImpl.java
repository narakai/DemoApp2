package com.clem.ipocadonation1.config;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.clem.ipocadonation1.activity.MainActivity;
import com.clem.ipocadonation1.core.GpodnetCallbacks;


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
