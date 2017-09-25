package com.clem.ipoca1;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.clem.ipoca1.core.ClientConfig;
import com.clem.ipoca1.core.feed.EventDistributor;
import com.clem.ipoca1.spa.SPAUtil;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;

/** Main application class. */
public class PodcastApp extends Application {

    // make sure that ClientConfigurator executes its static code
    static {
        try {
            Class.forName("com.clem.ipoca1.config.ClientConfigurator");
        } catch (Exception e) {
            throw new RuntimeException("ClientConfigurator not found");
        }
    }

	private static PodcastApp singleton;

	public static PodcastApp getInstance() {
		return singleton;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Thread.setDefaultUncaughtExceptionHandler(new CrashReportWriter());

		if(BuildConfig.DEBUG) {
			StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.penaltyLog()
				.penaltyDropBox();
			if (Build.VERSION.SDK_INT >= 11) {
				builder.detectActivityLeaks();
				builder.detectLeakedClosableObjects();
			}
			if(Build.VERSION.SDK_INT >= 16) {
				builder.detectLeakedRegistrationObjects();
			}
			StrictMode.setVmPolicy(builder.build());
		}

		singleton = this;

		ClientConfig.initialize(this);

		EventDistributor.getInstance();
		Iconify.with(new FontAwesomeModule());
		Iconify.with(new MaterialModule());

        SPAUtil.sendSPAppsQueryFeedsIntent(this);
    }

	private static long mLastRequestTime = 0;

	public static boolean isDoubleRequest() {
		long gapTime = 1000;
		long currentTimeTime = System.currentTimeMillis();

		if (mLastRequestTime == 0) {
			mLastRequestTime = System.currentTimeMillis();
			return false;
		} else if (currentTimeTime - mLastRequestTime < gapTime) {
			mLastRequestTime = currentTimeTime;
			return true;
		} else {
			mLastRequestTime = currentTimeTime;
			return false;
		}
	}

}
