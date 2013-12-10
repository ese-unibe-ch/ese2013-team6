package com.ese2013.mub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ese2013.mub.model.Mensa;

/**
 * Shows a list of all available Mensas and fills this list with the
 * MensaListAdapter
 * 
 */
public class MensaListFragment extends Fragment {
	private MensaListAdapter adapter;
	private ListView mensaListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.mensa_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		adapter = new MensaListAdapter(getActivity(), android.R.layout.simple_list_item_1, this);

		View view = inflater.inflate(R.layout.fragment_mensa_list, container, false);

		mensaListView = (ListView) view.findViewById(R.id.mensa_view_layout);
		mensaListView.setAdapter(adapter);
		adapter.fill();

		return view;
	}

	/**
	 * Calls the Activity for changing the fragment and start DailyFragment at
	 * the position of this mensa
	 * 
	 * @param mensa
	 *            Mensa Object on the current field
	 */
	public void sendListToMenusIntent(Mensa mensa) {
		((DrawerMenuActivity) getActivity()).launchByMensaAtGivenPage(mensa.getId());
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		getActivity().setTitle(R.string.mensa_list);
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
