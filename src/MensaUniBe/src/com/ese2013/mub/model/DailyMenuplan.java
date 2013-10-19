package com.ese2013.mub.model;

import java.util.ArrayList;

/**
 * Manages a list of menus served at a specific day (in one mensa). There must
 * be at least one menu in a DailyMenuplan (except while creation).
 */
public class DailyMenuplan {
	private ArrayList<Menu> menus = new ArrayList<Menu>();

	/**
	 * Adds a menu to this menu plan.
	 * 
	 * @param menu
	 *            Menu to be added. Must not be null.
	 */
	public void add(Menu menu) {
		menus.add(menu);
	}

	/**
	 * Returns the list of menus.
	 * 
	 * @return ArrayList containing the menus.
	 */
	public ArrayList<Menu> getMenus() {
		return menus;
	}

	/**
	 * Returns the date of the menus as strings. Uses the invariant that all
	 * menus have the same date.
	 * 
	 * @return String containing the date as a String. This String should only
	 *         be used for visual output, as it depends on the Locale settings.
	 */
	public String getDateString() {
		return menus.get(0).getDateString();
	}
}