package com.ese2013.mub;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.Observer;

public class HomeFragment extends Fragment implements Observer {
	
	private FragmentStatePagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;

	private static boolean showFavorites = true;	// if true, Spinner should be on favorites list
	private static boolean showAllByDay = false;	// if true, Spinner should be on list of all menus of one day
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
		
		this.onAttach(getActivity());
		
		if(showFavorites && dayOfWeek < 6 && dayOfWeek > 1)
			viewPager.setCurrentItem(dayOfWeek-2);
		handleGivenArguments();
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

	public void handleGivenArguments(){
		if(getArguments() != null){
			Bundle bundle = getArguments();
			int pos = bundle.getInt("POSITION", 0);
			viewPager.setCurrentItem(pos-1);
		}
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
		private ArrayList<Day> days;
		private Model model = Model.getInstance();
		
		public MenuSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			if (Model.getInstance().noMensasLoaded())
				days = new ArrayList<Day>();
			else
				days = new ArrayList<Day>(model.getMensas().get(0).getMenuplan().getDays());
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
			return days.get(position).getDayOfWeekString();
		}

		@Override
		public void notifyDataSetChanged() {
			if (!model.noMensasLoaded())
				days = new ArrayList<Day>(Model.getInstance().getMensas().get(0).getMenuplan().getDays());
			super.notifyDataSetChanged();
		}
	}
}
