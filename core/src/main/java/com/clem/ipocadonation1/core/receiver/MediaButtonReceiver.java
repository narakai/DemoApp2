package com.clem.ipocadonation1.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.clem.ipocadonation1.core.ClientConfig;
import com.clem.ipocadonation1.core.service.playback.PlaybackService;

/** Receives media button events. */
public class MediaButtonReceiver extends BroadcastReceiver {
	private static final String TAG = "MediaButtonReceiver";
	public static final String EXTRA_KEYCODE = "com.clem.ipocadonation1.core.service.extra.MediaButtonReceiver.KEYCODE";
	public static final String EXTRA_SOURCE = "com.clem.ipocadonation1.core.service.extra.MediaButtonReceiver.SOURCE";

	public static final String NOTIFY_BUTTON_RECEIVER = "com.clem.ipocadonation1.NOTIFY_BUTTON_RECEIVER";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received intent");
		KeyEvent event = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
		if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount()==0) {
			ClientConfig.initialize(context);
			Intent serviceIntent = new Intent(context, PlaybackService.class);
			serviceIntent.putExtra(EXTRA_KEYCODE, event.getKeyCode());
			serviceIntent.putExtra(EXTRA_SOURCE, event.getSource());
			context.startService(serviceIntent);
		}

	}

}
