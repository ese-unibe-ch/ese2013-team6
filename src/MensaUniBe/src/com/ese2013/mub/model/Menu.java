package com.ese2013.mub.model;

/**
 * Stores all data which make up a menu. Always created using the
 * Menu.MenuBuilder class.
 */
public class Menu {
	private String id, origTitle, origDescription, translatedTitle, translatedDescription;
	private int ratingSum, ratingCount;

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
		this.origTitle = builder.title;
		this.origDescription = builder.description;
		this.translatedTitle = builder.translTitle;
		this.translatedDescription = builder.translDesc;
		this.ratingCount = builder.ratingCount;
		this.ratingSum = builder.ratingSum;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return getOrigTitle();
	}

	public String getDescription() {
		return getOrigDescription();
	}

	public String getOrigTitle() {
		return origTitle;
	}

	public String getOrigDescription() {
		return origDescription;
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

	public void setUserRating(int userRating) {
		ratingSum += userRating;
		ratingCount++;
	}

	public float getAverageRating() {
		if (ratingCount == 0)
			return 0;
		else
			return roundToHalf(ratingSum / ratingCount);
	}

	private static float roundToHalf(float x) {
		return (float) (Math.ceil(x * 2) / 2);
	}

	public void setRatingSum(int ratingSum) {
		this.ratingSum = ratingSum;
	}

	public void setRatingCount(int ratingCount) {
		this.ratingCount = ratingCount;
	}

	public int getRatingSum() {
		return ratingSum;
	}

	public int getRatingCount() {
		return ratingCount;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof Menu) {
			Menu otherMenu = (Menu) other;
			if (!otherMenu.getId().equals(this.id))
				return false;
			if (!otherMenu.getOrigTitle().equals(this.origTitle))
				return false;
			if (!otherMenu.getOrigDescription().equals(this.origDescription))
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
		result = multiplier * result + id.hashCode();
		result = multiplier * result + origTitle.hashCode();
		result = multiplier * result + origDescription.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Menu " + id + " { \n" + "  Title: " + origTitle + "\n  Description: " + origDescription + "\n }";
	}

	/**
	 * Builder class used to construct Menu objects. Has default values for all
	 * attributes.
	 */
	public static class MenuBuilder {
		private int ratingSum, ratingCount;
		private static final String INVALID_ID = "INVALID", DEFAULT = "N//A";
		private String id = INVALID_ID, title = DEFAULT, description = DEFAULT, translTitle = "", translDesc = "";

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

		public MenuBuilder setRatingSum(int ratingSum) {
			this.ratingSum = ratingSum;
			return this;
		}

		/**
		 * Sets the number of ratings this menu got.
		 * 
		 * @param ratingCount
		 *            int for the number of ratings, must be bigger than 0.
		 * @return This MenuBuilder to allow chaining of setXXX calls.
		 */
		public MenuBuilder setRatingCount(int ratingCount) {
			this.ratingCount = ratingCount;
			return this;
		}

		/**
		 * Creates a Menu object from the data of this builder. Caller needs to
		 * make sure that the builder has a valid Id set by using
		 * "setId(String)"
		 * 
		 * @return Menu object constructed from this MenuBuilder.
		 */
		public Menu build() {
			return new Menu(this);
		}
	}
}
