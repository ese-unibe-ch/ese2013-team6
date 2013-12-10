package com.ese2013.mub.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * Receives a BroadCast and starts the Notification Service.
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, NotificationService.class));
	}
}