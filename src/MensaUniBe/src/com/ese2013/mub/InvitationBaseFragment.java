package com.ese2013.mub;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * Base Fragment for the Invitations and Friends. It displays Fragments in a
 * ViewPager and makes them accessible through the
 * ActionBar.NAVIGATION_MODE_TABS
 * 
 */
public class InvitationBaseFragment extends Fragment implements ActionBar.TabListener {
	private ViewPager viewPager;
	private ActionBar actionBar;
	private String[] tabs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.invitations);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_base_invitations, container, false);
		viewPager = (ViewPager) view.findViewById(R.id.invitation_pager);
		viewPager.setAdapter(new InvitationPageAdapter(getChildFragmentManager()));

		actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		tabs = getActivity().getResources().getStringArray(R.array.invitation_tabs_titles);
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
		}
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		return view;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// method from tab listener, no need to handle reselect.
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// method from tab listener, no need to handle unselect.
	}

	@Override
	public void onPause() {
		actionBar.removeAllTabs();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		onDestroyOptionsMenu();
		super.onPause();
	}

	@Override
	public void onResume() {
		actionBar = getActivity().getActionBar();
		getActivity().setTitle(R.string.invitations);
		actionBar.removeAllTabs();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		actionBar.removeAllTabs();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		onDestroyOptionsMenu();
		super.onDestroy();
	}

	public void setPagerToFriends() {
		viewPager.setCurrentItem(2);
		actionBar.selectTab(actionBar.getTabAt(2));
	}

	/**
	 * 
	 * Pager adapter for the {@link InvitationBaseFragment} viewPager. adds
	 * statically the three to be displayed Fragments to the Pager.
	 */
	private static class InvitationPageAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments = new ArrayList<Fragment>();

		public InvitationPageAdapter(FragmentManager fm) {
			super(fm);
			fragments.add(InvitedFragment.newInstance(new InvitedListAdapter()));
			fragments.add(InvitedFragment.newInstance(new InvitesListAdapter()));
			fragments.add(new FriendsListFragment());
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}
}
