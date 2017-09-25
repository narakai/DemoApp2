package com.clem.ipocadonation1.core.util.playback;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;

import com.clem.ipocadonation1.core.preferences.UserPreferences;

import org.antennapod.audio.MediaPlayer;

public class AudioPlayer extends MediaPlayer implements IPlayer {
	private static final String TAG = "AudioPlayer";

	public AudioPlayer(Context context) {
		super(context);
		PreferenceManager.getDefaultSharedPreferences(context)
				.registerOnSharedPreferenceChangeListener(sonicListener);
	}

	private final SharedPreferences.OnSharedPreferenceChangeListener sonicListener =
			(sharedPreferences, key) -> {
				if (key.equals(UserPreferences.PREF_SONIC)) {
					checkMpi();
				}
			};

	@Override
	public void setScreenOnWhilePlaying(boolean screenOn) {
		Log.e(TAG, "Setting screen on while playing not supported in Audio Player");
		throw new UnsupportedOperationException("Setting screen on while playing not supported in Audio Player");
	}

	@Override
	public void setDisplay(SurfaceHolder sh) {
		if (sh != null) {
			Log.e(TAG, "Setting display not supported in Audio Player");
			throw new UnsupportedOperationException("Setting display not supported in Audio Player");
		}
	}

    @Override
    public void setVideoScalingMode(int mode) {
        throw new UnsupportedOperationException("Setting scaling mode is not supported in Audio Player");
    }

	@Override
	protected boolean useSonic() {
		return UserPreferences.useSonic();
	}

	@Override
	protected boolean downmix() {
		return UserPreferences.stereoToMono();
	}
}
