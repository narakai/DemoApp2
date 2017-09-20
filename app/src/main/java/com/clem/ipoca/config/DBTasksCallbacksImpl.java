package com.clem.ipoca.config;

import com.clem.ipoca.core.DBTasksCallbacks;
import com.clem.ipoca.core.preferences.UserPreferences;
import com.clem.ipoca.core.storage.APDownloadAlgorithm;
import com.clem.ipoca.core.storage.AutomaticDownloadAlgorithm;
import com.clem.ipoca.core.storage.EpisodeCleanupAlgorithm;

public class DBTasksCallbacksImpl implements DBTasksCallbacks {

    @Override
    public AutomaticDownloadAlgorithm getAutomaticDownloadAlgorithm() {
        return new APDownloadAlgorithm();
    }

    @Override
    public EpisodeCleanupAlgorithm getEpisodeCacheCleanupAlgorithm() {
        return UserPreferences.getEpisodeCleanupAlgorithm();
    }
}
