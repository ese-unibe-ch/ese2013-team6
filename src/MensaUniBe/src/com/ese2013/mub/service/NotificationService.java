package com.ese2013.mub.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends IntentService{

	public NotificationService(String name) {
		super("MUB Notification Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO create Model, match criteria and push a notification in case theres a match!
		
		
		this.stopSelf();
	}
	@Override
	   public void onStart(Intent intent, int startId) {
	      super.onStart(intent, startId);
	   }
	

}
