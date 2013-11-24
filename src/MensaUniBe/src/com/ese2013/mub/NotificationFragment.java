package com.ese2013.mub;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.service.CriteriaMatcher;
import com.ese2013.mub.service.NotificationService;
import com.ese2013.mub.util.Criteria;
import com.google.android.gms.internal.ad;

public class NotificationFragment extends Fragment {
	private NotificationService service;
	private NotificationAdapter notificationAdapter;
	private ListView list;

	
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().getApplicationContext().bindService(new Intent(getActivity().getApplicationContext(), NotificationService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((NotificationService.NBinder) binder).getService();
		
		}

		public void onServiceDisconnected(ComponentName className) {
			service = null;
			
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_notification, container, false);
		notificationAdapter = new NotificationAdapter();
		
		
		list = (ListView) view.findViewById(R.id.notification_list);
		list.setAdapter(notificationAdapter);
		notificationAdapter.fill();
		Log.d("am i here 2","am i here 2");
		return view;
	}
	
	public void onPause() {
		//getActivity().unbindService(mConnection);
		super.onPause();
	};

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getApplicationContext().bindService(new Intent(getActivity(), NotificationService.class), mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onDestroy() {
		//getActivity().unbindService(mConnection);
		super.onDestroy();
	}

	public void onNotifyChanges() {
		notificationAdapter.notifyDataSetChanged();

	}

	public void sendListToMenusIntent(Mensa mensa) {
		((DrawerMenuActivity) getActivity()).launchByMensaAtGivenPage(mensa
				.getId());
	}

	class NotificationAdapter extends BaseAdapter implements IAdapter {
		private LayoutInflater inflater;
		private List<Criteria> adapterList;

		public NotificationAdapter() {
			super();
			adapterList = new ArrayList<Criteria>();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d("am i here 1","am i here 1");
			
//			convertView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					if(service != null){
//					service.addObserver(NotificationFragment.this);
//					service.createCriteriaList();
//					criteriaList = service.getCriteraData();
//					}
//				}
//			});
//			convertView.performClick();
//			
//			
//			
//			if(criteriaList.isEmpty()){
//				TextView text = (TextView) convertView.findViewById(R.id.no_crit_text);
//				text.setText("No matching criteria found!");
//				return convertView;
//			}
			View view = convertView;
			if (inflater == null)
				inflater = (LayoutInflater) getActivity().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);

			view = inflater.inflate(R.layout.notification_list_element, null);
			LinearLayout layout = (LinearLayout) view
					.findViewById(R.id.notification_list_sublayout);
			
			Criteria criteria = adapterList.get(position);
			// needs proper layout..
			TextView criteriaTitle = (TextView) view
					.findViewById(R.id.criteria_title);
			criteriaTitle.setText(criteria.getName());
			

			for (Menu menu : criteria.getMap().keySet()) {
				layout.addView(new MenuView(getActivity(), menu.getTitle(),
						menu.getDescription()));
				for (Mensa mensa : criteria.getMap().get(menu)) {
					RelativeLayout rel = (RelativeLayout) inflater.inflate(
							R.layout.daily_section_title_bar, null);
					TextView text = (TextView) rel.getChildAt(0);
					text.setOnClickListener(new AddressTextListener(mensa, this));
					text.setText(mensa.getName());
					ImageButton favoriteButton = (ImageButton) rel
							.getChildAt(1);
					favoriteButton
							.setOnClickListener(new FavoriteButtonListener(
									mensa, favoriteButton));
					ImageButton mapButton = (ImageButton) rel.getChildAt(2);
					mapButton.setOnClickListener(new MapButtonListener(mensa,
							NotificationFragment.this));
					layout.addView(rel);
				}
			}
			return view;
		}

		@Override
		public int getCount() {
			return adapterList.size();
		}

		@Override
		public Object getItem(int position) {
			return adapterList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public void notifyDataSetChanged() {
			adapterList = service.getCriteraData();
			super.notifyDataSetChanged();
		}

		@Override
		public void sendListToMenusIntent(Mensa mensa) {
			((DrawerMenuActivity) getActivity()).launchByMensaAtGivenPage(mensa
					.getId());
		}
		public void fill(){
			fuckingTest();
		}
		public void fuckingTest(){
			Mensa mensa1 = new Mensa.MensaBuilder().setId(0).setIsFavorite(true).setName("Mensa no. 1").build();
			Mensa mensa2 = new Mensa.MensaBuilder().setId(0).setIsFavorite(true).setName("Mensa no. 2").build();
			Mensa mensa3 = new Mensa.MensaBuilder().setId(0).setIsFavorite(true).setName("Mensa no. 3").build();
		
			WeeklyMenuplan plan1 = new WeeklyMenuplan();
			WeeklyMenuplan plan2 = new WeeklyMenuplan();
			WeeklyMenuplan plan3 = new WeeklyMenuplan();
		
			Menu menu1 = new Menu.MenuBuilder().setDate(Day.today()).setDescription("Pommes und Schnitzel").setTitle("Menu").build();
			Menu menu2 = new Menu.MenuBuilder().setDate(Day.today()).setDescription("Teigwaren und Schnitzel").setTitle("Menu").build();
			Menu menu3 = new Menu.MenuBuilder().setDate(Day.today()).setDescription("Rösti und Geschnetzeltes").setTitle("Menu").build();
			
			Menu menu4 = new Menu.MenuBuilder().setDate(Day.today()).setDescription("Pommes und Schnitzel").setTitle("Menu").build();
			Menu menu5 = new Menu.MenuBuilder().setDate(Day.today()).setDescription("Bratkartoffeln und Geschnetzeltes").setTitle("Menu").build();
			Menu menu6 = new Menu.MenuBuilder().setDate(Day.today()).setDescription("Kartoffeln und Fleisch").setTitle("Menu").build();
			
			Menu menu7 = new Menu.MenuBuilder().setDate(Day.today()).setDescription("Bratkartoffeln und Geschnetzeltes").setTitle("Menu").build();
			Menu menu8 = new Menu.MenuBuilder().setDate(Day.today()).setDescription("Bratkartoffeln und Fleisch").setTitle("Menu").build();
		
			
			plan1.add(menu1);
			plan1.add(menu2);
			plan1.add(menu3);
			plan2.add(menu4);
			plan2.add(menu5);
			plan2.add(menu6);
			plan3.add(menu7);
			plan3.add(menu8);
			
			mensa1.setMenuplan(plan1);
			mensa2.setMenuplan(plan2);
			mensa3.setMenuplan(plan3);
		
			List<Mensa> mensas = new ArrayList<Mensa>();
			mensas.add(mensa1);
			mensas.add(mensa2);
			mensas.add(mensa3);
		
			Set<String> criteria = new LinkedHashSet<String>();
			criteria.add("Schnitzel");
			criteria.add("Fleisch");
			criteria.add("Pommes");
			criteria.add("kommt nicht vor");
			criteria.add("bogus");
		
			CriteriaMatcher matcher = new CriteriaMatcher();
			adapterList = matcher.match(criteria, mensas);
		}
	}
}
