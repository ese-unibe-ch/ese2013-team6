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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

//TODO: Zoom, Setttings TravelMode

public class MapFragment extends Fragment {
	//private static final int INIT_ZOOM = 14;
	protected static final String TRAVEL_MODE_WALKING = "walking";
	protected static final float DETAIL_ZOOM = 17;
	private GoogleMap map;
	List<Mensa> mensaList;
	ArrayList<NamedLocation> mensaLocations = new ArrayList<NamedLocation>();
	private ArrayList<NamedLocation> spinnerList;
	private LocationManager locationManager;
	private NamedLocation currentNamedLocation;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location rawLocation = getLocation();
		if (rawLocation !=null){
			currentNamedLocation = new NamedLocation("My Location", rawLocation.getLatitude(), rawLocation.getLongitude());
		}
		
		mensaList = getMensaList();
		mensaLocations= convertMensasToNamedLocations(mensaList);
		
		map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		if (map == null) 
			return view;
		
		drawOnMap();
		
		spinnerList = new ArrayList<NamedLocation>();
		spinnerList.addAll(mensaLocations);
		addCurrentLocationToSpinner();
	    ArrayAdapter<NamedLocation> adapter = new ArrayAdapter<NamedLocation>(getActivity(),
	            android.R.layout.simple_spinner_dropdown_item, spinnerList);
	    Spinner spinFocus = (Spinner)view.findViewById(R.id.focus_spinner);
	    spinFocus.setAdapter(adapter);
	    addListenerOnSpinnerItemSelection(spinFocus);
	    
	    System.out.println("Zoom on Map - onCreate");
	    zoomOnContent(adapter, spinFocus);
        
