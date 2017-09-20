package com.clem.ipoca.core.feed;

import com.clem.ipoca.core.cast.RemoteMedia;

/**
 * Implements methods for FeedMedia that are flavor dependent.
 */
public class FeedMediaFlavorHelper {
    private FeedMediaFlavorHelper(){}
    static boolean instanceOfRemoteMedia(Object o) {
        return o instanceof RemoteMedia;
    }
}
