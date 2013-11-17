package com.ese2013.mub;

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

		getActivity().bindService(
				new Intent(getActivity(), NotificationService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		service.addObserver(this);
		service.createCriteriaList();
		
		criteriaList = service.getCriteraData();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_notification, container);
		notificationAdapter = new NotificationAdapter();
		// layoutStuff, analog mensaList..
		ListView list = (ListView) view
				.findViewById(R.id.notification_view_layout);
		list.setAdapter(notificationAdapter);
		return view;
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((NotificationService.NBinder) binder).getService();
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

	class NotificationAdapter extends BaseAdapter{
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
			
			Criteria criteria = criteriaList.get(position);
			TextView textView = (TextView) view.findViewById(R.id.criteria_title);
			textView.setText(criteria.getName());
			LinearLayout lay = (LinearLayout)view.findViewById(R.id.notification_list_layout);
			for(Menu menu : criteria.getMap().keySet()){
				lay.addView(new MenuView(getActivity(), menu.getTitle(), menu.getDescription()));
				for(Mensa mensa : criteria.getMap().get(menu)){
					RelativeLayout rel = (RelativeLayout)inflater.inflate(R.layout.daily_section_title_bar, null);
					TextView text = (TextView) rel.getChildAt(0);
					//maybe set a click listener to textfield to get to the byMensa view..
					text.setText(mensa.getName());
					ImageButton favoriteButton = (ImageButton)rel.getChildAt(1);
					favoriteButton.setOnClickListener(new FavoriteButtonListener(mensa, favoriteButton));
					ImageButton mapButton =(ImageButton)rel.getChildAt(2);
					mapButton.setOnClickListener(new MapButtonListener(mensa, NotificationFragment.this));
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

	}
}
