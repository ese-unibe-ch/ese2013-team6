package com.ese2013.mub.service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.Model;

public class NotificationService extends IntentService{

	private Set<String> criteria;
	private boolean allMensas;
	private List<Mensa> mensas;
	private HashMap<Menu, List<Mensa>> matchedMenus;
	
	public NotificationService(String name) {
		super("MUB Notification Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO match criteria and push a notification in case there's a match!
		new Model(this.getApplicationContext());
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		criteria = pref.getStringSet(NotificationHandler.CRITERIA_LIST, new TreeSet<String>());
		allMensas = pref.getBoolean(NotificationHandler.MENSAS_ALL, true);
		
		CriteriaMatcher criteriaMatcher = new CriteriaMatcher();
		matchedMenus = criteriaMatcher.match(criteria, mensas);
		
		push();
		this.stopSelf();
	}
	private void push() {
		// TODO Add Notification. iterate trough menu set and set notification,
		
	}

	@Override
	   public void onStart(Intent intent, int startId) {
	      super.onStart(intent, startId);
	   }
	

}
