package com.ese2013.mub.service;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ese2013.mub.Preferences;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Preferences pref = new Preferences();	
		if (pref.getDoNotification(context)) {
			
			Calendar tenOClock = Calendar.getInstance(Locale.getDefault());
			
			tenOClock.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH,
					10, 0, 0);
			
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			
			PendingIntent operation = PendingIntent.getBroadcast(context, 0,
					new Intent(context, AlarmReceiver.class),
					PendingIntent.FLAG_CANCEL_CURRENT);

			
			alarm.setInexactRepeating(AlarmManager.RTC,
					tenOClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					operation);
		}
	}
}
