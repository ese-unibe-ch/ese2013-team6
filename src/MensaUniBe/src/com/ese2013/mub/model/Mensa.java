package com.ese2013.mub.model;

/**
 * Represents a mensa by holding information about name, address and
 * geographical location. Also provides a unique Id for the mensa.
 * 
 */
public class Mensa {
	private String name;
	private String street;
	private String zip;
	private double longitude, latitude;
	private int id;
	private WeeklyMenuplan menuplan;

	/**
	 * Creates a Mensa object given a Mensa.MensaBuilder. Is private to ensure
	 * that it is only called via the MensaBuilder.
	 * 
	 * @param builder
	 *            MensaBuilder containing all information to instantiate a
	 *            Mensa. Must not be null;
	 */
	private Mensa(MensaBuilder builder) {
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

	/**
	 * Sets the current WeeklyMenuplan for a mensa.
	 * 
	 * @param menuplan
	 *            WeeklyMenuplan to be set. Must not be null.
	 */
	public void setMenuplan(WeeklyMenuplan menuplan) {
		this.menuplan = menuplan;
	}

	public WeeklyMenuplan getMenuplan() {
		return menuplan;
	}

	/**
	 * Standard builder class used to construct Mensa objects. All fields have
	 * default values, this means a MensaBuilder can always successfully create
	 * a Mensa object.
	 */
	public static class MensaBuilder {
		private static final String DEFAULT = "N//A";
		private String name = DEFAULT, street = DEFAULT, zip = DEFAULT;
		private double longitude, latitude;
		private int id;

		public MensaBuilder setId(int id) {
			this.id = id;
			return this;
		}

		public MensaBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public MensaBuilder setStreet(String street) {
			this.street = street;
			return this;
		}

		public MensaBuilder setZip(String zip) {
			this.zip = zip;
			return this;
		}

		public MensaBuilder setLongitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		public MensaBuilder setLatitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		/**
		 * Creates the actual Mensa object from the builder.
		 * 
		 * @return The Mensa which has been created.
		 */
		public Mensa build() {
			return new Mensa(this);
		}
	}
}