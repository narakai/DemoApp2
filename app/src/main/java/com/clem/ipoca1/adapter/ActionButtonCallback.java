package com.clem.ipoca1.adapter;

import com.clem.ipoca1.core.feed.FeedItem;
import com.clem.ipoca1.core.util.LongList;

public interface ActionButtonCallback {
	/** Is called when the action button of a list item has been pressed. */
	void onActionButtonPressed(FeedItem item, LongList queueIds);
}
