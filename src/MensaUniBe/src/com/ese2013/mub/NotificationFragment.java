package com.ese2013.mub;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.service.NotificationService;
import com.ese2013.mub.util.Criteria;

public class NotificationFragment extends Fragment {
	private NotificationService service;
	private List<Criteria> criteriaList;
	private NotificationAdapter notificationAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		criteriaList = new ArrayList<Criteria>();
		getActivity().bindService(
				new Intent(getActivity(), NotificationService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		
		//service im moment null, wiso???
	
		

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_notification, container);
		notificationAdapter = new NotificationAdapter();
		// layoutStuff, analog mensaList..
		if(criteriaList.isEmpty()){
			//TODO make textList
		}
		ListView list = (ListView) view
				.findViewById(R.id.notification_view_layout);
		list.setAdapter(notificationAdapter);
		return view;
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((NotificationService.NBinder) binder).getService();
			if(service != null){
				criteriaList = service.getCriteraData();
				service.addObserver(NotificationFragment.this);
				service.createCriteriaList();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			service = null;
		}
	};

	public void onPause() {
		super.onPause();
		getActivity().unbindService(mConnection);
	};

	@Override
	public void onResume() {
		super.onResume();
		getActivity().bindService(
				new Intent(getActivity(), NotificationService.class),
				mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		service.stopSelf();
	}

	public void onNotifyChanges() {
		notificationAdapter.notifyDataSetChanged();

	}

	public void sendListToMenusIntent(Mensa mensa){
		((DrawerMenuActivity) getActivity()).launchByMensaAtGivenPage(mensa.getId());
	}

	class NotificationAdapter extends BaseAdapter implements IAdapter{
		private LayoutInflater inflater;

		public NotificationAdapter() {
			super();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	
			View view = convertView;
			if (view == null)
				inflater = (LayoutInflater) getActivity().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
			
			view = inflater.inflate(R.layout.notification_list_element, null);
			
			//TODO problem, cannot add to layout which is parsed from xml.
			Criteria criteria = criteriaList.get(position);
			TextView textView = (TextView) view.findViewById(R.id.criteria_title);
			textView.setText(criteria.getName());
			LinearLayout layout = (LinearLayout)view.findViewById(R.id.notification_list_layout);
		
			for(Menu menu : criteria.getMap().keySet()){
				layout.addView(new MenuView(getActivity(), menu.getTitle(), menu.getDescription()));
				for(Mensa mensa : criteria.getMap().get(menu)){
					RelativeLayout rel = (RelativeLayout)inflater.inflate(R.layout.daily_section_title_bar, null);
					TextView text = (TextView) rel.getChildAt(0);
					text.setOnClickListener(new AddressTextListener(mensa, this));
					text.setText(mensa.getName());
					ImageButton favoriteButton = (ImageButton)rel.getChildAt(1);
					favoriteButton.setOnClickListener(new FavoriteButtonListener(mensa, favoriteButton));
					ImageButton mapButton =(ImageButton)rel.getChildAt(2);
					mapButton.setOnClickListener(new MapButtonListener(mensa, NotificationFragment.this));
					layout.addView(rel);
				}
			}
		
			return view;
		}

		@Override
		public int getCount() {
			return criteriaList.size();
		}

		@Override
		public Object getItem(int position) {
			return criteriaList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public void notifyDataSetChanged() {
			criteriaList = service.getCriteraData();
			super.notifyDataSetChanged();
		}

		@Override
		public void sendListToMenusIntent(Mensa mensa) {
			((DrawerMenuActivity) getActivity()).launchByMensaAtGivenPage(mensa.getId());
		}

	}
}
