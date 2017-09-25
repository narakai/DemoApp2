package com.clem.ipocadonation1.fragment.gpodnet;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.clem.ipocadonation1.activity.MainActivity;
import com.clem.ipocadonation1.core.gpoddernet.GpodnetService;
import com.clem.ipocadonation1.core.gpoddernet.GpodnetServiceException;
import com.clem.ipocadonation1.core.gpoddernet.model.GpodnetPodcast;
import com.clem.ipocadonation1.core.gpoddernet.model.GpodnetTag;

import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Shows all podcasts from gpodder.net that belong to a specific tag.
 * Use the newInstance method of this class to create a new TagFragment.
 */
public class TagFragment extends PodcastListFragment {

    private static final String TAG = "TagFragment";
    private static final int PODCAST_COUNT = 50;

    private GpodnetTag tag;

    public static TagFragment newInstance(GpodnetTag tag) {
        Validate.notNull(tag);
        TagFragment fragment = new TagFragment();
        Bundle args = new Bundle();
        args.putParcelable("tag", tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        Validate.isTrue(args != null && args.getParcelable("tag") != null, "args invalid");
        tag = args.getParcelable("tag");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(tag.getTitle());
    }

    @Override
    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        return service.getPodcastsForTag(tag, PODCAST_COUNT);
    }
}