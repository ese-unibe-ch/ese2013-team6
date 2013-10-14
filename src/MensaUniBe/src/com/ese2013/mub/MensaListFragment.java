package com.ese2013.mub;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
// not sure which data structure yet, for test purpose only

public class MensaListFragment extends Fragment {

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
		return Model.getInstance().getMensas();
	}

}
