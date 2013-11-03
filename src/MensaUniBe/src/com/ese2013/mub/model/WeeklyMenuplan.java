package com.ese2013.mub.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.ese2013.mub.util.Day;

public class WeeklyMenuplan implements Iterable<DailyMenuplan> {
	private LinkedHashMap<Day, DailyMenuplan> dailymenus = new LinkedHashMap<Day, DailyMenuplan>();

	public void addMenu(Menu menu) {
		Day date = menu.getDate();
		if (dailymenus.containsKey(date)) {
			dailymenus.get(date).add(menu);
		} else {
			DailyMenuplan plan = new DailyMenuplan();
			plan.add(menu);
			dailymenus.put(date, plan);
		}
	}

	public DailyMenuplan getDailymenuplan(Day date) {
		return dailymenus.get(date);
	}

	@Override
	public Iterator<DailyMenuplan> iterator() {
		return dailymenus.values().iterator();
	}

	public Set<Day> getDays() {
		return dailymenus.keySet();
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
