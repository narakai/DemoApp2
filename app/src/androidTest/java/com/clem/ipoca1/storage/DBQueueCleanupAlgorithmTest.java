package com.clem.ipoca1.storage;

import android.test.FlakyTest;

import com.clem.ipoca1.core.feed.Feed;
import com.clem.ipoca1.core.feed.FeedItem;
import com.clem.ipoca1.core.preferences.UserPreferences;
import com.clem.ipoca1.core.storage.DBTasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests that the APQueueCleanupAlgorithm is working correctly.
 */
public class DBQueueCleanupAlgorithmTest extends DBCleanupTests {

    private static final String TAG = "DBQueueCleanupAlgorithmTest";

    public DBQueueCleanupAlgorithmTest() {
        super(UserPreferences.EPISODE_CLEANUP_QUEUE);
    }

    /**
     * For APQueueCleanupAlgorithm we expect even unplayed episodes to be deleted if needed
     * if they aren't in the queue
     */
    @FlakyTest(tolerance = 3)
    public void testPerformAutoCleanupHandleUnplayed() throws IOException {
        final int NUM_ITEMS = EPISODE_CACHE_SIZE * 2;

        Feed feed = new Feed("url", null, "title");
        List<FeedItem> items = new ArrayList<>();
        feed.setItems(items);
        List<File> files = new ArrayList<>();
        populateItems(NUM_ITEMS, feed, items, files, FeedItem.UNPLAYED, false, false);

        DBTasks.performAutoCleanup(context);
        for (int i = 0; i < files.size(); i++) {
            if (i < EPISODE_CACHE_SIZE) {
                assertTrue(files.get(i).exists());
            } else {
                assertFalse(files.get(i).exists());
            }
        }
    }
}
