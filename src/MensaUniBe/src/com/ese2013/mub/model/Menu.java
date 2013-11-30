package com.ese2013.mub.model;

/**
 * Stores all data which make up a menu. Always created using the
 * Menu.MenuBuilder class.
 */
public class Menu {
	private String id, title, description, translatedTitle, translatedDescription;
	private Day date;
	private int userRating = 0;
	private int ratingSum = 0, ratingCount = 0;
	private boolean beenRated = false;

	public Menu(String id, String title, String description, String translTitle, String translDesc, Day day,
			int ratingCount, int ratingSum, int userRating) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.translatedTitle = translTitle;
		this.translatedDescription = translDesc;
		this.date = day;
		this.ratingCount = ratingCount;
		this.ratingSum = ratingSum;
		this.userRating = userRating;
	}

	/**
	 * Creates a Menu from a given MenuBuilder. Is private to ensure that Menus
	 * are only created by using the build() method from the MenuBuilder class.
	 * 
	 * @param builder
	 *            MenuBuilder containing all information to instantiate a Menu.
	 *            Must not be null;
	 */
	private Menu(MenuBuilder builder) {
		this.id = builder.id;
		this.title = builder.title;
		this.description = builder.description;
		this.translatedTitle = builder.translTitle;
		this.translatedDescription = builder.translDesc;
		this.date = builder.date;
		this.ratingCount = builder.ratingCount;
		this.ratingSum = builder.ratingSum;
		this.userRating = builder.userRating;
		this.beenRated = builder.beenRated;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getTranslatedTitle() {
		return translatedTitle;
	}

	public String getTranslatedDescription() {
		return translatedDescription;
	}

	public void setTranslatedTitle(String translatedTitle) {
		this.translatedTitle = translatedTitle;
	}

	public void setTranslatedDescription(String translatedDescription) {
		this.translatedDescription = translatedDescription;
	}

	public Day getDate() {
		return date;
	}

	public int getUserRating() {
		return userRating;
	}

	public void setUserRating(int userRating) {
		beenRated = true;
		this.userRating = userRating;
	}

	public boolean hasBeenRated() {
		return beenRated;
	}

	public float getAvarageRating() {
		if (ratingCount == 0) {
			return 0;
		} else {
			return roundToHalf(ratingSum / ratingCount);
		}
	}

	private static float roundToHalf(float x) {
		return (float) (Math.ceil(x * 2) / 2);
	}

	public int getRatingSum() {
		return ratingSum;
	}

	public int getRatingCount() {
		return ratingCount;
	}

	/**
	 * Converts the date when the menu is served to a string. This string
	 * depends on the Locale settings and should only be used for visual output.
	 * 
	 * @return String containing the date of the menu in a long format (e.g.
	 *         "Monday, 14. October 2013").
	 */
	public String getDateString() {
		return date.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof Menu) {
			Menu otherMenu = (Menu) other;
			if (!otherMenu.getId().equals(this.id))
				return false;
			if (otherMenu.getDate() == null ? this.date != null : !otherMenu.getDate().equals(this.date))
				return false;
			if (!otherMenu.getTitle().equals(this.title))
				return false;
			if (!otherMenu.getDescription().equals(this.description))
				return false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + id.hashCode();
		result = 31 * result + title.hashCode();
		result = 31 * result + description.hashCode();
		result = 31 * result + date.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Menu " + id + " { \n" + "  Title: " + title + "\n  Description: " + description + "\n  Date: "
				+ getDateString() + " \n }";
	}

	/**
	 * Standard builder class used to construct Menu objects.
	 */
	public static class MenuBuilder {
		public int userRating;
		public boolean beenRated = false;
		public int ratingSum;
		public int ratingCount;
		public static final String INVALID_ID = "INVALID";
		private static final String DEFAULT = "N//A";
		private String id = INVALID_ID, title = DEFAULT, description = DEFAULT, translTitle = "", translDesc = "";
		private Day date;

		public MenuBuilder setId(String id) {
			this.id = id;
			return this;
		}

		public MenuBuilder setTitle(String title) {
			this.title = title;
			return this;
		}

		public MenuBuilder setDescription(String description) {
			this.description = description;
			return this;
		}

		public MenuBuilder setTranslatedTitle(String title) {
			this.translTitle = title;
			return this;
		}

		public MenuBuilder setTranslatedDescription(String description) {
			this.translDesc = description;
			return this;
		}

		public MenuBuilder setDate(Day date) {
			this.date = date;
			return this;
		}

		public MenuBuilder setRatingSum(int ratingSum) {
			this.ratingSum = ratingSum;
			return this;
		}

		public MenuBuilder setRatingCount(int ratingCount) {
			this.ratingCount = ratingCount;
			return this;
		}

		public MenuBuilder setBeenRated(boolean beenRated) {
			this.beenRated = beenRated;
			return this;
		}

		public MenuBuilder setUserRating(int userRating) {
			this.userRating = userRating;
			return this;
		}

		public Menu build() {
			return new Menu(this);
		}
	}
}
