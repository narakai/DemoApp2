package com.clem.ipoca.adapter;

import com.clem.ipoca.core.feed.FeedItem;
import com.clem.ipoca.core.util.LongList;

public interface ActionButtonCallback {
	/** Is called when the action button of a list item has been pressed. */
	void onActionButtonPressed(FeedItem item, LongList queueIds);
}
