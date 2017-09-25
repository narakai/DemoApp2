package com.clem.ipocadonation1.config;

import com.clem.ipocadonation1.core.DBTasksCallbacks;
import com.clem.ipocadonation1.core.preferences.UserPreferences;
import com.clem.ipocadonation1.core.storage.APDownloadAlgorithm;
import com.clem.ipocadonation1.core.storage.AutomaticDownloadAlgorithm;
import com.clem.ipocadonation1.core.storage.EpisodeCleanupAlgorithm;

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
