package com.ese2013.mub.service;

import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * NotificationHandler is Responsible for storing the selected criteria and set up the AlarmManger for the NotificationService
 * It is needed to call the setUp() method to make the Handler work.
 * NotificationHandler is only invoked when the notifications are being enabled in the settings.
 * It also defines the keys used for saving the used data.
 */
public class NotificationHandler{ // maybe make this an async task or an actycity!
	
	public static final String NOTIFICATION_ENABLED = "com.ese2013.mub.service.notificationEnabled";// key for notificationEnabled in sharedPreferences
	public static final String CRITERIA_LIST = "com.ese2013.mub.service.criteriaList";// key for criteriaList in sharedPreferences
	public static final String MENSAS_ALL = "com.ese2013.mub.service.allMensas";
	
	private boolean allMensas; //if false only favorites are shown
	private boolean notificationEnabled; // flag that notifications are enabled, used for BootReceiver
	private Set<String> criteria;
	private Context context;
	
	public NotificationHandler(Context context, boolean allMensas, Set<String> criterias){
		this.allMensas = allMensas;
		this.criteria = criterias;
		this.context = context;
		this.notificationEnabled = true;
	}
	public void setUp(){
		//TODO if not saved by preferences fragment, save here.
		this.setAlarmManager();
		
	}
	private void setAlarmManager(){
		Calendar tenOClock = Calendar.getInstance(Locale.getDefault());
		tenOClock.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 10, 0, 0);
		
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class),  PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		alarm.setInexactRepeating(AlarmManager.RTC, tenOClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY, operation);
	}
}
