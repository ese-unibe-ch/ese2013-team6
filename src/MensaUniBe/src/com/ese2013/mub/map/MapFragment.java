package com.ese2013.mub.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.ese2013.mub.R;
import com.ese2013.mub.map.util.DirectionsDownloadTask;
import com.ese2013.mub.map.util.NamedLocation;
import com.ese2013.mub.map.util.NamedLocationList;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

/**
 * Displays a map showing all the Mensas and, if possible, the location of the
 * user. Allows to display the path to a specific Mensa.
 * 
 * At startup the closest Mensa is selected if possible, else a favorite Mensa
 * if one exists. If no closest or favorite Mensa are available, just the first
 * Mensa in the Mensa List is selected. The path to the selected mensa is drawn
 * if possible.
 * When the MENSA_ID_LOCATION argument is given for the fragment the map view
 * will zoom to that mensa.
 * 
 */
public class MapFragment extends Fragment {
	private static final String TRAVEL_MODE_WALKING = "walking", TRAVEL_MODE_BICYCLE = "bicycle",
			TRAVEL_MODE_DRIVING = "driving";
	public static final String MENSA_ID_LOCATION = "mensa.id";
	private GoogleMap map;
	private NamedLocationList namedLocations;
	private ArrayAdapter<NamedLocation> namedLocationsAdapter;
	private NamedLocation currentLocation;
	private NamedLocation selectedLocation;
	private LocationManager locationManager;
	private Spinner locationSpinner;
	private Model model;
	private String travelMode = TRAVEL_MODE_WALKING;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.map);
	}
	/**
	 * Sets up the map view, listeners and default values.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		if (map == null)
			return view;

		model = Model.getInstance();
		setLocationListener();
		setRadioGroupListener(view);

		namedLocations = new NamedLocationList();
		namedLocations.addMensas(model.getMensas());

		namedLocationsAdapter = namedLocations.createAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item);
		locationSpinner = (Spinner) view.findViewById(R.id.focus_spinner);
		locationSpinner.setAdapter(namedLocationsAdapter);
		setSpinnerItemSelectionListener(locationSpinner);

		setMarkerClickListener();

		setupInitValues(view);
		return view;
	}

	/**
	 * Sets up initial zoom and sets spinner to the selected mensa if @this has 
	 * been called with an MENSA_ID_LOCATION argument.
	 */
	private void setupInitValues(View view) {
		Location rawLocation = getLocation();
		if (rawLocation != null)
			updateCurrentLocation(rawLocation);

		Bundle bundle = getArguments();
		zoomOnContent();
		if (bundle == null || bundle.isEmpty()) {
			setSpinnerDefault();
		} else {
			Integer mensaId = (Integer) bundle.get(MENSA_ID_LOCATION);
			if (mensaId != null) {
				selectedLocation = namedLocations.getNamedLocation(mensaId);
				setSpinnerTo(selectedLocation);
			}
		}
	}

	/**
	 * Sets the spinner to his default value. If possible, it is set to the
	 * closest mensa. Else to a favorite Mensa if one exists. If there is no
	 * closest Mensa and no favorite Mensa, just the first Mensa in the Mensa
	 * List is selected.
	 */
	private void setSpinnerDefault() {
		if (model.noMensasLoaded())
			return;

		Mensa selectedMensa;
		if (currentLocationAvailable())
			selectedLocation = namedLocations.getClosestMensa(currentLocation);
		else {
			selectedMensa = model.favoritesExist() ? model.getFavoriteMensas().get(0) : model.getMensas().get(0);
			selectedLocation = namedLocations.getNamedLocation(selectedMensa);
		}
		setSpinnerTo(selectedLocation);
	}
	
	
	/**
	 * Creates a MarkerClickListener to select a mensa location by clicking on it.
	 */
	private void setMarkerClickListener() {
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				NamedLocation newLoc = namedLocations.getNamedLocation(marker);
				if (newLoc == null)
					return true;
				setSpinnerTo(newLoc);
				return true;
			}
		});
	}
	
	/**
	 * Creates listener for clicks on travelmode radio buttons
	 * @param view
	 */
	private void setRadioGroupListener(View view) {
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_modes);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radioButton = (RadioButton) getView().findViewById(checkedId);
				switch (radioButton.getId()) {
				case R.id.rb_bicycling:
					travelMode = TRAVEL_MODE_BICYCLE;
					break;
				case R.id.rb_walking:
					travelMode = TRAVEL_MODE_WALKING;
					break;
				case R.id.rb_driving:
					travelMode = TRAVEL_MODE_DRIVING;
					break;
				}
				repaintMap();
			}
		});
	}

	/**
	 * Creates listerner to @param spinFocus to select Locations from the spinner.
	 * @param spinFocus
	 */
	private void setSpinnerItemSelectionListener(Spinner spinFocus) {
		spinFocus.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				NamedLocation namedLoc = (NamedLocation) parent.getItemAtPosition(position);
				updateSelectedLocation(namedLoc);
				repaintMap();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	/**
	 * Sets up the Location Listener. The listener is used to update the current
	 * location if the location of the phone has been changed.
	 */
	private void setLocationListener() {
		String provider = locationManager.getBestProvider(new Criteria(), true);
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				updateCurrentLocation(location);
				repaintMap();
			}

			@Override
			public void onProviderDisabled(String arg0) {
			}

			@Override
			public void onProviderEnabled(String arg0) {
				updateCurrentLocation(getLocation());
				repaintMap();
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};
		locationManager.requestLocationUpdates(provider, 1000, 15, locationListener);
	}

	/**
	 * Updates the current location.
	 * 
	 * @param location
	 *            the new current Location. Must not be null.
	 */
	private void updateCurrentLocation(Location location) {
		if (currentLocation == null)
			currentLocation = new NamedLocation(location, getString(R.string.map_my_location),
					BitmapDescriptorFactory.HUE_AZURE);
		else
			currentLocation.setLocation(location);
	}

	/**
	 * Sets the spinner to a given NamedLocation
	 * 
	 * @param namedLoc
	 *            NamedLocation for the spinner to be set to. Must be in the
	 *            namedLocationsAdapter.
	 */
	private void setSpinnerTo(NamedLocation namedLoc) {
		updateSelectedLocation(namedLoc);
		locationSpinner.setSelection(namedLocationsAdapter.getPosition(namedLoc));
	}
	
	private void updateSelectedLocation(NamedLocation namedLoc) {
		if (namedLoc == null)
			return;
		if (selectedLocation != null)
			selectedLocation.resetColor();
		selectedLocation = namedLoc;
		selectedLocation.setColorSelected();
	}

	/**
	 * Repaints the map, also updates the displayed path to a Mensa if needed
	 * and the zoom.
	 */
	private void repaintMap() {
		map.clear();
		drawAllLocations();
		if (currentLocationAvailable()) {
			drawRouteFromTo(currentLocation, selectedLocation);
			zoomOnContent();
		}
		updateRadioGroup();
	}

	/**
	 * Sets the zoom to either show all Mensas or, if the path is drawn, to just
	 * show the current location and the target Mensa.
	 */
	private void zoomOnContent() {
		if (currentLocationAvailable() && selectedLocation != null)
			zoomOnContent(currentLocation, selectedLocation);
		else
			zoomOnContent(namedLocations.getList());
	}
	
	/**
	 * Zooms to the two locations @param loc1 and @param loc2
	 * @param loc1 
	 * 			Must be valid location.
	 * @param loc2 	
	 * 			Must be valid location.
	 */
	private void zoomOnContent(NamedLocation loc1, NamedLocation loc2) {
		List<NamedLocation> list = new ArrayList<NamedLocation>();
		list.add(loc1);
		list.add(loc2);
		zoomOnContent(list);
	}

	/**
	 * Zooms defined by bounds including all @param locations and the currentLocation when available
	 * @param locations 
	 * 			All locations which should be zoomed to. Must be valid locations
	 */
	private void zoomOnContent(List<NamedLocation> locations) {
		LatLngBounds.Builder bounds = new LatLngBounds.Builder();
		for (NamedLocation l : locations)
			bounds.include(l.getLatLng());
		if (currentLocation != null)
			bounds.include(currentLocation.getLatLng());
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 400, 400, 15));
	}

	/**
	 * Calculates and draws the path from the given origin to destination using
	 * the callback method onDirectionsDownloadFinished
	 * 
	 * @param origin
	 *            Start NamedLocation. Must not be null.
	 * @param destination
	 *            Goal NamedLocation. Must not be null.
	 */
	private void drawRouteFromTo(NamedLocation origin, NamedLocation destination) {
		DirectionsDownloadTask downloadTask = new DirectionsDownloadTask(origin.getLatLng(), destination.getLatLng(),
				travelMode, this);
		downloadTask.execute();
	}

	/**
	 * Callback from DirectionsDownloadTask.
	 * 
	 * @param downloadTask
	 *            the Task which downloaded the directions.
	 */
	public void onDirectionsDownloadFinished(DirectionsDownloadTask downloadTask) {
		if (downloadTask.wasSuccesful())
			map.addPolyline(downloadTask.getPolyline());
		else
			showDirectionsError();
	}

	private void showDirectionsError() {
		Toast.makeText(getActivity(), getActivity().getString(R.string.map_directions_error), Toast.LENGTH_LONG).show();
	}

	private boolean currentLocationAvailable() {
		return currentLocation != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private void drawAllLocations() {
		namedLocations.addMarkersToMap(map);
		if (currentLocation != null)
			map.addMarker(currentLocation.getMarkerOptions());
	}

	private Location getLocation() {
		return locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
	}
	/**
	 * Disables the selection of travel modes if there is no GPS location found.
	 */
	private void updateRadioGroup() {
		RadioGroup radioGroup = (RadioGroup) getView().findViewById(R.id.rg_modes);
		for (int i = 0; i < radioGroup.getChildCount(); i++)
			((RadioButton) radioGroup.getChildAt(i)).setEnabled(currentLocationAvailable());

	}

	/**
	 * Destroy method which is called by the android framework when the view is
	 * no longer needed. Here we remove the google map fragment which is
	 * embedded in our own map fragment.
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (!getActivity().isChangingConfigurations())
			removeMapFragment();
	}

	private void removeMapFragment() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.map);
		if (fragment != null)
			fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
	}
	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.map);
	}
}