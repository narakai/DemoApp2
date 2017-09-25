package com.clem.ipocadonation1.core.service.download;

/**
 * Callback used by the Downloader-classes to notify the requester that the
 * download has completed.
 */
public interface DownloaderCallback {

	void onDownloadCompleted(Downloader downloader);
}
