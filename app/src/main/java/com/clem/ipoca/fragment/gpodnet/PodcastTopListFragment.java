package com.clem.ipoca.fragment.gpodnet;

import com.clem.ipoca.core.gpoddernet.GpodnetService;
import com.clem.ipoca.core.gpoddernet.GpodnetServiceException;
import com.clem.ipoca.core.gpoddernet.model.GpodnetPodcast;

import java.util.List;

/**
 *
 */
public class PodcastTopListFragment extends PodcastListFragment {
    private static final String TAG = "PodcastTopListFragment";
    private static final int PODCAST_COUNT = 50;

    @Override
    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        return service.getPodcastToplist(PODCAST_COUNT);
    }
}
