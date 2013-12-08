package com.ese2013.mub.model;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a weekly menuplan. Allows to add menus to the plan and to get the
 * Menus of a specific day.
 * 
 */
public class WeeklyMenuplan implements Iterable<DailyMenuplan> {
	private TreeMap<Day, DailyMenuplan> dailymenus = new TreeMap<Day, DailyMenuplan>();

	/**
	 * Adds a menu to the plan.
	 * 
	 * @param menu
	 *            Menu object to be added. Must not be null.
	 * @param day
	 *            Day when the given Menu is served. Caller must assure to only
	 *            add Day objects which are in the same week to a
	 *            WeeklyMenuplan.
	 */
	public void add(Menu menu, Day day) {
		if (dailymenus.containsKey(day)) {
			dailymenus.get(day).add(menu);
		} else {
			DailyMenuplan plan = new DailyMenuplan(day);
			plan.add(menu);
			dailymenus.put(day, plan);
		}
	}

	/**
	 * Returns the DailyMenuplan containing all menus of the given day.
	 * 
	 * @param date
	 *            Day to receive the menus for. Should not be null.
	 * @return DailyMenuplan for the given Day. Null if there are no menus
	 *         stored for this day.
	 */
	public DailyMenuplan getDailymenuplan(Day date) {
		return dailymenus.get(date);
	}

	/**
	 * Returns an iterator to iterate through all DailyMenuplans.
	 */
	@Override
	public Iterator<DailyMenuplan> iterator() {
		return dailymenus.values().iterator();
	}

	/**
	 * Returns all Days which belong to plan.
	 * 
	 * @return Set of Days contained in the plan. (which means days which have
	 *         menus associated to them)
	 */
	public Set<Day> getDays() {
		return dailymenus.keySet();
	}

	/**
	 * Returns the calendar number of the week managed by the plan.
	 * 
	 * @return Int containing the number of the week in the year.
	 */
	public int getWeekNumber() {
		Day day = getDays().iterator().next();
		return day.getWeekNumber();
	}

	/**
	 * Returns the Day when a given Menu is served.
	 * 
	 * @param menu
	 *            Menu to find the Day when it's served. Must not be null.
	 * @return Day object for the day when the menu is served. Null if the Menu
	 *         is never served. If the Menu is served multiplte times on
	 *         different days, the first day of serving is returned. This should
	 *         not happen as the Mensas usually don't have the same Menu on
	 *         different days in one week.
	 */
	public Day getDayOfServing(Menu menu) {
		for (DailyMenuplan dailyPlan : this) {
			if (dailyPlan.contains(menu))
				return dailyPlan.getDay();
		}
		return null;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof WeeklyMenuplan) {
			WeeklyMenuplan otherPlan = (WeeklyMenuplan) other;
			return otherPlan.dailymenus.equals(this.dailymenus);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return dailymenus.hashCode();
	}

	@Override
	public String toString() {
		String result = "";
		for (Day d : getDays()) {
			result += getDailymenuplan(d).toString();
			result += "\n";
		}
		return result;
	}
}
