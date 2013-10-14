package com.ese2013.mub.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Mensa {
	private String name;
	private String street;
	private String zip;
	private double longitude, latitude;
	private int id;
	private WeeklyMenuplan menuplan; 

	public Mensa(MensaBuilder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.street = builder.street;
		this.zip = builder.zip;
		this.longitude = builder.longitude;
		this.latitude = builder.latitude;		
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getStreet() {
		return street;
	}
	
	public String getZip() {
		return zip;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setMenuplan(WeeklyMenuplan menuplan) {
		this.menuplan = menuplan;
	}
	
	public WeeklyMenuplan getMenuplan() {
		return menuplan;
	}
	
	
	public static class MensaBuilder {
		private static final String DEFAULT = "N//A";
		private String name = DEFAULT, street = DEFAULT,
				zip = DEFAULT;
		private double longitude, latitude;
		private int id;

		public MensaBuilder() {
		}

		public MensaBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public MensaBuilder setAdress(String adress) {
			this.street = adress;
			return this;
		}
		
		public MensaBuilder setZip(String zip) {
			this.zip = zip;
			return this;
		}
		
		public MensaBuilder setLongitude(double longitude)  {
			this.longitude = longitude;
			return this;
		}
		
		public MensaBuilder setLatitude(double latitude)  {
			this.latitude = latitude;
			return this;
		}

		public MensaBuilder parseJSON(JSONObject json) throws JSONException {
			this.id = json.getInt("id");
			this.name = json.getString("mensa");
			this.street = json.getString("street");
			this.zip = json.getString("plz");
			this.longitude = json.getDouble("lon");
			this.latitude = json.getDouble("lat");
			return this;
		}

		public Mensa build() {
			return new Mensa(this);
		}
	}
}