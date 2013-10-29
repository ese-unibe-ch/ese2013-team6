package com.ese2013.mub;

import java.util.ArrayList;
import java.util.List;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
	private static final int INIT_ZOOM = 14;
	protected static final float DETAIL_ZOOM = 17;
	private GoogleMap map;
	List<Mensa> mensaList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);
		mensaList = Model.getInstance().getMensas();
		map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if (map == null) 
			return view;
		
		
		Mensa favouriteMensa = drawMensas();
		Location location =getLocation();
		Mensa currentLocation = null;
		ArrayList<Mensa> spinnerList = new ArrayList<Mensa>();
		if(location!=null){
			currentLocation = new Mensa("My Location", location.getLatitude(), location.getLongitude());
			spinnerList.add(currentLocation);
			
		}
		spinnerList.addAll(mensaList);
	
	    ArrayAdapter<Mensa> adapter = new ArrayAdapter<Mensa>(getActivity(),
	            android.R.layout.simple_spinner_dropdown_item, spinnerList);
	    Spinner spinFocus = (Spinner)view.findViewById(R.id.focus_spinner);
	    spinFocus.setAdapter(adapter);
	    addListenerOnSpinnerItemSelection(spinFocus);
	    
	    
		if(location!=null){
            //PLACE THE INITIAL MARKER              
            drawOnMap(location);
            int spinnerPosition = adapter.getPosition(currentLocation);
            Toast.makeText(getActivity(), "spinnerPosition: "+ spinnerPosition, Toast.LENGTH_SHORT).show();
    	    spinFocus.setSelection(spinnerPosition);

         }else{
        	 if (favouriteMensa==null){
        		 int spinnerPosition = adapter.getPosition(mensaList.get(0));
         	    spinFocus.setSelection(spinnerPosition);
        	 }else{
        		int spinnerPosition = adapter.getPosition(favouriteMensa);
          	    spinFocus.setSelection(spinnerPosition);
        	 }
        	
         }
        
		return view;
	}

	private void addListenerOnSpinnerItemSelection(Spinner spinFocus) {
		spinFocus.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				 Mensa mensa = (Mensa) parent.getItemAtPosition(position);
				 LatLng selectedLocation = new LatLng(mensa.getLatitude(), mensa.getLongitude());
        		 map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, DETAIL_ZOOM));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void drawOnMap(Location location) {
		
		if (location!=null){
			drawMarker(location);
			drawMensas(location);
		}else{
			drawMensas();
		}
		
	}


	private Mensa drawMensas() {
		Mensa favouriteMensa = null;
		for (Mensa m : mensaList) {
			LatLng mensaLocation = new LatLng(m.getLatitude(), m.getLongitude());
			

			Marker marker = map.addMarker(new MarkerOptions().position(mensaLocation).title(m.getName()));
			
			if (m.isFavorite()){
				favouriteMensa = m;
			}
		}
		return favouriteMensa;
	}

	private Mensa drawMensas(Location location) {
		Mensa favouriteMensa = null;
		try {
			Mensa closest= getClosestMensa(location);
		
		for (Mensa m : mensaList) {
			LatLng mensaLocation = new LatLng(m.getLatitude(), m.getLongitude());
			Marker marker;
			if (m==closest){
				 marker = map.addMarker(new MarkerOptions()
				.position(mensaLocation)
				.snippet("Lat:" + location.getLatitude() + "Lng:"+ location.getLongitude())
				.title(m.getName())
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//				 drawRouteFromTo(m, closest);
			}else{
				marker = map.addMarker(new MarkerOptions().position(mensaLocation).title(m.getName()));
			}
			if (m.isFavorite()){
				favouriteMensa = m;
			}
		}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return favouriteMensa;
	}
	
//	private void drawRouteFromTo(Mensa current, Mensa closest) {
////		LatLng currentP = new LatLng(current.getLatitude(), current.getLongitude());
////		LatLng closestP = new LatLng(closest.getLatitude(), closest.getLongitude());
//
//		
//		findDirections(current.getLatitude(),
//				current.getLongitude(),
//				closest.getLatitude(), closest.getLongitude(), GMapV2Direction.MODE_DRIVING );
//	}
	
//	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints)
//	{
//	    Polyline newPolyline;
////	    GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap(); 
//	    PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
//
//	    for(int i = 0 ; i < directionPoints.size() ; i++) 
//	    {          
//	        rectLine.add(directionPoints.get(i));
//	    }
//	    newPolyline = map.addPolyline(rectLine);
//	}
//
//
//	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
//	{
//	    Map<String, String> map = new HashMap<String, String>();
//	    map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
//	    map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
//	    map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
//	    map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
//	    map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
//
//	    GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
//	    asyncTask.execute(map); 
//	}

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


	private void drawMarker(Location location){
		map.clear();
		LatLng currentPosition = new LatLng(location.getLatitude(),
		location.getLongitude());
		Marker marker = map.addMarker(new MarkerOptions()
		.position(currentPosition)
		.snippet("Lat:" + location.getLatitude() + "Lng:"+ location.getLongitude())
		.title("Your Location")
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//		marker.showInfoWindow();
		
	}
	
	private Location getLocation() {
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		// Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);
        
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            // redraw the marker when get location update.
            drawOnMap(location);
            
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
				
			}};
          
          locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);
		return location;
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