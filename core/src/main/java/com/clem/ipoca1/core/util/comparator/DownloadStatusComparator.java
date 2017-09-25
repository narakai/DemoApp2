package com.clem.ipoca1.core.util.comparator;

import com.clem.ipoca1.core.service.download.DownloadStatus;

import java.util.Comparator;

/** Compares the completion date of two Downloadstatus objects. */
public class DownloadStatusComparator implements Comparator<DownloadStatus> {

	@Override
	public int compare(DownloadStatus lhs, DownloadStatus rhs) {
		return rhs.getCompletionDate().compareTo(lhs.getCompletionDate());
	}

}
