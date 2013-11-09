package com.ese2013.mub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.DirectionsJSONParser;
import com.ese2013.mub.util.JsonDataRequest;
import com.ese2013.mub.util.NamedLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

//TODO: Zoom, Settings TravelMode

public class MapFragment extends Fragment {
	private static final String TRAVEL_MODE_WALKING = "walking";
	private static final float DETAIL_ZOOM = 17;
	private GoogleMap map;
	private List<Mensa> mensaList;
	private ArrayList<NamedLocation> mensaLocations = new ArrayList<NamedLocation>();
	private ArrayList<NamedLocation> spinnerList = new ArrayList<NamedLocation>();
	private LocationManager locationManager;
	private NamedLocation currentNamedLocation;
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
			currentNamedLocation = new NamedLocation(rawLocation, "My Location", BitmapDescriptorFactory.HUE_AZURE);
			spinnerList.add(currentNamedLocation);
		}

		model = Model.getInstance();
		mensaList = model.getMensas();
		mensaLocations = getNamedLocationsFromMensas();

		map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		if (map == null)
			return view;

		spinnerList.addAll(mensaLocations);
		namedLocationAdapter = new ArrayAdapter<NamedLocation>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
				spinnerList);
		spinFocus = (Spinner) view.findViewById(R.id.focus_spinner);
		spinFocus.setAdapter(namedLocationAdapter);
		addListenerOnSpinnerItemSelection(spinFocus);
		setSpinnerDefault(spinFocus);

		Button getDirButton = (Button) view.findViewById(R.id.get_directions_button);
		addOnCLickListener(getDirButton);
		repaintMap();
		zoomOnContent();
		return view;
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
				currentNamedLocation.setLocation(location);
				repaintMap();
			}

			@Override
			public void onProviderDisabled(String arg0) {
			}

			@Override
			public void onProviderEnabled(String arg0) {
				currentNamedLocation.setLocation(getLocation());
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};
		locationManager.requestLocationUpdates(provider, 1000, 15, locationListener);
	}

	private void zoomTo(int mensaId) {
		String string = "";
		for (Mensa m : mensaList) {
			if (m.getId() == mensaId) {
				string = m.getName();
				break;
			}
		}

		for (NamedLocation nl : mensaLocations) {
			if (nl.getName().equals(string)) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(nl.getLatLng(), DETAIL_ZOOM));
				int pos = namedLocationAdapter.getPosition(nl);
				spinFocus.setSelection(pos);
				if (currentLocationAvailable()) {
					drawRouteFromTo(currentNamedLocation, nl);
				}
			}
		}
	}

	private ArrayList<NamedLocation> getNamedLocationsFromMensas() {
		ArrayList<NamedLocation> results = new ArrayList<NamedLocation>();
		for (Mensa m : mensaList)
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
		drawMensas();
	}

	private void drawRouteFromTo(NamedLocation currentNamedLocation, NamedLocation destination) {
		LatLng origin = new LatLng(currentNamedLocation.getLatitude(), currentNamedLocation.getLongitude());
		LatLng dest = new LatLng(destination.getLatitude(), destination.getLongitude());

		String url = getDirectionsUrl(origin, dest, TRAVEL_MODE_WALKING);
		DownloadTask downloadTask = new DownloadTask();
		downloadTask.execute(url);
	}

	private boolean currentLocationAvailable() {
		if (currentNamedLocation != null)
			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		else
			return false;
	}

	private void setSpinnerDefault(Spinner spinFocus) {
		Mensa closest = null;
		if (currentNamedLocation != null)
			closest = getClosestMensa(currentNamedLocation);

		if (closest != null) {
			NamedLocation closestNameLoc = null;
			for (NamedLocation nl : spinnerList) {
				if (nl.getName().equals(closest.getName())) {
					closestNameLoc = nl;
				}
			}
			int pos = namedLocationAdapter.getPosition(closestNameLoc);
			spinFocus.setSelection(pos);
		} else {
			if (model.favoritesExist()) {
				Mensa fav = model.getFavoriteMensas().get(0);
				NamedLocation favNameLoc = null;
				for (NamedLocation nl : spinnerList) {
					if (nl.getName() == fav.getName()) {
						favNameLoc = nl;
					}
				}
				int pos = namedLocationAdapter.getPosition(favNameLoc);
				spinFocus.setSelection(pos);
			}
		}
	}

	private void addOnCLickListener(Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentLocationAvailable()) {
					map.clear();
					repaintMap();
					drawRouteFromTo(currentNamedLocation, selectedLocation);
				}
			}
		});
	}

	private void addListenerOnSpinnerItemSelection(Spinner spinFocus) {
		spinFocus.setOnItemSelectedListener(new OnItemSelectedListener() {
			// private LatLng selectedLocation;
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

	private void drawMensas() {
		for (int i = 0; i < namedLocationAdapter.getCount(); i++) {
			map.addMarker(namedLocationAdapter.getItem(i).getMarker());
		}
	}

	private Mensa getClosestMensa(Location location) {
		Mensa result = null;
		float smallestDist = Integer.MAX_VALUE;
		for (Mensa m : mensaList) {
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

	private String getDirectionsUrl(LatLng origin, LatLng dest, String transportMode) {
		String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
		String sensor = "sensor=false";
		String mode = "mode=" + transportMode;
		String webServiceParams = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
		String outputFormat = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/" + outputFormat + "?" + webServiceParams;
		return url;
	}

	private class DownloadTask extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... url) {
			List<List<HashMap<String, String>>> routes = null;
			try {
				JSONObject data = new JsonDataRequest(url[0]).execute();
				DirectionsJSONParser parser = new DirectionsJSONParser();
				routes = parser.parse(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			super.onPostExecute(result);
			// Drawing polyline in the Google Map for the route
			map.addPolyline(createPolyLine(result));
		}
	}

	private static PolylineOptions createPolyLine(List<List<HashMap<String, String>>> result) {
		ArrayList<LatLng> points = null;
		PolylineOptions lineOptions = null;
		// Traversing through all the routes
		for (int i = 0; i < result.size(); i++) {
			points = new ArrayList<LatLng>();
			lineOptions = new PolylineOptions();

			// Fetching i-th route
			List<HashMap<String, String>> path = result.get(i);

			// Fetching all the points in i-th route
			for (int j = 0; j < path.size(); j++) {
				HashMap<String, String> point = path.get(j);
				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				LatLng position = new LatLng(lat, lng);
				points.add(position);
			}
			// Adding all the points in the route to LineOptions
			lineOptions.addAll(points);
			lineOptions.width(3);
			lineOptions.color(Color.RED);
		}
		return lineOptions;
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