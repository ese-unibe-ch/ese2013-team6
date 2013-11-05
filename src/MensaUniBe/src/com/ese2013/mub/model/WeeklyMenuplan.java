package com.ese2013.mub.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Represents a weekly menuplan. Allows to add menus to the plan and to get the
 * Menus of a specific day.
 * 
 */
public class WeeklyMenuplan implements Iterable<DailyMenuplan> {
	private LinkedHashMap<Day, DailyMenuplan> dailymenus = new LinkedHashMap<Day, DailyMenuplan>();

	/**
	 * Adds a menu to the plan.
	 * 
	 * @param menu
	 *            Menu object to be added. Must not be null. Also, the caller
	 *            must assure to only add menus from the same week. If he
	 *            doesn't, the class will be in an undefined state.
	 */
	public void add(Menu menu) {
		Day date = menu.getDate();
		if (dailymenus.containsKey(date)) {
			dailymenus.get(date).add(menu);
		} else {
			DailyMenuplan plan = new DailyMenuplan();
			plan.add(menu);
			dailymenus.put(date, plan);
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
	 * @return Set of Days contained in the plan. (which means days where menus
	 *         are available)
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
		Day d = getDays().iterator().next();
		return d.getWeekNumber();
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
