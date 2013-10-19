package com.ese2013.mub.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Stores all data which make up a menu. Always created using the
 * Menu.MenuBuilder class.
 */
public class Menu {
	private String title, description;
	private Date date;

	/**
	 * Creates a Menu from a given MenuBuilder. Is private to ensure that Menus
	 * are only created by using the build() method from the MenuBuilder class.
	 * 
	 * @param builder
	 *            MenuBuilder containing all information to instantiate a Menu.
	 *            Must not be null;
	 */
	private Menu(MenuBuilder builder) {
		this.title = builder.title;
		this.description = builder.description;
		this.date = builder.date;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Date getDate() {
		return date;
	}

	/**
	 * Converts the date when the menu is served to a string. This string
	 * depends on the Locale settings and should only be used for visual output.
	 * 
	 * @return String containing the date of the menu in a long format (e.g.
	 *         "Monday, 14. October 2013").
	 */
	public String getDateString() {
		return new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.getDefault()).format(date);
	}

	/**
	 * Standard builder class used to construct Menu objects.
	 */
	public static class MenuBuilder {
		private String title, description;
		private Date date;

		public MenuBuilder setTitle(String title) {
			this.title = title;
			return this;
		}

		public MenuBuilder setDescription(String description) {
			this.description = description;
			return this;
		}

		public MenuBuilder setDate(Date date) {
			this.date = date;
			return this;
		}

		public Menu build() {
			return new Menu(this);
		}
	}
}
