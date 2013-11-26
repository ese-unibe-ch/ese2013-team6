package com.ese2013.mub.service;

import java.util.List;
import java.util.Set;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.ese2013.mub.DrawerMenuActivity;
import com.ese2013.mub.NotificationFragment;
import com.ese2013.mub.Preferences;
import com.ese2013.mub.R;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.Criteria;

public class NotificationService extends Service{

	public static final String START_FROM_N = "com.ese2013.mub.service.startFromN";
	private Set<String> criteria;
	private boolean allMensas;
	private List<Mensa> mensas;
	private final IBinder nBinder = new NBinder();
	private List<Criteria> criteriaList;
	private NotificationFragment observer;
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		createCriteriaList();

		if(!criteriaList.isEmpty())
			push();
		this.stopSelf();
		return START_STICKY;
	}
	private void push() {
		// TODO Add Notification. iterate trough menu set and set notification,
		//add logo!!
		StringBuilder sb = new StringBuilder();
		for(Criteria crit : criteriaList){
		  sb.append(crit.getName() + ", ");
		}
		sb.deleteCharAt(sb.length());
		String criteriaString = sb.toString();
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
				.setContentTitle(criteriaList.size() + "Criteria " + ((criteriaList.size() == 1)? "is" :" are") + " matching!")
		        .setContentText(criteriaString);
		
		Intent notificationIntent = new Intent(this, DrawerMenuActivity.class);
		notificationIntent.putExtra(START_FROM_N, true);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		
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
	private void notifyObserver(){
		observer.onNotifyChanges();
	}
	public void createCriteriaList() {
				new Model(this.getApplicationContext());
				
				Preferences pref= new Preferences();
				
				//TODO needs to be changed in setting fragment, more than one criteria possible!
				String criteria = pref.getNotificationFood(this);
				this.criteria.add(criteria);
				
				//TODO all mensas or just favorites, add to settings
				//allMensas = pref.getBoolean(NotificationHandler.MENSAS_ALL, true);
				
				CriteriaMatcher criteriaMatcher = new CriteriaMatcher();
				mensas = allMensas ? Model.getInstance().getMensas() : Model.getInstance().getFavoriteMensas();
				
				criteriaList = criteriaMatcher.match(this.criteria, mensas);
				notifyObserver();
	}
}
