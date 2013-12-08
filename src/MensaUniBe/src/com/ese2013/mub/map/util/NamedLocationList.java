package com.ese2013.mub.map.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.widget.ArrayAdapter;

import com.ese2013.mub.model.Mensa;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Maintains a list of NamedLocation objects. Allows to create this locations
 * from a passed Mensa List and also provides functionality to add Markers to a
 * Google Map and also retrieve a NamedLocation using mensaId, Mensa or Marker
 * from the Google Map.
 * 
 */
public class NamedLocationList {
	private List<NamedLocation> namedLocations;
	private HashMap<Marker, NamedLocation> markerMap = new HashMap<Marker, NamedLocation>();

	public void addMensas(List<Mensa> menas) {
		List<NamedLocation> locations = new ArrayList<NamedLocation>();
		for (Mensa m : menas)
			locations.add(new NamedLocation(m));
		namedLocations = Collections.unmodifiableList(locations);
	}

	public ArrayAdapter<NamedLocation> createAdapter(Context context, int resource) {
		return new ArrayAdapter<NamedLocation>(context, resource, namedLocations);
	}

	public NamedLocation getNamedLocation(int mensaId) {
		for (NamedLocation n : namedLocations)
			if (n.isLocationOfMensa(mensaId))
				return n;
		return null;
	}

	public NamedLocation getNamedLocation(Mensa mensa) {
		return getNamedLocation(mensa.getId());
	}

	public NamedLocation getNamedLocation(Marker marker) {
		return markerMap.get(marker);
	}

	public List<NamedLocation> getList() {
		return namedLocations;
	}

	/**
	 * Adds markers for the NamedLocations to the given map. Is done here to
	 * retain a reference to the added markers to later be able to get a named
	 * location by passing the marker (used on selection of a marker).
	 * 
	 * @param map
	 *            Google Map, must not be null.
	 */
	public void addMarkersToMap(GoogleMap map) {
		markerMap.clear();
		for (NamedLocation n : namedLocations) {
			Marker marker = map.addMarker(n.getMarkerOptions());
			markerMap.put(marker, n);
		}
	}

	/**
	 * Returns the NamedLocation which is closest to the given Location.
	 * 
	 * @param location
	 *            Location from which the closest NamedLocation is searched.
	 * @return NamedLocation which is closest to the given Location.
	 */
	public NamedLocation getClosestMensa(Location location) {
		NamedLocation result = null;
		float smallestDist = Integer.MAX_VALUE;
		for (NamedLocation n : namedLocations) {
			float currentDist = location.distanceTo(n);
			if (smallestDist > currentDist) {
				smallestDist = currentDist;
				result = n;
			}
		}
		return result;
	}
}