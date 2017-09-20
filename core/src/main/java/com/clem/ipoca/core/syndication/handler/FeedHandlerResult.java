package com.clem.ipoca.core.syndication.handler;

import com.clem.ipoca.core.feed.Feed;

import java.util.Map;

/**
 * Container for results returned by the Feed parser
 */
public class FeedHandlerResult {

    public Feed feed;
    public Map<String, String> alternateFeedUrls;

    public FeedHandlerResult(Feed feed, Map<String, String> alternateFeedUrls) {
        this.feed = feed;
        this.alternateFeedUrls = alternateFeedUrls;
    }
}
