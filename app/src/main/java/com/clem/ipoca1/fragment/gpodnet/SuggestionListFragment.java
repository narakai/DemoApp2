package com.clem.ipoca1.fragment.gpodnet;

import com.clem.ipoca1.core.gpoddernet.GpodnetService;
import com.clem.ipoca1.core.gpoddernet.GpodnetServiceException;
import com.clem.ipoca1.core.gpoddernet.model.GpodnetPodcast;
import com.clem.ipoca1.core.preferences.GpodnetPreferences;

import java.util.Collections;
import java.util.List;

/**
 * Displays suggestions from gpodder.net
 */
public class SuggestionListFragment extends PodcastListFragment {
    private static final int SUGGESTIONS_COUNT = 50;

    @Override
    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        if (GpodnetPreferences.loggedIn()) {
            service.authenticate(GpodnetPreferences.getUsername(), GpodnetPreferences.getPassword());
            return service.getSuggestions(SUGGESTIONS_COUNT);
        } else {
            return Collections.emptyList();
        }
    }
}
