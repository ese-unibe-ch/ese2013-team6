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
import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.User;
import com.ese2013.mub.util.Observer;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class HomeFragment extends Fragment implements Observer {

	public static final String POSITION = "com.ese2013.mub.HomeFragment.position";
	private FragmentStatePagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;

	private static boolean showFavorites = true;
	private static boolean showAllByDay = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

		FragmentManager fm = getChildFragmentManager();
		if (showFavorites)
			sectionsPagerAdapter = new MenuSectionsPagerAdapter(fm);
		else
			sectionsPagerAdapter = new MensaSectionsPagerAdapter(fm);

		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);

		onAttach(getActivity());

		if (showFavorites && dayOfWeek < 6 && dayOfWeek > 1)
			viewPager.setCurrentItem(dayOfWeek - 2);
		handleGivenArguments();
		Model.getInstance().addObserver(this);
		getActivity().getActionBar().setDisplayShowCustomEnabled(true);
		return view;
	}

	@Override
	public void onNotifyChanges() {

		try {
			if (LoginService.isLoggedIn()) {
				new OnlineDBHandler().retrieveFriends(LoginService.getLoggedInUser());
				ArrayList<User> invitees = new ArrayList<User>();
				invitees.add(LoginService.getLoggedInUser().getFriends().get(0));
				invitees.add(LoginService.getLoggedInUser());
				// new OnlineDBHandler().sendInvitation(new
				// Invitation(LoginService.getLoggedInUser(), invitees,
				// "Come eating you fool", 1, new java.util.Date()));
				List<Invitation> invitations = new OnlineDBHandler().getRetrievedInvitations(LoginService.getLoggedInUser());
				for (Invitation i : invitations) {
					System.out.println(i.getFrom().getNick() + "," + i.getFrom().getEmail() + ", " + i.getMessage());
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sectionsPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getActionBar().setDisplayShowCustomEnabled(false);
		Model.getInstance().removeObserver(this);
	}

	/**
	 * Returns if all menus of a day should be displayed.
	 * 
	 * @return true if all menus should be displayed, false if only favorites
	 *         should be displayed.
	 */
	public static boolean getShowAllByDay() {
		return showAllByDay;
	}

	public void setFavorites(boolean bool) {
		showFavorites = bool;
	}

	public void setShowAllByDay(boolean bool) {
		showAllByDay = bool;
	}

	public void handleGivenArguments() {
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
			if (model.noMensasLoaded())
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
				days = new ArrayList<Day>(model.getMensas().get(0).getMenuplan().getDays());
			super.notifyDataSetChanged();
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
