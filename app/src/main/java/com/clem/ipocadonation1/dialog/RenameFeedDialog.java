package com.clem.ipocadonation1.dialog;

import android.app.Activity;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;
import com.clem.ipocadonation1.R;
import com.clem.ipocadonation1.core.feed.Feed;
import com.clem.ipocadonation1.core.storage.DBWriter;

import java.lang.ref.WeakReference;

public class RenameFeedDialog {

    private final WeakReference<Activity> activityRef;
    private final Feed feed;

    public RenameFeedDialog(Activity activity, Feed feed) {
        this.activityRef = new WeakReference<>(activity);
        this.feed = feed;
    }

    public void show() {
        Activity activity = activityRef.get();
        if(activity == null) {
            return;
        }
        new MaterialDialog.Builder(activity)
                .title(R.string.rename_feed_label)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(feed.getTitle(), feed.getTitle(), true, (dialog, input) -> {
                    feed.setCustomTitle(input.toString());
                    DBWriter.setFeedCustomTitle(feed);
                    dialog.dismiss();
                })
                .neutralText(R.string.reset)
                .onNeutral((dialog, which) -> dialog.getInputEditText().setText(feed.getFeedTitle()))
                .negativeText(R.string.cancel_label)
                .onNegative((dialog, which) -> dialog.dismiss())
                .autoDismiss(false)
                .show();
    }

}
