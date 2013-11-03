package com.ese2013.mub.util;

import com.ese2013.mub.model.Mensa;

import android.location.Location;

public class NamedLocation extends Location {
	
	private String name;
	
	public NamedLocation(Location loc, String name){
		super(loc);
		this.setName(name);
	}
	
	public NamedLocation(Mensa mensa){
		super(calcMensaLocation(mensa));
		this.name = mensa.getName();
	}

	public NamedLocation(String string, double latitude, double longitude) {
		super(calcLocation(latitude, longitude));
		this.setName(string);
	}

	private static Location calcLocation(double latitude, double longitude) {
		Location loc = new Location("");
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);
		return loc;
	}
	
	private static Location calcMensaLocation(Mensa mensa) {
		assert mensa != null;
		System.out.println("Mensa in Calc: "+ mensa.getName());
		Location loc = new Location("");
		loc.setLatitude(mensa.getLatitude());
		loc.setLongitude(mensa.getLongitude());
		return loc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
}
