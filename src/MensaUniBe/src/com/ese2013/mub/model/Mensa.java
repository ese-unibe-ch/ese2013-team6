package com.ese2013.mub.model;

/**
 * Represents a Mensa by holding information about name, address and
 * geographical location and the served menus and if the Mensa is a favorite.
 * Also provides a unique Id for the Mensa.
 */
public class Mensa implements Comparable<Mensa> {
	private String name, street, zip;
	private double longitude, latitude;
	private int id;
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

	/**
	 * Sets if a Mensa is favorite or not.
	 * 
	 * @param isFavorite
	 *            true if the Mensa should be a favorite Mensa, false otherwise.
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
	public int compareTo(Mensa other) {
		return this.getId() - other.getId();
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
		final int multiplier = 31;
		result = multiplier * result + id;
		result = multiplier * result + name.hashCode();
		result = multiplier * result + street.hashCode();
		result = multiplier * result + zip.hashCode();
		result = multiplier * result + (isFavorite ? 1 : 0);
		result = multiplier * result + (menuplan == null ? 0 : menuplan.hashCode());
		long latLong = Double.doubleToLongBits(latitude);
		result = multiplier * result + (int) (latLong ^ (latLong >>> 32));
		long lonLong = Double.doubleToLongBits(longitude);
		result = multiplier * result + (int) (lonLong ^ (lonLong >>> 32));
		return result;
	}

	/**
	 * Standard builder class used to construct Mensa objects. All fields have
	 * default values, this means a MensaBuilder can always "successfully" create
	 * a Mensa object.
	 */
	public static class MensaBuilder {
		private boolean isFavorite = false;
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