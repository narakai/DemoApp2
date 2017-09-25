package com.clem.ipoca1.config;

import com.clem.ipoca1.core.DBTasksCallbacks;
import com.clem.ipoca1.core.preferences.UserPreferences;
import com.clem.ipoca1.core.storage.APDownloadAlgorithm;
import com.clem.ipoca1.core.storage.AutomaticDownloadAlgorithm;
import com.clem.ipoca1.core.storage.EpisodeCleanupAlgorithm;

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
