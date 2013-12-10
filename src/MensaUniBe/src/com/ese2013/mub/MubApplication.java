package com.ese2013.mub;

import com.ese2013.mub.model.Model;
import com.ese2013.mub.social.SocialManager;
import com.ese2013.mub.util.database.MensaDataSource;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

/**
 * This class extends the default android application class. It's onCreate
 * method is called every time a part of the app is started (e.g. an activity or
 * service). This class should mainly be used to initialize any global state
 * like singletons.
 */
public class MubApplication extends android.app.Application {
	@Override
	public void onCreate() {
		super.onCreate();
		initParseService();
		initSingletons();
	}

	/**
	 * Initializes the static variables in the singleton classes. No loading is
	 * done here, just the instances get created and stored.
	 */
	private void initSingletons() {
		Model.getInstance();
		SocialManager.getInstance();
		MensaDataSource.getInstance();
	}

	/**
	 * Initializes the parse service.
	 */
	private void initParseService() {
		Parse.initialize(this, "ZmdQMR7FctP2XgMJN5lvj98Aj9IA2Bf8mJrny11n", "yVVh3GiearTRsRXZqgm2FG6xfWvcQPjINX6dGJNu");
		PushService.setDefaultPushCallback(this, PushNotificationCallbackActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
}