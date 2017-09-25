package com.clem.ipocadonation1.core.util.exception;

import com.clem.ipocadonation1.core.feed.FeedMedia;

public class MediaFileNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	private FeedMedia media;

	public MediaFileNotFoundException(String msg, FeedMedia media) {
		super(msg);
		this.media = media;
	}

	public MediaFileNotFoundException(FeedMedia media) {
		super();
		this.media = media;
	}

	public FeedMedia getMedia() {
		return media;
	}
}
