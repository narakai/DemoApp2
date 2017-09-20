package com.clem.ipoca;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.clem.ipoca.core.ClientConfig;
import com.clem.ipoca.core.feed.EventDistributor;
import com.clem.ipoca.spa.SPAUtil;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;

/** Main application class. */
public class PodcastApp extends Application {

    // make sure that ClientConfigurator executes its static code
    static {
        try {
            Class.forName("com.clem.ipoca.config.ClientConfigurator");
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

}
