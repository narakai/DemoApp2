package com.clem.ipocadonation1.core;

import com.clem.ipocadonation1.core.storage.AutomaticDownloadAlgorithm;
import com.clem.ipocadonation1.core.storage.EpisodeCleanupAlgorithm;

/**
 * Callbacks for the DBTasks class of the storage module.
 */
public interface DBTasksCallbacks {

    /**
     * Returns the client's implementation of the AutomaticDownloadAlgorithm interface.
     */
    AutomaticDownloadAlgorithm getAutomaticDownloadAlgorithm();

    /**
     * Returns the client's implementation of the EpisodeCacheCleanupAlgorithm interface.
     */
    EpisodeCleanupAlgorithm getEpisodeCacheCleanupAlgorithm();
}
