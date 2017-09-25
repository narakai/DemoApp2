package com.clem.ipoca1.core.util.comparator;

import com.clem.ipoca1.core.feed.Chapter;

import java.util.Comparator;

public class ChapterStartTimeComparator implements Comparator<Chapter> {

	@Override
	public int compare(Chapter lhs, Chapter rhs) {
		if (lhs.getStart() == rhs.getStart()) {
			return 0;
		} else if (lhs.getStart() < rhs.getStart()) {
			return -1;
		} else {
			return 1;
		}
	}

}
