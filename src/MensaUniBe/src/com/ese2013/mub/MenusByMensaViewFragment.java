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
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState); //is this needed?
		View view = inflater.inflate(R.layout.fragment_menusbymensa_view, container, false);
		
	    // Create the adapter that will return a fragment for each of the three primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
    
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new WeeklyPlanFragment();
            Bundle args = new Bundle();
            args.putInt(WeeklyPlanFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
                case 2:
                    return getString(R.string.title_section3);
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class WeeklyPlanFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public WeeklyPlanFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.section_linear_layout);
            layout.addView(new MenuView(container.getContext(), "NATÜRLICH VEGI", "Muskatkürbis-Törtchen\nReis mit Tomatenwürfeli\nCHF 6.60 / 12.60"));
            layout.addView(new MenuView(container.getContext(), "EINFACH GUT", "Maispoulardenbrust an Lauchsauce\nSpiralen\nPeperonate\nFleisch: Frankreich\nCHF 6.60 / 12.60"));
            layout.addView(new MenuView(container.getContext(), "voll anders", "Pizza mit Belag nach Wahl\nMenüsalat\nFleisch: Schweiz\nCHF 9.90"));
           
            layout.addView(new MenuView(container.getContext(), "NATÜRLICH VEGI", "Muskatkürbis-Törtchen\nReis mit Tomatenwürfeli\nCHF 6.60 / 12.60"));
            layout.addView(new MenuView(container.getContext(), "EINFACH GUT", "Maispoulardenbrust an Lauchsauce\nSpiralen\nPeperonate\nFleisch: Frankreich\nCHF 6.60 / 12.60"));
            layout.addView(new MenuView(container.getContext(), "voll anders", "Pizza mit Belag nach Wahl\nMenüsalat\nFleisch: Schweiz\nCHF 9.90"));
            
            return rootView;
        }
    }
}
