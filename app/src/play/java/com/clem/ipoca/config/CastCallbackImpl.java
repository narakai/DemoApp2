package com.clem.ipoca.config;

import android.support.annotation.NonNull;
import android.support.v7.app.MediaRouteControllerDialogFragment;
import android.support.v7.app.MediaRouteDialogFactory;

import com.clem.ipoca.core.CastCallbacks;
import com.clem.ipoca.fragment.CustomMRControllerDialogFragment;

public class CastCallbackImpl implements CastCallbacks {
    @Override
    public MediaRouteDialogFactory getMediaRouterDialogFactory() {
        return new MediaRouteDialogFactory() {
            @NonNull
            @Override
            public MediaRouteControllerDialogFragment onCreateControllerDialogFragment() {
                return new CustomMRControllerDialogFragment();
            }
        };
    }
}
