package com.clem.ipoca1.event;

import com.clem.ipoca1.adapter.itunes.ItunesAdapter;

/**
 * Created by laileon on 2017/9/20.
 */

public class AddEvent {
    public ItunesAdapter.Podcast mPodcast;

    public AddEvent(ItunesAdapter.Podcast podcast) {
        mPodcast = podcast;
    }
}
