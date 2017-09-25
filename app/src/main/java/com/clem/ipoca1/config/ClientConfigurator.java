package com.clem.ipoca1.config;

import com.clem.ipoca1.core.ClientConfig;

/**
 * Configures the ClientConfig class of the core package.
 */
public class ClientConfigurator {

    static {
//        ClientConfig.USER_AGENT = "AntennaPod/" + BuildConfig.VERSION_NAME;
        ClientConfig.USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";
        ClientConfig.applicationCallbacks = new ApplicationCallbacksImpl();
        ClientConfig.downloadServiceCallbacks = new DownloadServiceCallbacksImpl();
        ClientConfig.gpodnetCallbacks = new GpodnetCallbacksImpl();
        ClientConfig.playbackServiceCallbacks = new PlaybackServiceCallbacksImpl();
        ClientConfig.flattrCallbacks = new FlattrCallbacksImpl();
        ClientConfig.dbTasksCallbacks = new DBTasksCallbacksImpl();
        ClientConfig.castCallbacks = new CastCallbackImpl();
    }
}
