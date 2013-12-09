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
import android.widget.Toast;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.Observer;

public class HomeFragment extends Fragment implements Observer {

	public static final String POSITION = "com.ese2013.mub.HomeFragment.position";
	private FragmentStatePagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;

	private boolean showDailyPlans = true, showOnlyFavorites = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		FragmentManager fm = getChildFragmentManager();
		if (showDailyPlans)
			sectionsPagerAdapter = new MenuSectionsPagerAdapter(fm);
		else
			sectionsPagerAdapter = new MensaSectionsPagerAdapter(fm);

		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);

		onAttach(getActivity());

		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		if (showDailyPlans && dayOfWeek < 6 && dayOfWeek > 1)
			viewPager.setCurrentItem(dayOfWeek - 2);
		handleGivenArguments();
		Model.getInstance().addObserver(this);
		getActivity().getActionBar().setDisplayShowCustomEnabled(true);
		return view;
	}

	@Override
	public void onNotifyChanges(Object... message) {
		if (message.length > 0)
			Toast.makeText(getActivity(), getActivity().getString((Integer) message[0]), Toast.LENGTH_SHORT).show();
		sectionsPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getActionBar().setDisplayShowCustomEnabled(false);
		Model.getInstance().removeObserver(this);
	}

	public void setShowDailyPlans(boolean showDailyPlans) {
		this.showDailyPlans = showDailyPlans;
	}

	public void setShowOnlyFavorites(boolean showOnlyFavorites) {
		this.showOnlyFavorites = showOnlyFavorites;
	}

	private void handleGivenArguments() {
		if (getArguments() != null) {
			Bundle bundle = getArguments();
			int pos = bundle.getInt(POSITION, 0);
			viewPager.setCurrentItem(pos - 1);
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	private class MensaSectionsPagerAdapter extends FragmentStatePagerAdapter {
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

	private class MenuSectionsPagerAdapter extends FragmentStatePagerAdapter {
		private ArrayList<Day> days;
		private Model model = Model.getInstance();

		public MenuSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			if (model.noMensasLoaded())
				days = new ArrayList<Day>();
			else
				days = getDays();
		}

		/**
		 * Instantiates to fragment which is currently displayed
		 */
		@Override
		public Fragment getItem(int position) {
			return DailyPlanFragment.newInstance(days.get(position), showOnlyFavorites);
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
				days = getDays();
			super.notifyDataSetChanged();
		}

		private ArrayList<Day> getDays() {
			return new ArrayList<Day>(model.getMensas().get(0).getMenuplan().getDays());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
