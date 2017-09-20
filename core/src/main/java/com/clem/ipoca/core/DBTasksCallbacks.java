package com.clem.ipoca.core;

import com.clem.ipoca.core.storage.AutomaticDownloadAlgorithm;
import com.clem.ipoca.core.storage.EpisodeCleanupAlgorithm;

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
