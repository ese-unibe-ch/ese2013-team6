package com.ese2013.mub;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.os.Bundle;
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
import com.ese2013.mub.model.Model;
import com.ese2013.mub.service.CriteriaMatcher;
import com.ese2013.mub.util.Criteria;

public class NotificationFragment extends Fragment {
	private NotificationAdapter notificationAdapter;
	private List<Criteria> criteriaList;
	private ListView list;

	
	
	@Override
	public void onStart() {
		super.onStart();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences pref = new Preferences();
		
		//TODO get set of Criteria not just criteria;
		Set<String> criteria = new TreeSet<String>();
		criteria.add(pref.getNotificationFood(this.getActivity()));
		criteria.add("paniert");
		boolean allMensas = pref.getNotificationMensas(this.getActivity()) == 0 ? true : false;
		
		CriteriaMatcher criteriaMatcher = new CriteriaMatcher();
		List<Mensa> mensas = allMensas ? Model.getInstance().getMensas() : Model.getInstance().getFavoriteMensas();
		
		criteriaList = criteriaMatcher.match(criteria, mensas);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		View view = inflater.inflate(R.layout.fragment_notification, container, false);
		
		if(criteriaList.isEmpty()){
			TextView text = (TextView) view.findViewById(R.id.no_crit_text);
			text.setText(R.string.noMatches);
			return view;
		}
		notificationAdapter = new NotificationAdapter();
		
		
		list = (ListView) view.findViewById(R.id.notification_list);
		list.setAdapter(notificationAdapter);
		notificationAdapter.fill();
		return view;
	}
	
	public void onPause() {
		super.onPause();
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
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
			criteriaTitle.setText(criteria.getName().toUpperCase(Locale.getDefault()));
			
			
			for (Menu menu : criteria.getMap().keySet()) {
				TextView menuHeader = new TextView(getActivity());
				menuHeader.setText(R.string.givenMenu);
				layout.addView(menuHeader);
				layout.addView(new MenuView(getActivity(), menu));
				TextView mensaHeader = new TextView(getActivity());
				mensaHeader.setText(R.string.servedInMensa);
				layout.addView(mensaHeader);
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
			super.notifyDataSetChanged();
		}

		@Override
		public void sendListToMenusIntent(Mensa mensa) {
			((DrawerMenuActivity) getActivity()).launchByMensaAtGivenPage(mensa
					.getId());
		}
		public void fill(){
			adapterList = criteriaList;
		}
	}
}
