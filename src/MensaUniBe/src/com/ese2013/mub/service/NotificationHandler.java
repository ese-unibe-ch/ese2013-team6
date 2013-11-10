package com.ese2013.mub.service;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * NotificationHandler is Responsible for storing the selected criteria and set up the AlarmManger for the NotificationService
 * It is needed to call the setUp() method to make the Handler work.
 *
 */
public class NotificationHandler{ // maybe make this an async task or an actycity!
	private boolean allMensas; //if false only favorites are shown
	private List<String> criteria;
	private Context context;
	private PendingIntent operation; // null at the moment
	
	public NotificationHandler(Context context, boolean allMensas, List<String> criterias){
		this.allMensas = allMensas;
		this.criteria = criterias;
		this.context = context;
		this.operation = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class),  PendingIntent.FLAG_CANCEL_CURRENT);
	}
	public void setUp(){
		this.setAlarmManager();
		
	}
	private void setAlarmManager(){
		Calendar tenOClock = Calendar.getInstance(Locale.getDefault());
		tenOClock.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 10, 0, 0);
		
		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		alarm.setInexactRepeating(AlarmManager.RTC, tenOClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY, operation);
	}
}
