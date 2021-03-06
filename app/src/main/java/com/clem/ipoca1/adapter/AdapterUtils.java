package com.clem.ipoca1.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clem.ipoca1.core.feed.FeedItem;
import com.clem.ipoca1.core.feed.FeedMedia;
import com.clem.ipoca1.core.util.Converter;
import com.clem.ipoca1.core.util.NetworkUtils;
import com.joanzapata.iconify.Iconify;

/**
 * Utility methods for adapters
 */
class AdapterUtils {

    private static final String TAG = AdapterUtils.class.getSimpleName();

    private AdapterUtils() {

    }

    /**
     * Updates the contents of the TextView that shows the current playback position and the ProgressBar.
     */
    static void updateEpisodePlaybackProgress(FeedItem item, TextView txtvPos, ProgressBar episodeProgress) {
        FeedMedia media = item.getMedia();
        episodeProgress.setVisibility(View.GONE);
        if (media == null) {
            txtvPos.setVisibility(View.GONE);
            return;
        } else {
            txtvPos.setVisibility(View.VISIBLE);
        }

        FeedItem.State state = item.getState();
        if (state == FeedItem.State.PLAYING
                || state == FeedItem.State.IN_PROGRESS) {
            if (media.getDuration() > 0) {
                episodeProgress.setVisibility(View.VISIBLE);
                episodeProgress.setProgress((int) (((double) media
                                .getPosition()) / media.getDuration() * 100));
                txtvPos.setText(Converter.getDurationStringLong(media.getDuration()
                                - media.getPosition()));
            }
        } else if (!media.isDownloaded()) {
            if (media.getSize() > 0) {
                txtvPos.setText(Converter.byteToString(media.getSize()));
            } else if(NetworkUtils.isDownloadAllowed() && !media.checkedOnSizeButUnknown()) {
                txtvPos.setText("{fa-spinner}");
                Iconify.addIcons(txtvPos);
                NetworkUtils.getFeedMediaSizeObservable(media)
                        .subscribe(
                                size -> {
                                    if (size > 0) {
                                        txtvPos.setText(Converter.byteToString(size));
                                    } else {
                                        txtvPos.setText("");
                                    }
                                }, error -> {
                                    txtvPos.setText("");
                                    Log.e(TAG, Log.getStackTraceString(error));
                                });
            } else {
                txtvPos.setText("");
            }
        } else {
            txtvPos.setText(Converter.getDurationStringLong(media.getDuration()));
        }
    }
}
