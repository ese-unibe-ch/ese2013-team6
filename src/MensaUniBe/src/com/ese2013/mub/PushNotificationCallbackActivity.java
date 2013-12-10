package com.ese2013.mub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * This activity is just used as a callback for the push notifications. It
 * starts the normal app and opens it at the invited tab.
 */
public class PushNotificationCallbackActivity extends Activity {
	public static final String SHOW_INVITES = "showInvites";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, DrawerMenuActivity.class);
		intent.putExtra(SHOW_INVITES, true);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}