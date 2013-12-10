package com.ese2013.mub.service;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ese2013.mub.util.SharedPrefsHandler;

public class BootReceiver extends BroadcastReceiver {
	/**
	 * Receives BootUpComplete Broadcast and sets an {@link AlarmManager} with
	 * an Intent for the {@link AlarmReceiver}. Its actions depend on the
	 * predefined values made in the Settings, if there are now settings made,
	 * it does nothing else it sets an daily repeating AlarmManager, starting
	 * the AlarmReceiver.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPrefsHandler pref = new SharedPrefsHandler(context);
		if (pref.getDoNotification()) {
			Calendar tenOClock = Calendar.getInstance(Locale.getDefault());
			tenOClock.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 10, 0, 0);
			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class),
					PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.setInexactRepeating(AlarmManager.RTC, tenOClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					operation);
		}
	}
}