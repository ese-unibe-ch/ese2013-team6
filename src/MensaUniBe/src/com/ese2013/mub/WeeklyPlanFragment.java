package com.ese2013.mub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ese2013.mub.map.MapButtonListener;
import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.Model;

/**
 * This fragment displays the weekly menu plan for the given mensa.
 */
public class WeeklyPlanFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private Mensa mensa;

	public WeeklyPlanFragment() {
	}

	/**
	 * Maybe it would be better to send the mensa via a Bundle. Depends on the
	 * implementation of the mensa class.
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
			return rootView;

		setUpMapButton(rootView);
		setUpFavoriteButton(rootView);

		for (DailyMenuplan dailyPlan : mensa.getMenuplan()) {
			LinearLayout menuLayout = new LinearLayout(container.getContext());
			menuLayout.setOrientation(LinearLayout.VERTICAL);

			for (Menu menu : dailyPlan.getMenus()) {
				menuLayout.addView(new MenuView(container.getContext(), menu, dailyPlan.getDay()));
				decideToggleState(menuLayout, menu, dailyPlan.getDay());
			}

			TextView text = (TextView) inflater.inflate(R.layout.section_title_text, null);
			text.setText(dailyPlan.getDateString());

			text.setOnClickListener(new ToggleListener(menuLayout, getActivity()));

			layout.addView(text);
			layout.addView(menuLayout);

		}
		return rootView;
	}

	private void decideToggleState(LinearLayout menuLayout, Menu menu, Day day) {
		menuLayout.setVisibility((day.equals(Day.today()) ? View.VISIBLE : View.GONE));
	}

	private void setUpFavoriteButton(View rootView) {
		ImageButton favorite = (ImageButton) rootView.findViewById(R.id.page_title_favorite_button);
		favorite.setImageResource((mensa.isFavorite()) ? R.drawable.ic_fav : R.drawable.ic_fav_grey);
		favorite.setOnClickListener(new FavoriteButtonListener(mensa, favorite));
	}

	private void setUpMapButton(View rootView) {
		ImageButton map = (ImageButton) rootView.findViewById(R.id.page_title_map_button);
		map.setImageResource(R.drawable.ic_map);
		map.setOnClickListener(new MapButtonListener(mensa, this));
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}