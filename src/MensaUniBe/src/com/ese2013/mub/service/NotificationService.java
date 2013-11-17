package com.ese2013.mub.service;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.ese2013.mub.DrawerMenuActivity;
import com.ese2013.mub.NotificationFragment;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.Criteria;

public class NotificationService extends Service{

	private Set<String> criteria;
	private boolean allMensas;
	private List<Mensa> mensas;
	private final IBinder nBinder = new NBinder();
	private List<Criteria> criteriaList;
	private NotificationFragment observer;
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		createCriteriaList();
		push();
		return START_STICKY;
	}
	private void push() {
		// TODO Add Notification. iterate trough menu set and set notification,
		StringBuilder sb = new StringBuilder();
		for(Criteria crit : criteriaList){
		  sb.append(crit.getName() + ", ");
		}
		sb.deleteCharAt(sb.length());
		String criteriaString = sb.toString();
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
				.setContentTitle(criteriaList.size() + "Criteria " + ((criteriaList.size() == 1)? "is" :" are") + " a match!")
		        .setContentText(criteriaString);
		Intent notificationIntent = new Intent(this, DrawerMenuActivity.class);
		//TODO put extra in Intent which opens NotificationFragment
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());

	}
	public void addObserver(NotificationFragment observer){
		this.observer = observer;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return nBinder;
	}
	public class NBinder extends Binder{
		
		public NotificationService getService(){
			return NotificationService.this;
		}
	}
	public List<Criteria> getCriteraData(){
		return criteriaList;
	}
	public void notifyObserver(){
		observer.onNotifyChanges();
	}
	public void createCriteriaList() {
				new Model(this.getApplicationContext());
				
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
				criteria = pref.getStringSet(NotificationHandler.CRITERIA_LIST, new TreeSet<String>());
				allMensas = pref.getBoolean(NotificationHandler.MENSAS_ALL, true);
				
				CriteriaMatcher criteriaMatcher = new CriteriaMatcher();
				mensas = allMensas ? Model.getInstance().getMensas() : Model.getInstance().getFavoriteMensas();
				criteriaList = criteriaMatcher.match(criteria, mensas);
	}
}
