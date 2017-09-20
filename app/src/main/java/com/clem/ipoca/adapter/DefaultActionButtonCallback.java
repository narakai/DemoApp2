package com.clem.ipoca.adapter;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.clem.ipoca.R;
import com.clem.ipoca.core.dialog.DownloadRequestErrorDialogCreator;
import com.clem.ipoca.core.feed.FeedItem;
import com.clem.ipoca.core.feed.FeedMedia;
import com.clem.ipoca.core.preferences.UserPreferences;
import com.clem.ipoca.core.service.playback.PlaybackService;
import com.clem.ipoca.core.storage.DBReader;
import com.clem.ipoca.core.storage.DBTasks;
import com.clem.ipoca.core.storage.DBWriter;
import com.clem.ipoca.core.storage.DownloadRequestException;
import com.clem.ipoca.core.storage.DownloadRequester;
import com.clem.ipoca.core.util.LongList;
import com.clem.ipoca.core.util.NetworkUtils;

import org.apache.commons.lang3.Validate;

/**
 * Default implementation of an ActionButtonCallback
 */
public class DefaultActionButtonCallback implements ActionButtonCallback {

    private static final String TAG = "DefaultActionButtonCallback";

    private final Context context;

    private static final int TEN_MINUTES_IN_MILLIS = 60 * 1000 * 10;

    // remember timestamp when user allowed downloading via mobile connection
    private static long allowMobileDownloadsTimestamp;
    private static long onlyAddToQueueTimeStamp;

    public DefaultActionButtonCallback(Context context) {
        Validate.notNull(context);
        this.context = context;
    }

    public static boolean userAllowedMobileDownloads() {
        return System.currentTimeMillis() - allowMobileDownloadsTimestamp < TEN_MINUTES_IN_MILLIS;
    }

    public static boolean userChoseAddToQueue() {
        return System.currentTimeMillis() - onlyAddToQueueTimeStamp < TEN_MINUTES_IN_MILLIS;
    }

    @Override
    public void onActionButtonPressed(final FeedItem item, final LongList queueIds) {

        if (item.hasMedia()) {
            final FeedMedia media = item.getMedia();
            boolean isDownloading = DownloadRequester.getInstance().isDownloadingFile(media);
            if (!isDownloading && !media.isDownloaded()) {
                if (NetworkUtils.isDownloadAllowed() || userAllowedMobileDownloads()) {
                    try {
                        DBTasks.downloadFeedItems(context, item);
                        Toast.makeText(context, R.string.status_downloading_label, Toast.LENGTH_SHORT).show();
                    } catch (DownloadRequestException e) {
                        e.printStackTrace();
                        DownloadRequestErrorDialogCreator.newRequestErrorDialog(context, e.getMessage());
                    }
                } else if(userChoseAddToQueue() && !queueIds.contains(item.getId())) {
                    DBWriter.addQueueItem(context, item);
                    Toast.makeText(context, R.string.added_to_queue_label, Toast.LENGTH_SHORT).show();
                } else {
                    confirmMobileDownload(context, item);
                }
            } else if (isDownloading) {
                DownloadRequester.getInstance().cancelDownload(context, media);
                if(UserPreferences.isEnableAutodownload()) {
                    DBWriter.setFeedItemAutoDownload(media.getItem(), false);
                    Toast.makeText(context, R.string.download_canceled_autodownload_enabled_msg, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, R.string.download_canceled_msg, Toast.LENGTH_LONG).show();
                }
            } else { // media is downloaded
                if (item.hasMedia() && item.getMedia().isCurrentlyPlaying()) {
                    context.sendBroadcast(new Intent(PlaybackService.ACTION_PAUSE_PLAY_CURRENT_EPISODE));
                }
                else if (item.hasMedia() && item.getMedia().isCurrentlyPaused()) {
                    context.sendBroadcast(new Intent(PlaybackService.ACTION_RESUME_PLAY_CURRENT_EPISODE));
                }
                else {
                    DBTasks.playMedia(context, media, false, true, false);
                }
            }
        } else {
            if (!item.isPlayed()) {
                DBWriter.markItemPlayed(item, FeedItem.PLAYED, true);
            }
        }
    }

    private void confirmMobileDownload(final Context context, final FeedItem item) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder
                .title(R.string.confirm_mobile_download_dialog_title)
                .content(R.string.confirm_mobile_download_dialog_message)
                .positiveText(context.getText(R.string.confirm_mobile_download_dialog_enable_temporarily))
                .onPositive((dialog, which) -> {
                    allowMobileDownloadsTimestamp = System.currentTimeMillis();
                    try {
                        DBTasks.downloadFeedItems(context, item);
                        Toast.makeText(context, R.string.status_downloading_label, Toast.LENGTH_SHORT).show();
                    } catch (DownloadRequestException e) {
                        e.printStackTrace();
                        DownloadRequestErrorDialogCreator.newRequestErrorDialog(context, e.getMessage());
                    }
                });
        LongList queueIds = DBReader.getQueueIDList();
        if(!queueIds.contains(item.getId())) {
            builder
                    .content(R.string.confirm_mobile_download_dialog_message_not_in_queue)
                    .neutralText(R.string.confirm_mobile_download_dialog_only_add_to_queue)
                    .onNeutral((dialog, which) -> {
                        onlyAddToQueueTimeStamp = System.currentTimeMillis();
                        DBWriter.addQueueItem(context, item);
                        Toast.makeText(context, R.string.added_to_queue_label, Toast.LENGTH_SHORT).show();
                    });
        }
        builder.show();
    }

}
