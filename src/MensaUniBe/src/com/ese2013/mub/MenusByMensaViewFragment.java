package com.ese2013.mub;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenusByMensaViewFragment extends Fragment {
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;

	private DummyMensa[] mensas = {
			new DummyMensa("Mensa Gesellschaftsstrasse"),
			new DummyMensa("Mensa Unitobler"),			
			new DummyMensa("Cafeteria Maximum"),
			new DummyMensa("UNIESS - Bar Lounge"),
			new DummyMensa("UNIESS - Bistro"),
			new DummyMensa("Mensa und Cafeteria von Roll")};

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
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Instantiates to fragment which is currently displayed
         */
        @Override
        public Fragment getItem(int position) {
            return WeeklyPlanFragment.newInstance(mensas[position]);
        }

        @Override
        public int getCount() {
            return mensas.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	return mensas[position].name;
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
        public static final String ARG_SECTION_NUMBER = "section_number";
        
        private DummyMensa mensa;

        public WeeklyPlanFragment() {
        }
        
        /**
         * Maybe it would be better to send the mensa via a Bundle. Depends on the implementation of the mensa class.
         */
        public void setMensa(DummyMensa mensa) {
        	this.mensa = mensa;
        }
       
        public static WeeklyPlanFragment newInstance(DummyMensa mensa) {
        	WeeklyPlanFragment frag = new WeeklyPlanFragment();
        	frag.setMensa(mensa);
        	return frag;
        }
        

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_scrollable_content, container, false);
            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.section_linear_layout);
            
            for (java.util.Calendar day : mensa.menuPlans.keySet()) {
            	TextView text = new TextView(container.getContext());
            	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.getDefault());
            	text.setText(dateFormat.format(day.getTime()));
            	layout.addView(text);
            	
            	for (DummyMenu menu : mensa.menuPlans.get(day)) {
            		layout.addView(new MenuView(container.getContext(), menu.title, menu.desc));
            	}
            }
            return rootView;
        }
    }
    
    /**
     * THESE TWO CLASSES NEED TO BE REPLACED AS SOON AS POSSIBLE BY THE REAL IMPLEMENTATION
     */
    public class DummyMensa {
    	String name;
    	
    	DummyMenu[] mondayMenus = {new DummyMenu("natürlich vegi", "Muskatkürbis-Törtchen\nReis mit Tomatenwürfeli\nCHF 6.60 / 12.60"),
    							   new DummyMenu("einfach gut", "Maispoulardenbrust an Lauchsauce\nSpiralen\nPeperonate\nFleisch: Frankreich\nCHF 6.60 / 12.60"),
    							   new DummyMenu("voll anders", "Pizza mit Belag nach Wahl\nMenüsalat\nFleisch: Schweiz\nCHF 9.90")};
    	java.util.LinkedHashMap<java.util.Calendar, DummyMenu[]> menuPlans = new java.util.LinkedHashMap<java.util.Calendar, DummyMenu[]>();
    	DummyMensa(String name) {
    		this.name = name;
    		java.util.Calendar monday = new java.util.GregorianCalendar();
    		monday.set(2013, 9, 14);
    		menuPlans.put(monday, mondayMenus);
    		
    		java.util.Calendar tuesday = new java.util.GregorianCalendar();
    		tuesday.set(2013, 9, 15);
    		menuPlans.put(tuesday, mondayMenus); //they have the same food all the time ;)
    		
    		java.util.Calendar wednesday = new java.util.GregorianCalendar();
    		wednesday.set(2013, 9, 16);
    		menuPlans.put(wednesday, mondayMenus); //they have the same food all the time ;)
    		
    		java.util.Calendar thursday = new java.util.GregorianCalendar();
    		thursday.set(2013, 9, 17);
    		menuPlans.put(thursday, mondayMenus); //they have the same food all the time ;)
    	}
    	
    }
    
    public class DummyMenu {
    	String title, desc;
    	DummyMenu(String title, String desc) {
    		this.title = title;
    		this.desc = desc;
    	}
    }
}
