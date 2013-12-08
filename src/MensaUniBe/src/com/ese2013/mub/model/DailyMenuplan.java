package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of menus served at a specific day (in one mensa). There must
 * be at least one menu in a DailyMenuplan (except while creation).
 */
public class DailyMenuplan {
	private ArrayList<Menu> menus = new ArrayList<Menu>();
	private Day day;

	/**
	 * Creates a menu plan for the given day.
	 * 
	 * @param day
	 *            Day for which the menu plan is created. Must not be null.
	 */
	public DailyMenuplan(Day day) {
		this.day = day;
	}

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
	 * @return List containing the menus.
	 */
	public List<Menu> getMenus() {
		return menus;
	}

	/**
	 * Returns if the menu is contained in this DailyMenuplan.
	 * 
	 * @param menu
	 *            the Menu to be checked.
	 * @return true if the menu is contained in the menu plan.
	 */
	public boolean contains(Menu menu) {
		return menus.contains(menu);
	}

	/**
	 * Returns the day of this menu plan.
	 * 
	 * @return Day of the menu plan.
	 */
	public Day getDay() {
		return day;
	}

	/**
	 * Returns the date of the menus as strings.
	 * 
	 * @return String containing the date as a String. This String should only
	 *         be used for visual output, as it depends on the Locale settings.
	 */
	public String getDateString() {
		return day.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof DailyMenuplan) {
			DailyMenuplan otherPlan = (DailyMenuplan) other;
			return otherPlan.getDay().equals(this.day) && otherPlan.getMenus().equals(this.menus);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return menus.hashCode();
	}

	@Override
	public String toString() {
		String result = "Menus for " + getDateString() + "\n";
		for (Menu m : menus) {
			result += m.toString();
			result += "\n";
		}
		return result;
	}
}