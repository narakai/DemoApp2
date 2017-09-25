package com.clem.ipocadonation1.adapter;

import com.clem.ipocadonation1.core.feed.FeedItem;
import com.clem.ipocadonation1.core.util.LongList;

public interface ActionButtonCallback {
	/** Is called when the action button of a list item has been pressed. */
	void onActionButtonPressed(FeedItem item, LongList queueIds);
}
