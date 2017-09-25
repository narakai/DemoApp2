package com.clem.ipocadonation1.core.util.comparator;

import com.clem.ipocadonation1.core.feed.FeedItem;

import java.util.Comparator;

/** Compares the pubDate of two FeedItems for sorting*/
public class FeedItemPubdateComparator implements Comparator<FeedItem> {

	/** Returns a new instance of this comparator in reverse order. 
	public static FeedItemPubdateComparator newInstance() {
		FeedItemPubdateComparator
	}*/
	@Override
	public int compare(FeedItem lhs, FeedItem rhs) {
		return rhs.getPubDate().compareTo(lhs.getPubDate());
	}

}
