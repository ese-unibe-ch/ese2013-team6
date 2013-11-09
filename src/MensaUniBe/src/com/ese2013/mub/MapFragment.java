package com.ese2013.mub;

import java.util.ArrayList;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.DirectionsDownloadTask;
import com.ese2013.mub.util.NamedLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

//TODO: Zoom, Settings TravelMode

public class MapFragment extends Fragment {
	private static final String TRAVEL_MODE_WALKING = "walking";
	private static final float DETAIL_ZOOM = 17;
	private GoogleMap map;
	private ArrayList<NamedLocation> spinnerList = new ArrayList<NamedLocation>();
	private NamedLocation currentNamedLocation;
	private LocationManager locationManager;
	private ArrayAdapter<NamedLocation> namedLocationAdapter;
	private NamedLocation selectedLocation;
	private Spinner spinFocus;
	private Model model;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);

		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		registerLocationListener();
		Location rawLocation = getLocation();
		if (rawLocation != null) {
			updateCurrentNamedLocation(rawLocation);
			spinnerList.add(currentNamedLocation);
		}

		model = Model.getInstance();
		map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		if (map == null)
			return view;

		spinnerList.addAll(getNamedLocationsFromMensas());
		namedLocationAdapter = new ArrayAdapter<NamedLocation>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
				spinnerList);
		spinFocus = (Spinner) view.findViewById(R.id.focus_spinner);
		spinFocus.setAdapter(namedLocationAdapter);
		addListenerOnSpinnerItemSelection(spinFocus);
		setSpinnerDefault();

		Button getDirButton = (Button) view.findViewById(R.id.get_directions_button);
		addOnCLickListener(getDirButton);
		repaintMap();
		zoomOnContent();
		return view;
	}

	private void updateCurrentNamedLocation(Location location) {
		if (currentNamedLocation == null)
			currentNamedLocation = new NamedLocation(location, "My Location", BitmapDescriptorFactory.HUE_AZURE);
		else
			currentNamedLocation.setLocation(location);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle == null || bundle.isEmpty()) {
			zoomOnContent();
			return;
		}

		Integer mensaId = (Integer) bundle.get("mensa.id");
		if (mensaId != null)
			zoomTo(mensaId.intValue());
		else
			zoomOnContent();
	}

	private void registerLocationListener() {
		String provider = locationManager.getBestProvider(new Criteria(), true);
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				updateCurrentNamedLocation(location);
				repaintMap();
			}

			@Override
			public void onProviderDisabled(String arg0) {
			}

			@Override
			public void onProviderEnabled(String arg0) {
				updateCurrentNamedLocation(getLocation());
				repaintMap();
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};
		locationManager.requestLocationUpdates(provider, 1000, 15, locationListener);
	}

	private void zoomTo(int mensaId) {
		for (NamedLocation nl : spinnerList) {
			if (nl.isLocationOfMensa(mensaId)) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(nl.getLatLng(), DETAIL_ZOOM));
				setSpinnerTo(nl);
				if (currentLocationAvailable()) {
					drawRouteFromTo(currentNamedLocation, nl);
				}
			}
		}
	}

	private void setSpinnerTo(NamedLocation location) {
		spinFocus.setSelection(namedLocationAdapter.getPosition(location));
	}

	private ArrayList<NamedLocation> getNamedLocationsFromMensas() {
		ArrayList<NamedLocation> results = new ArrayList<NamedLocation>();
		for (Mensa m : model.getMensas())
			results.add(new NamedLocation(m));
		return results;
	}

	public void zoomOnContent() {
		double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;
		for (int i = 0; i < spinnerList.size(); i++) {
			NamedLocation n = spinnerList.get(i);
			minLat = Math.min(minLat, n.getLatitude());
			maxLat = Math.max(maxLat, n.getLatitude());
			minLon = Math.min(minLon, n.getLongitude());
			maxLon = Math.max(maxLon, n.getLongitude());
		}
		LatLng southWest = new LatLng(minLat, minLon);
		LatLng northEast = new LatLng(maxLat, maxLon);
		LatLngBounds bounds = new LatLngBounds(southWest, northEast);
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 400, 400, 10));
	}

	private void repaintMap() {
		map.clear();
		drawedNamedLocations();
	}

	private void drawRouteFromTo(NamedLocation currentNamedLocation, NamedLocation destination) {
		DirectionsDownloadTask downloadTask = new DirectionsDownloadTask(currentNamedLocation.getLatLng(),
				destination.getLatLng(), TRAVEL_MODE_WALKING, this);
		downloadTask.execute();
	}

	public void onDirectionsDownloadFinished(DirectionsDownloadTask downloadTask) {
		if (downloadTask.wasSuccesful())
			map.addPolyline(downloadTask.getPolyline());
		else
			Toast.makeText(getActivity(), "Could not retrieve directions", Toast.LENGTH_LONG).show();
	}

	private boolean currentLocationAvailable() {
		return currentNamedLocation != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private void setSpinnerDefault() {

		if (model.noMensasLoaded())
			return;

		Mensa selectedMensa;
		if (currentLocationAvailable())
			selectedMensa = getClosestMensa(currentNamedLocation);
		else
			selectedMensa = model.favoritesExist() ? model.getFavoriteMensas().get(0) : model.getMensas().get(0);

		for (NamedLocation namedLoc : spinnerList)
			if (namedLoc.isLocationOfMensa(selectedMensa))
				selectedLocation = namedLoc;

		setSpinnerTo(selectedLocation);
	}

	private void addOnCLickListener(Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentLocationAvailable()) {
					repaintMap();
					drawRouteFromTo(currentNamedLocation, selectedLocation);
				}
			}
		});
	}

	private void addListenerOnSpinnerItemSelection(Spinner spinFocus) {
		spinFocus.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				NamedLocation namedLoc = (NamedLocation) parent.getItemAtPosition(position);
				selectedLocation = namedLoc;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

	}

	private void drawedNamedLocations() {
		for (int i = 0; i < namedLocationAdapter.getCount(); i++) {
			map.addMarker(namedLocationAdapter.getItem(i).getMarker());
		}
	}

	private Mensa getClosestMensa(Location location) {
		Mensa result = null;
		float smallestDist = Integer.MAX_VALUE;
		for (Mensa m : model.getMensas()) {
			Location mensaLocation = new Location("");
			mensaLocation.setLatitude(m.getLatitude());
			mensaLocation.setLongitude(m.getLongitude());
			float currentDist = location.distanceTo(mensaLocation);
			if (smallestDist > currentDist) {
				smallestDist = currentDist;
				result = m;
			}
		}
		return result;
	}

	private Location getLocation() {
		String provider = locationManager.getBestProvider(new Criteria(), true);
		return locationManager.getLastKnownLocation(provider);
	}

	/**
	 * Destroy method which is called by the android framework when the view is
	 * no longer needed. Here we remove the google map fragment which is
	 * embedded in our own map fragment.
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}
}