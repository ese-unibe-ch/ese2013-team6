package com.ese2013.mub.service;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHandler{
	
	private Context context;
	private PendingIntent operation;
	private AlarmManager alarm;
	
	public NotificationHandler(Context context){
		this.context = context;
		operation = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class),  PendingIntent.FLAG_CANCEL_CURRENT);
	}
	public void setAlarmManager(){
		Calendar tenOClock = Calendar.getInstance(Locale.getDefault());
		tenOClock.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 10, 0, 0);
		
		alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		alarm.setInexactRepeating(AlarmManager.RTC, tenOClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY, operation);
	}
	public void unSetAlarm(){
		alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(operation);
	}
}
