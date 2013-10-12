package com.ese2013.mub;

import java.util.*;// not sure which data structure yet, for test purpose only
import com.ese2013.mub.Mensa.MensaBuilder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class MensaListFragment extends Fragment {

	private static List<Mensa> list; // dummy List, data structure?

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_mensa_list, container,
				false);

		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.mensa_view_layout);

		List<Mensa> list = getMensas();

		for (Mensa mensa : list) {
			layout.addView(new MensaInList(container.getContext(), mensa));
		}

		return view;
	}

	/**
	 * TODO: Change implementation, get List from webrequest
	 * 
	 * @return
	 */
	private static List<Mensa> getMensas() {
		list = new LinkedList<Mensa>();

		list.add(new MensaBuilder().setName("Mensa Gesellschaftsstrasse")
				.setAdress("Dummy adress\nadsfasdf\ndsfgafadg").build());
		list.add(new MensaBuilder().setName("Mensa UniTobler")
				.setAdress("Dummy adress\nadsfasdf\ndsfgafadg").build());
		return list;
	}

}
