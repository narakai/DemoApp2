package com.clem.ipoca1.core.util;

import com.clem.ipoca1.core.feed.Feed;

import java.util.Comparator;

/** Compares the title of two feeds for sorting. */
public class FeedtitleComparator implements Comparator<Feed> {

	@Override
	public int compare(Feed lhs, Feed rhs) {
		return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
	}

}
