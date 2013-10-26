package com.ese2013.mub;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.Model;

public class DailyPlanFragment extends Fragment {
	private Date day;
	private List<Mensa> mensas;

	public DailyPlanFragment() {
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public static DailyPlanFragment newInstance(Date day) {
		DailyPlanFragment frag = new DailyPlanFragment();
		frag.setDay(day);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (MenusByMensaViewFragment.getShowAllByDay()) {
			mensas = Model.getInstance().getMensas();
		} else {
			mensas = Model.getInstance().getFavoriteMensas();
		}
		View rootView = inflater.inflate(R.layout.fragment_home_scrollable_content, container, false);
		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.section_linear_layout);
		if (Model.getInstance().noMensasLoaded())
			return rootView; // hacky fix for the case when app is recreated
								// due screen rotation, needs to be handled
								// through proper state management and so
								// on.
		
		/* Date of the displayed day in Favorites View */
		SimpleDateFormat df = new SimpleDateFormat( "dd. MMMM yyyy", Locale.getDefault());
		TextView textDateOfDayOfWeek = new TextView(container.getContext());
		textDateOfDayOfWeek.setText(df.format(day));
		
		layout.addView(textDateOfDayOfWeek);
		
		Log.d("CALL", "BEFORE LOOP");
		for (Mensa mensa : mensas) {
				DailyMenuplan d = mensa.getMenuplan().getDailymenuplan(day);
				TextView text = new TextView(container.getContext());
				text.setText(mensa.getName());
				layout.addView(text);
				Log.d("CALL", "IN SE LOOP");
				for (Menu menu : d.getMenus()) {
					layout.addView(new MenuView(container.getContext(), menu.getTitle(), menu.getDescription()));
				}
		}
		return rootView;
	}
}