package com.clem.ipoca.core.feed;

import java.util.Date;

class FeedItemMother {

    static FeedItem anyFeedItemWithImage() {
        FeedItem item = new FeedItem(0, "Item", "Item", "url", new Date(), FeedItem.PLAYED, FeedMother.anyFeed());
        item.setImage(FeedImageMother.anyFeedImage());
        return item;
    }

}
