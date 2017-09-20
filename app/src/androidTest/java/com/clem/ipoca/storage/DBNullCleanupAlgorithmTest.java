package com.clem.ipoca.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.FlakyTest;
import android.test.InstrumentationTestCase;

import com.clem.ipoca.core.feed.Feed;
import com.clem.ipoca.core.feed.FeedItem;
import com.clem.ipoca.core.feed.FeedMedia;
import com.clem.ipoca.core.preferences.UserPreferences;
import com.clem.ipoca.core.storage.DBTasks;
import com.clem.ipoca.core.storage.PodDBAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tests that the APNullCleanupAlgorithm is working correctly.
 */
public class DBNullCleanupAlgorithmTest extends InstrumentationTestCase {

    private static final String TAG = "DBNullCleanupAlgorithmTest";
    private static final int EPISODE_CACHE_SIZE = 5;

    private Context context;

    private File destFolder;

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        assertTrue(PodDBAdapter.deleteDatabase());

        cleanupDestFolder(destFolder);
        assertTrue(destFolder.delete());
    }

    private void cleanupDestFolder(File destFolder) {
        for (File f : destFolder.listFiles()) {
            assertTrue(f.delete());
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getTargetContext();
        destFolder = context.getExternalCacheDir();
        cleanupDestFolder(destFolder);
        assertNotNull(destFolder);
        assertTrue(destFolder.exists());
        assertTrue(destFolder.canWrite());

        // create new database
        PodDBAdapter.init(context);
        PodDBAdapter.deleteDatabase();
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.close();

        SharedPreferences.Editor prefEdit = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        prefEdit.putString(UserPreferences.PREF_EPISODE_CACHE_SIZE, Integer.toString(EPISODE_CACHE_SIZE));
        prefEdit.putString(UserPreferences.PREF_EPISODE_CLEANUP, Integer.toString(UserPreferences.EPISODE_CLEANUP_NULL));
        prefEdit.commit();

        UserPreferences.init(context);
    }

    /**
     * A test with no items in the queue, but multiple items downloaded.
     * The null algorithm should never delete any items, even if they're played and not in the queue.
     * @throws IOException
     */
    @FlakyTest(tolerance = 3)
    public void testPerformAutoCleanupShouldNotDelete() throws IOException {
        final int NUM_ITEMS = EPISODE_CACHE_SIZE * 2;

        Feed feed = new Feed("url", null, "title");
        List<FeedItem> items = new ArrayList<>();
        feed.setItems(items);
        List<File> files = new ArrayList<>();
        for (int i = 0; i < NUM_ITEMS; i++) {
            FeedItem item = new FeedItem(0, "title", "id", "link", new Date(), FeedItem.PLAYED, feed);

            File f = new File(destFolder, "file " + i);
            assertTrue(f.createNewFile());
            files.add(f);
            item.setMedia(new FeedMedia(0, item, 1, 0, 1L, "m", f.getAbsolutePath(), "url", true,
                    new Date(NUM_ITEMS - i), 0, 0));
            items.add(item);
        }

        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setCompleteFeed(feed);
        adapter.close();

        assertTrue(feed.getId() != 0);
        for (FeedItem item : items) {
            assertTrue(item.getId() != 0);
            assertTrue(item.getMedia().getId() != 0);
        }
        DBTasks.performAutoCleanup(context);
        for (int i = 0; i < files.size(); i++) {
            assertTrue(files.get(i).exists());
        }
    }
}
