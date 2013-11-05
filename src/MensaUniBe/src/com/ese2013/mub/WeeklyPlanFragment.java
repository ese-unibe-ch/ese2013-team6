package com.ese2013.mub;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.Model;

/**
 * This fragment displays the weekly menu plan for the given mensa.
 */
public class WeeklyPlanFragment extends PlanFragment {
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
		
		ImageButton favorite = (ImageButton) rootView.findViewById(R.id.page_title_favorite_button);
		ImageButton map = (ImageButton) rootView.findViewById(R.id.page_title_map_button);
		map.setImageResource(R.drawable.ic_map);
		map.setOnClickListener(new MapButtonListener(mensa, this));
		
		if(mensa.isFavorite())
			favorite.setImageResource(R.drawable.ic_fav);
		else
			favorite.setImageResource(R.drawable.ic_fav_grey);
		
			favorite.setOnClickListener(new FavoriteButtonListener(mensa, favorite));
		
		for (DailyMenuplan d : mensa.getMenuplan()) {
			
			TextView text = (TextView)inflater.inflate(R.layout.section_title_text, null);
			text.setText(d.getDateString());
			layout.addView(text);
			
			LinearLayout menuLayout = new LinearLayout(container.getContext());
			menuLayout.setOrientation(LinearLayout.VERTICAL);
			
			for (Menu menu : d.getMenus()) {
				menuLayout.addView(new MenuView(container.getContext(), menu.getTitle(), menu.getDescription()));
			}
			
			text.setOnClickListener(new ToggleListener(menuLayout, getActivity()));
			
			Date date = Calendar.getInstance().getTime();
			
			if(d.getDateString().equals(new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.getDefault()).format(date)))
				menuLayout.setVisibility(View.VISIBLE);
			else
				menuLayout.setVisibility(View.GONE);
			layout.addView(menuLayout);
			
		}
		return rootView;
	}
}