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
import com.ese2013.mub.util.NamedLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
	//private static final int INIT_ZOOM = 14;
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
				LatLngBounds bounds = new LatLngBounds(currentPoint, favPoint);
//				map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
				int spinnerPosition = adapter.getPosition(getCurrentNamedLocation());
	    	    spinFocus.setSelection(spinnerPosition);
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
				drawRouteFromTo(currentNamedLocation, favlocations);
			}else{
				drawMensas();
			}
		}else{
			drawMensas();
		}
	}

	

	private void drawMensasWithClosest(NamedLocation currentNamedLocation) {
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
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	private void drawCurrentLocation(NamedLocation currentNamedLocation) {
		map.clear();
		LatLng currentPosition = new LatLng(currentNamedLocation.getLatitude(),
				currentNamedLocation.getLongitude());
		Marker marker = map.addMarker(new MarkerOptions()
		.position(currentPosition)
		.snippet("Lat:" + currentNamedLocation.getLatitude() + "Lng:"+ currentNamedLocation.getLongitude())
		.title("Your Location")
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	private void drawRouteFromTo(NamedLocation currentNamedLocation,
			ArrayList<NamedLocation> favlocations) {
		// TODO Auto-generated method stub
		
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
			Marker marker = map.addMarker(new MarkerOptions().position(mensaLocation).title(m.getName()));
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




	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}
}