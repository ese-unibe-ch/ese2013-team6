package com.ese2013.mub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.Observer;

public class HomeFragment extends Fragment implements Observer {
	
	private FragmentStatePagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;

	private static boolean showFavorites = true;	// if true, Spinner should be on favorites list
	private static boolean showAllByDay = false;	// if true, Spinner should be on list of all menus of one day
													// else Spinner is on list of all menus of one mensa
	public static boolean getShowAllByDay(){
		return showAllByDay;
	}
	
	public void setFavorites(boolean bool) {
		showFavorites = bool;
	}
	
	public void setShowAllByDay(boolean bool){
		showAllByDay = bool;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	
		if (showFavorites) {
			sectionsPagerAdapter = new MenuSectionsPagerAdapter(getChildFragmentManager());
			
		} else {
			sectionsPagerAdapter = new MensaSectionsPagerAdapter(getChildFragmentManager());
		}
		
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		
		
		viewPager.setAdapter(sectionsPagerAdapter);
		
		if(showFavorites && dayOfWeek < 6 && dayOfWeek > 1)
			viewPager.setCurrentItem(dayOfWeek-2);
		
		if(getArguments() != null){
			Bundle bundle = getArguments();//TODO change in DrawerMenu form goTopage to setArguments();
			bundle.getInt("POSITION", 0);
		}
		
		Model.getInstance().addObserver(this);
		getActivity().getActionBar().setDisplayShowCustomEnabled(true);
		
		return view;
	}

	@Override
	public void onNotifyChanges() {
		sectionsPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getActionBar().setDisplayShowCustomEnabled(false);
		Model.getInstance().removeObserver(this);
	}
	public void goToPage(int pos){
		
		//viewPager.setCurrentItem(pos-1); //crashes badly!!
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class MensaSectionsPagerAdapter extends FragmentStatePagerAdapter {
		private List<Mensa> mensas = Model.getInstance().getMensas();

		public MensaSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * Instantiates to fragment which is currently displayed
		 */
		@Override
		public Fragment getItem(int position) {
			return WeeklyPlanFragment.newInstance(mensas.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return mensas.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mensas.get(position).getName();
		}

		@Override
		public void notifyDataSetChanged() {
			mensas = Model.getInstance().getMensas();
			super.notifyDataSetChanged();
		}

	}

	public class MenuSectionsPagerAdapter extends FragmentStatePagerAdapter {
		private ArrayList<Date> days;
		
		public MenuSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			if (Model.getInstance().noMensasLoaded())
				days = new ArrayList<Date>();
			else
				days = new ArrayList<Date>(Model.getInstance().getMensas().get(0).getMenuplan().getDays());
		}

		/**
		 * Instantiates to fragment which is currently displayed
		 */
		@Override
		public Fragment getItem(int position) {
			return DailyPlanFragment.newInstance(days.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return days.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
		SimpleDateFormat df = new SimpleDateFormat( "EEEE", Locale.getDefault());
		String dayOfWeek = df.format(days.get(position));
		return dayOfWeek;
		}

		@Override
		public void notifyDataSetChanged() {
			days = new ArrayList<Date>(Model.getInstance().getMensas().get(0).getMenuplan().getDays());
			super.notifyDataSetChanged();
		}

	}
}
