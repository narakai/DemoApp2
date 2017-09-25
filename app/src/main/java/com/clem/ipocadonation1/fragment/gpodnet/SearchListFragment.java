package com.clem.ipocadonation1.fragment.gpodnet;

import android.os.Bundle;

import com.clem.ipocadonation1.core.gpoddernet.GpodnetService;
import com.clem.ipocadonation1.core.gpoddernet.GpodnetServiceException;
import com.clem.ipocadonation1.core.gpoddernet.model.GpodnetPodcast;

import java.util.List;

/**
 * Performs a search on the gpodder.net directory and displays the results.
 */
public class SearchListFragment extends PodcastListFragment {
    private static final String ARG_QUERY = "query";

    private String query;

    public static SearchListFragment newInstance(String query) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_QUERY)) {
            this.query = getArguments().getString(ARG_QUERY);
        } else {
            this.query = "";
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        // parent already inflated menu
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);
//        MenuItemUtils.adjustTextColor(getActivity(), sv);
//        sv.setQueryHint(getString(R.string.gpodnet_search_hint));
//        sv.setQuery(query, false);
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                sv.clearFocus();
//                changeQuery(s);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
//    }

    @Override
    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        return service.searchPodcasts(query, 0);
    }

//    public void changeQuery(String query) {
//        Validate.notNull(query);
//
//        this.query = query;
//        loadData();
//    }
}
