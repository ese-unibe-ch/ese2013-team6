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

public class InvitationBaseFragment extends Fragment implements ActionBar.TabListener{
	private ViewPager viewPager;
	private ActionBar actionBar;
	 private String[] tabs = { "Invites", "Invited", "Friends" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_base_invitations, container, false);
		viewPager = (ViewPager)view.findViewById(R.id.invitation_pager);
		viewPager.setAdapter(new InvitationPageAdapter(getChildFragmentManager()));
		
		actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		 for (String tab_name : tabs) {
	            actionBar.addTab(actionBar.newTab().setText(tab_name)
	                    .setTabListener(this));
	        }
		 viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			 
			    @Override
			    public void onPageSelected(int position) {
			        // on changing the page
			        // make respected tab selected
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
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
		
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}
	class InvitationPageAdapter extends FragmentPagerAdapter{
		
		private List<InvitationFragments> fragments = new ArrayList<InvitationFragments>();
		
		public InvitationPageAdapter(FragmentManager fm) {
			super(fm);
			//TODO add Fragments;
			fragments.add(new InvitesFragment());
			fragments.add(new FriendsListFragment());
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position).getInstance();
		}
		@Override
		public int getCount() {
			return fragments.size();
		}
	}
}