		return view;
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
		if (currentLocationAvail()){
			NamedLocation currentNamedLocation = getCurrentNamedLocation();
			spinnerList.add(currentNamedLocation);
		}
	}



	private void zoomOnContent(ArrayAdapter<NamedLocation> adapter,
			Spinner spinFocus) {
		if (currentLocationAvail()){
			if (favouriteMensaSelected(mensaList)){
				LatLng currentPoint = new LatLng(getCurrentNamedLocation().getLatitude(), getCurrentNamedLocation().getLongitude());
				LatLng favPoint = new LatLng(getFavMensaNamedLocations().get(0).getLatitude(), getFavMensaNamedLocations().get(0).getLongitude());
				
//				LatLngBounds bounds = new LatLngBounds(currentPoint, favPoint);
//				System.out.println("bounds: "+bounds.toString());
////				map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
//				
//				 
//				LatLng center = bounds.getCenter();
//
//				 map.addMarker(new MarkerOptions()
//					.position(center)
//					.snippet("Lat:" + currentNamedLocation.getLatitude() + "Lng:"+ currentNamedLocation.getLongitude())
//					.title("Center")
//					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
				 
				int spinnerPosition = adapter.getPosition(getCurrentNamedLocation());
	    	    spinFocus.setSelection(spinnerPosition);
//	    	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));
			}else{
				int spinnerPosition = adapter.getPosition(getCurrentNamedLocation());
	    	    spinFocus.setSelection(spinnerPosition);
			}
		}else{
			if (favouriteMensaSelected(mensaList)){
				System.out.println("Zoom to: "+getFavMensaNamedLocations().get(0).toString());
				int spinnerPosition = adapter.getPosition(getFavMensaNamedLocations().get(0));
				System.out.println("spinnerPosition: "+spinnerPosition);
	    	    spinFocus.setSelection(spinnerPosition);
			}else{
				int spinnerPosition = adapter.getPosition(mensaLocations.get(2));
	    	    spinFocus.setSelection(spinnerPosition);
			}
		}
	}




	private void drawOnMap() {
		System.out.println(currentLocationAvail() + " + "+ favouriteMensaSelected(mensaList));
		if (currentLocationAvail()){
			NamedLocation currentNamedLocation = getCurrentNamedLocation();
			drawCurrentLocation(currentNamedLocation);
			if (favouriteMensaSelected(mensaList)){
				ArrayList<NamedLocation> favlocations = getFavMensaNamedLocations();
				drawMensasWithClosest(currentNamedLocation);
				drawRouteFromToMany(currentNamedLocation, favlocations);
			}else{
				drawMensas();
			}
		}else{
			drawMensas();
		}
	}

	

	private void drawMensasWithClosest(NamedLocation currentNamedLocation) {
		assert currentLocationAvail();
		try {
			Mensa closest= getClosestMensa(currentNamedLocation);
		
		for (Mensa m : mensaList) {
			LatLng mensaLocation = new LatLng(m.getLatitude(), m.getLongitude());
			if (m==closest){
				 map.addMarker(new MarkerOptions()
				.position(mensaLocation)
				.snippet("Lat:" + currentNamedLocation.getLatitude() + "Lng:"+ currentNamedLocation.getLongitude())
				.title(m.getName())
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			}else{
				map.addMarker(new MarkerOptions().position(mensaLocation).title(m.getName()));
			}
		}
		drawRouteFromTo(getCurrentNamedLocation(), new NamedLocation(closest));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	private void drawCurrentLocation(NamedLocation currentNamedLocation) {
		map.clear();
		LatLng currentPosition = new LatLng(currentNamedLocation.getLatitude(),
				currentNamedLocation.getLongitude());
		map.addMarker(new MarkerOptions()
		.position(currentPosition)
		.snippet("Lat:" + currentNamedLocation.getLatitude() + "Lng:"+ currentNamedLocation.getLongitude())
		.title("Your Location")
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	private void drawRouteFromToMany(NamedLocation currentNamedLocation,
			ArrayList<NamedLocation> favlocations) {
		LatLng origin = new LatLng(currentNamedLocation.getLatitude(), currentNamedLocation.getLongitude());
		
		for (NamedLocation namedL : favlocations){
			 LatLng dest = new LatLng(namedL.getLatitude(), namedL.getLongitude());
			 // Getting URL to the Google Directions API
	        String url = getDirectionsUrl(origin, dest, TRAVEL_MODE_WALKING);
	
	        DownloadTask downloadTask = new DownloadTask();
	
	        // Start downloading json data from Google Directions API
	        downloadTask.execute(url);
		}
		
	}
	
	private void drawRouteFromTo(NamedLocation currentNamedLocation,
			NamedLocation destination) {
		LatLng origin = new LatLng(currentNamedLocation.getLatitude(), currentNamedLocation.getLongitude());
		
		
		LatLng dest = new LatLng(destination.getLatitude(), destination.getLongitude());
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
		System.out.println("locManager enabled: "+locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
		if (currentNamedLocation != null){
			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}else{
			return false;
		}
		 
	}

	private ArrayList<NamedLocation> getFavMensaNamedLocations() {
		ArrayList<Mensa> favMensas = getFavouriteMensas(mensaList);
		ArrayList<NamedLocation> result = new ArrayList<NamedLocation>();
		for (Mensa m : favMensas){
			for (NamedLocation nL : mensaLocations){
				if (nL.getName() == m.getName()){
					result.add(nL);
				}
			}
		}
		return result;
	}

	private ArrayList<Mensa> getFavouriteMensas(List<Mensa> mensaList2) {
		assert favouriteMensaSelected(mensaList2) == true;
		ArrayList<Mensa> result = new ArrayList<Mensa>();
		for (Mensa m : mensaList) {
			if (m.isFavorite()){
				result.add(m);
			}
		}
		return result;
	}

	private boolean favouriteMensaSelected(List<Mensa> mensaList2) {
		boolean result = false;
		for (Mensa m : mensaList) {
			if (m.isFavorite()){
				result = true;
			}
		}
		return result;
	}

	private void addListenerOnSpinnerItemSelection(Spinner spinFocus) {
		spinFocus.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println(" onItemSelected position: "+position);
				 NamedLocation namedLoc = (NamedLocation) parent.getItemAtPosition(position);
				 System.out.println("onItemSelected: "+namedLoc);
				 
					 LatLng selectedLocation = new LatLng(namedLoc.getLatitude(), namedLoc.getLongitude());
					 map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, DETAIL_ZOOM));
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
			map.addMarker(new MarkerOptions().position(mensaLocation).title(m.getName()));
		}
	}



	private Mensa getClosestMensa(Location location) throws Exception {
		Mensa result = null;
		float smallestDist=99999999;
		for (Mensa m : mensaList){
			Location mensaLocation = new Location("");
			mensaLocation.setLatitude(m.getLatitude());
			mensaLocation.setLongitude(m.getLongitude());
			float currentDist= location.distanceTo(mensaLocation);
			if (smallestDist>currentDist){
				smallestDist=currentDist;
				result=m;
			}
		}
		if (smallestDist==99999999){
			throw new Exception();
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
//            Toast.makeText(getActivity(), "Location changed", Toast.LENGTH_SHORT).show();
            	System.out.println("Draw on Map - onLocationChanged");
            drawOnMap();
          }


			@Override
			public void onProviderDisabled(String arg0) {
				Toast.makeText(getActivity(), "ProviderDisabled", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onProviderEnabled(String arg0) {
				Toast.makeText(getActivity(), "onProviderEnabled", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//				Toast.makeText(getActivity(), "onStatusChanged", Toast.LENGTH_SHORT).show();
//				drawCurrentLocation(getCurrentNamedLocation());
//	            drawMensas();
			}};
          
          locationManager.requestLocationUpdates(provider, 200000, 100, locationListener);
		return location;
	}
	

//	---------------------------------------------------------------------------------------------------------------------------------
//	---------------------------------------------------------------------------------------------------------------------------------
//	---------------------------------------------------------------------------------------------------------------------------------
//	---------------------------------------------------------------------------------------------------------------------------------
	
	
	private String getDirectionsUrl(LatLng origin,LatLng dest, String transportMode){
		 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
        
        String mode = "mode="+transportMode;

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
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
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
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
	
	
	
	
	
	
	
	
	
	
	
	
	

//	---------------------------------------------------------------------------------------------------------------------------------
//	---------------------------------------------------------------------------------------------------------------------------------
//	---------------------------------------------------------------------------------------------------------------------------------
//	---------------------------------------------------------------------------------------------------------------------------------
//	---------------------------------------------------------------------------------------------------------------------------------




//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
//		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//		ft.remove(fragment);
//		ft.commit();
//	}
}