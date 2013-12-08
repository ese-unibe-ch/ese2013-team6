package com.ese2013.mub.map.util;

import android.location.Location;

import com.ese2013.mub.model.Mensa;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents a location with a name and (optionally) a Mensa it refers to.
 */
public class NamedLocation extends Location {

	private String name;
	private float color, origColor;
	private Mensa mensa;
	private MarkerOptions marker;

	private NamedLocation(Location loc, String name) {
		super(loc);
		this.name = name;
	}

	public NamedLocation(Mensa mensa) {
		this(calcMensaLocation(mensa), mensa.getName());
		this.mensa = mensa;
	}

	public NamedLocation(Location loc, String name, float color) {
		this(loc, name);
		this.origColor = color;
		this.color = this.origColor;
	}

	private static Location calcMensaLocation(Mensa mensa) {
		Location loc = new Location("");
		loc.setLatitude(mensa.getLatitude());
		loc.setLongitude(mensa.getLongitude());
		return loc;
	}

	/**
	 * Returns MarkerOptions to be added on a map
	 * 
	 * @return MarkerOptions at position, title and color of this NamedLocation.
	 */
	public MarkerOptions getMarkerOptions() {
		LatLng loc = new LatLng(getLatitude(), getLongitude());
		if (marker == null)
			marker = new MarkerOptions();
		return marker.position(loc).title(name).icon(BitmapDescriptorFactory.defaultMarker(color));
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return this.name;
	}

	public void setLocation(Location location) {
		setLatitude(location.getLatitude());
		setLongitude(location.getLongitude());
	}

	public LatLng getLatLng() {
		LatLng result = new LatLng(getLatitude(), getLongitude());
		return result;
	}

	public boolean isLocationOfMensa(int mensaId) {
		return mensa.getId() == mensaId;
	}

	public boolean isLocationOfMensa(Mensa mensa) {
		return isLocationOfMensa(mensa.getId());
	}

	public void setColorSelected() {
		color = BitmapDescriptorFactory.HUE_YELLOW;
	}

	public void resetColor() {
		color = origColor;
	}
}
