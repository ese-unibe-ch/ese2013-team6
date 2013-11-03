package com.ese2013.mub.model;

/**
 * Represents a mensa by holding information about name, address and
 * geographical location. Also provides a unique Id for the mensa.
 * 
 */
public class Mensa {
	private String name, street, zip;
	private double longitude, latitude;
	private int id, timestamp;
	private boolean isFavorite;
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
		this.isFavorite = builder.isFavorite;
		this.timestamp = builder.timestamp;
	}

	public Mensa(String name, double latitude, double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
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

	public boolean isFavorite() {
		return isFavorite;
	}

	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets if a mensa is favorite or not. This is the only property of a mensa
	 * which can change at runtime.
	 * 
	 * @param isFavorite
	 *            true if the mensa should be a favorite mensa, false otherwise
	 */
	public void setIsFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
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

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (other instanceof Mensa) {
			Mensa otherMensa = (Mensa) other;
			if (otherMensa.getId() != this.id)
				return false;
			if (otherMensa.getLatitude() != this.latitude)
				return false;
			if (otherMensa.getLongitude() != this.longitude)
				return false;
			if (!otherMensa.getName().equals(this.name))
				return false;
			if (!otherMensa.getStreet().equals(this.street))
				return false;
			if (!otherMensa.getZip().equals(this.zip))
				return false;
			if (otherMensa.getTimestamp() != this.timestamp)
				return false;
			if (otherMensa.isFavorite() != this.isFavorite)
				return false;
			if (otherMensa.getMenuplan() == null ? this.menuplan != null : !otherMensa.getMenuplan().equals(this.menuplan))
				return false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + id;
		result = 31 * result + name.hashCode();
		result = 31 * result + street.hashCode();
		result = 31 * result + zip.hashCode();
		result = 31 * result + timestamp;
		result = 31 * result + (isFavorite ? 1 : 0);
		result = 31 * result + (menuplan == null ? 0 : menuplan.hashCode());
		long latLong = Double.doubleToLongBits(latitude);
		result = 31 * result + (int) (latLong ^ (latLong >>> 32));
		long lonLong = Double.doubleToLongBits(longitude);
		result = 31 * result + (int) (lonLong ^ (lonLong >>> 32));
		return result;
	}

	/**
	 * Standard builder class used to construct Mensa objects. All fields have
	 * default values, this means a MensaBuilder can always successfully create
	 * a Mensa object.
	 */
	public static class MensaBuilder {
		public boolean isFavorite = false;
		private static final String DEFAULT = "N//A";
		private String name = DEFAULT, street = DEFAULT, zip = DEFAULT;
		private double longitude, latitude;
		private int id, timestamp;

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

		public MensaBuilder setTimestamp(int timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public MensaBuilder setIsFavorite(boolean isFavorite) {
			this.isFavorite = isFavorite;
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