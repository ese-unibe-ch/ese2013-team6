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
import com.ese2013.mub.R;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.Observer;
import com.ese2013.mub.util.SharedPrefsHandler;
import com.ese2013.mub.util.database.MensaDataSource;

public class NotificationService extends Service implements Observer {

	public static final String START_FROM_N = "com.ese2013.mub.service.startFromN";
	private final IBinder nBinder = new NBinder();
	private List<Criteria> criteriaList;
	private boolean hasPushed;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MensaDataSource dataSource = MensaDataSource.getInstance();
		dataSource.init(getApplicationContext());

		Model model = Model.getInstance();
		model.init(dataSource, new SharedPrefsHandler(getApplicationContext()));
		model.addObserver(this);
		return START_STICKY;
	}

	private void push() {
		if (!criteriaList.isEmpty()) {

			StringBuilder sb = new StringBuilder();
			String prefix = "";
			for (Criteria crit : criteriaList) {
				sb.append(prefix);
				prefix = ", ";
				sb.append(crit.getName());
			}
			String criteriaString = sb.toString();
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle(
					criteriaList.size() + " Criteria " + ((criteriaList.size() == 1) ? "is" : " are") + " matching!")
					.setContentText(criteriaString);

			Intent notificationIntent = new Intent(this, DrawerMenuActivity.class);
			notificationIntent.putExtra(START_FROM_N, true);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
			mBuilder.setSmallIcon(R.drawable.ic_launcher);
			mBuilder.setAutoCancel(true);

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(0, mBuilder.build());
			hasPushed = true;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return nBinder;
	}

	/**
	 * Only Used for binding purposes
	 * 
	 */
	public class NBinder extends Binder {

		public NotificationService getService() {
			return NotificationService.this;
		}
	}

	public List<Criteria> getCriteraData() {
		return criteriaList;
	}

	private List<Criteria> createCriteriaList() {
		SharedPrefsHandler pref = new SharedPrefsHandler(this);

		Set<String> criteria = pref.getNotificationListItems();
		boolean allMensas = pref.getNotificationMensas() == 0 ? true : false;
		CriteriaMatcher criteriaMatcher = new CriteriaMatcher();
		List<Mensa> mensas = allMensas ? Model.getInstance().getMensas() : Model.getInstance().getFavoriteMensas();

		return criteriaMatcher.match(criteria, mensas);
	}

	@Override
	public void onNotifyChanges(Object... message) {
		criteriaList = createCriteriaList();
		push();
		if (hasPushed)
			stopSelf();
	}
}
