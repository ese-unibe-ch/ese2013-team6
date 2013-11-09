package com.ese2013.mub.util;

import android.location.Location;

import com.ese2013.mub.model.Mensa;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NamedLocation extends Location {

	private String name;
	private float color;

	public NamedLocation(Location loc, String name) {
		super(loc);
		this.name = name;
	}

	public NamedLocation(Mensa mensa) {
		this(calcMensaLocation(mensa), mensa.getName());
	}

	public NamedLocation(Location loc, String name, float color) {
		this(loc, name);
		this.color = color;
	}

	private static Location calcMensaLocation(Mensa mensa) {
		Location loc = new Location("");
		loc.setLatitude(mensa.getLatitude());
		loc.setLongitude(mensa.getLongitude());
		return loc;
	}

	public MarkerOptions getMarker() {
		LatLng loc = new LatLng(getLatitude(), getLongitude());
		return new MarkerOptions().position(loc).title(name)
				.icon(BitmapDescriptorFactory.defaultMarker(color));
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
}
