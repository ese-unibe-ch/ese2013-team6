package com.ese2013.mub;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);
		List<Mensa> mensaList = Model.getInstance().getMensas();
		map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if (map == null) 
			return view;
		for (Mensa m : mensaList) {
			LatLng mensaLocation = new LatLng(m.getLatitude(), m.getLongitude());
			map.addMarker(new MarkerOptions().position(mensaLocation).title(m.getName()));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(mensaLocation, 15));
		}
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}
}