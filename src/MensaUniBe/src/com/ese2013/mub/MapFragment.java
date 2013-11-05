package com.ese2013.mub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.util.Log;
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
import com.ese2013.mub.util.DirectionsJSONParser;
import com.ese2013.mub.util.NamedLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

//TODO: Zoom, Setttings TravelMode

public class MapFragment extends Fragment {
	// private static final int INIT_ZOOM = 14;
	protected static final String TRAVEL_MODE_WALKING = "walking";
	protected static final float DETAIL_ZOOM = 17;
	private GoogleMap map;
	List<Mensa> mensaList;
	ArrayList<NamedLocation> mensaLocations = new ArrayList<NamedLocation>();
	private ArrayList<NamedLocation> spinnerList;
	private LocationManager locationManager;
	private NamedLocation currentNamedLocation;
	private ArrayAdapter<NamedLocation> adapter;
	private NamedLocation selectedLocation;
	private Spinner spinFocus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);
		
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		Location rawLocation = getLocation();
		if (rawLocation != null) {
			currentNamedLocation = new NamedLocation("My Location",
					rawLocation.getLatitude(), rawLocation.getLongitude());
		}

		mensaList = getMensaList();
		mensaLocations = convertMensasToNamedLocations(mensaList);

		map = ((SupportMapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		if (map == null)
			return view;

		drawOnMap();

		spinnerList = new ArrayList<NamedLocation>();
		spinnerList.addAll(mensaLocations);
		addCurrentLocationToSpinner();
		adapter = new ArrayAdapter<NamedLocation>(
				getActivity(), android.R.layout.simple_spinner_dropdown_item,
				spinnerList);
		spinFocus = (Spinner) view.findViewById(R.id.focus_spinner);
		spinFocus.setAdapter(adapter);
		addListenerOnSpinnerItemSelection(spinFocus);
		setSpinnerDefault(spinFocus);
		
		
		Button getDirButton = (Button) view.findViewById(R.id.get_directions_button);
		addOnCLickListener(getDirButton);
		return view;
	}



	



	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		boolean argumentsEmpty;
		try {
			argumentsEmpty = getArguments().isEmpty();
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			argumentsEmpty = true;
		}
		if (!argumentsEmpty){
			try {
				String mensaName = (String) getArguments().get("mensa.name");
				zoomTo(mensaName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				zoomOnContent();
			}
		}else{
			zoomOnContent();
		}
	}

	private void zoomTo(String string) throws Exception {
		if (MensaNameFound(mensaLocations,string)){
			for (NamedLocation nl : mensaLocations){
				if (nl.getName()==string){
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(nl.getLatLng(), DETAIL_ZOOM));
					int pos = adapter.getPosition(nl);
					spinFocus.setSelection(pos);
					if(currentLocationAvail()){
						drawRouteFromTo(currentNamedLocation, nl);
					}
				}
			}	
		}else{
			throw new Exception();
		}
		
		
		
	}



	private boolean MensaNameFound(ArrayList<NamedLocation> mensaLocations2,
			String string) {
		for (NamedLocation nl : mensaLocations){
			if (nl.getName()==string){
				return true;
			}
		}
		return false;
	}







	private ArrayList<NamedLocation> convertMensasToNamedLocations(
			List<Mensa> mensaList2) {
		ArrayList<NamedLocation> results = new ArrayList<NamedLocation>();
		for (Mensa m : mensaList2) {
			results.add(new NamedLocation(m));
		}
		return results;
	}

	private List<Mensa> getMensaList() {
		return Model.getInstance().getMensas();
	}

	private void addCurrentLocationToSpinner() {
		if (currentLocationAvail()) {
			NamedLocation currentNamedLocation = getCurrentNamedLocation();
			spinnerList.add(currentNamedLocation);
		}
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
//		map.addMarker(new MarkerOptions().position(southWest).title("southWest"));
//		map.addMarker(new MarkerOptions().position(northEast).title("northEast"));
		
		
		
		LatLngBounds bounds = new LatLngBounds(southWest, northEast);
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 400, 400, 10));
	}


	private void drawOnMap() {
		System.out.println(currentLocationAvail() + " + "
				+ favoriteMensaExists(mensaList));
		if (currentLocationAvail()) {
			NamedLocation currentNamedLocation = getCurrentNamedLocation();
			drawCurrentLocation(currentNamedLocation);
		}
		drawMensas();
	}

	
	private void drawCurrentLocation(NamedLocation currentNamedLocation) {
		map.clear();
		LatLng currentPosition = new LatLng(currentNamedLocation.getLatitude(),
				currentNamedLocation.getLongitude());
		map.addMarker(new MarkerOptions()
				.position(currentPosition)
				.snippet(
						"Lat:" + currentNamedLocation.getLatitude() + "Lng:"
								+ currentNamedLocation.getLongitude())
				.title("Your Location")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}



	private void drawRouteFromTo(NamedLocation currentNamedLocation,
			NamedLocation destination) {
		LatLng origin = new LatLng(currentNamedLocation.getLatitude(),
				currentNamedLocation.getLongitude());

		LatLng dest = new LatLng(destination.getLatitude(),
				destination.getLongitude());
		// Getting URL to the Google Directions API
		String url = getDirectionsUrl(origin, dest, TRAVEL_MODE_WALKING);

		DownloadTask downloadTask = new DownloadTask();

		// Start downloading json data from Google Directions API
		downloadTask.execute(url);

	}

	private NamedLocation getCurrentNamedLocation() {
		assert currentLocationAvail();
		return currentNamedLocation;
	}

	private boolean currentLocationAvail() {
		System.out.println("locManager enabled: "
				+ locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER));
		if (currentNamedLocation != null) {
			return locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} else {
			return false;
		}

	}


	private boolean favoriteMensaExists(List<Mensa> mensaList2) {
		boolean result = false;
		for (Mensa m : mensaList) {
			if (m.isFavorite()) {
				result = true;
			}
		}
		return result;
	}
	
	private void setSpinnerDefault(Spinner spinFocus) {
		
		Mensa closest = getClosestMensa(currentNamedLocation);
		
		if (closest != null) {
			NamedLocation closestNameLoc = null;
			for (NamedLocation nl : spinnerList) {
				if (nl.getName() == closest.getName()) {
					closestNameLoc = nl;
				}
			}
			int pos = adapter.getPosition(closestNameLoc);
			spinFocus.setSelection(pos);
		}else{
			if (favoriteMensaExists(getMensaList())){
				Mensa fav = getFavMensa();
				NamedLocation favNameLoc = null;
				for (NamedLocation nl : spinnerList){
					if (nl.getName() == fav.getName()) {
						favNameLoc = nl;
					}
				}
				int pos = adapter.getPosition(favNameLoc);
				spinFocus.setSelection(pos);
			}
		}
		
	}
	
	

	private Mensa getFavMensa() {
		Mensa result = null;
		for (Mensa m : mensaList) {
			if (m.isFavorite()) {
				result = m;
			}
		}
		return result;
	}

	
	private void addOnCLickListener(Button button){
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentLocationAvail()){
					map.clear();
					drawOnMap();
					drawRouteFromTo(currentNamedLocation, getSelectedLocation());
				}else{
					
				}
				
			}
		});
	}
	private void addListenerOnSpinnerItemSelection(Spinner spinFocus) {
		spinFocus.setOnItemSelectedListener(new OnItemSelectedListener() {

//			private LatLng selectedLocation;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				System.out.println(" onItemSelected position: "+position);
				 NamedLocation namedLoc = (NamedLocation) parent.getItemAtPosition(position);
				 System.out.println("onItemSelected: "+namedLoc);
				 setSelectedLocation(namedLoc);


			}


			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	}

	private void drawMensas() {
		for (Mensa m : mensaList) {
			LatLng mensaLocation = new LatLng(m.getLatitude(), m.getLongitude());
			map.addMarker(new MarkerOptions().position(mensaLocation).title(
					m.getName()));
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

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		Location location = locationManager.getLastKnownLocation(provider);

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// redraw the marker when get location update.
				// Toast.makeText(getActivity(), "Location changed",
				// Toast.LENGTH_SHORT).show();
				System.out.println("Draw on Map - onLocationChanged");
				drawOnMap();
			}

			@Override
			public void onProviderDisabled(String arg0) {
				Toast.makeText(getActivity(), "ProviderDisabled",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onProviderEnabled(String arg0) {
				Toast.makeText(getActivity(), "onProviderEnabled",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// Toast.makeText(getActivity(), "onStatusChanged",
				// Toast.LENGTH_SHORT).show();
				// drawCurrentLocation(getCurrentNamedLocation());
				// drawMensas();
			}
		};

		locationManager.requestLocationUpdates(provider, 200000, 100,
				locationListener);
		return location;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------------------

	private String getDirectionsUrl(LatLng origin, LatLng dest,
			String transportMode) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		String mode = "mode=" + transportMode;

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ mode;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
				e.printStackTrace();
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
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

			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);
		}
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
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		ft.remove(fragment);
		ft.commit();
		System.out.println("onDestroyView finished");
	}


	public NamedLocation getSelectedLocation() {
		return selectedLocation;
	}

	public void setSelectedLocation(NamedLocation selectedLocation) {
		this.selectedLocation = selectedLocation;
	}

	
}