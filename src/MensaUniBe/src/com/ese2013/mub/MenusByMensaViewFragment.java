package com.ese2013.mub;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.model.Observer;

public class MenusByMensaViewFragment extends Fragment {
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menusbymensa_view, container, false);
		sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);
		return view;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements Observer {
		private ArrayList<Mensa> mensas = Model.getInstance().getMensas();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			Model.getInstance().addObserver(this);
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
		public void onNotifyChanges() {
			mensas = Model.getInstance().getMensas();
			notifyDataSetChanged();
		}

	}

	/**
	 * This fragment displays the weekly menu plan for the given mensa.
	 */
	public static class WeeklyPlanFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private Mensa mensa;

		public WeeklyPlanFragment() {
		}

		/**
		 * Maybe it would be better to send the mensa via a Bundle. Depends on
		 * the implementation of the mensa class.
		 */
		public void setMensa(Mensa mensa) {
			this.mensa = mensa;
		}

		public static WeeklyPlanFragment newInstance(Mensa mensa) {
			WeeklyPlanFragment frag = new WeeklyPlanFragment();
			frag.setMensa(mensa);
			return frag;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home_scrollable_content, container, false);
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.section_linear_layout);
			if (Model.getInstance().noMensasLoaded())
				return rootView; // hacky fix for the case when app is recreated
									// due screen rotation, needs to be handled
									// through proper state management and so
									// on.

			for (DailyMenuplan d : mensa.getMenuplan()) {
				TextView text = new TextView(container.getContext());
				text.setText(d.getDateString());
				layout.addView(text);

				for (Menu menu : d.getMenus()) {
					layout.addView(new MenuView(container.getContext(), menu.getTitle(), menu.getDescription()));
				}
			}
			return rootView;
		}
	}
}
