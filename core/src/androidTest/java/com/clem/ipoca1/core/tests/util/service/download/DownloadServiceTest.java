package com.clem.ipoca1.core.tests.util.service.download;

import android.test.AndroidTestCase;

import com.clem.ipoca1.core.feed.Feed;
import com.clem.ipoca1.core.feed.FeedImage;
import com.clem.ipoca1.core.feed.FeedItem;
import com.clem.ipoca1.core.service.download.DownloadService;

import java.util.ArrayList;
import java.util.List;

public class DownloadServiceTest extends AndroidTestCase {

    public void testRemoveDuplicateImages() {
        List<FeedItem> items = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            FeedItem item = new FeedItem();
            String url = (i % 5 == 0) ? "dupe_url" : String.format("url_%d", i);
            item.setImage(new FeedImage(null, url, ""));
            items.add(item);
        }
        Feed feed = new Feed();
        feed.setItems(items);

        DownloadService.removeDuplicateImages(feed);

        assertEquals(50, items.size());
        for (int i = 0; i < items.size(); i++) {
            FeedItem item = items.get(i);
            String want = (i == 0) ? "dupe_url" : (i % 5 == 0) ? null : String.format("url_%d", i);
            assertEquals(want, item.getImageLocation());
        }
    }
}
