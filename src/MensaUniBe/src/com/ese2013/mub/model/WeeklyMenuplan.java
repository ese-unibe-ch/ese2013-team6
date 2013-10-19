package com.ese2013.mub.model;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class WeeklyMenuplan implements Iterable<DailyMenuplan> {
	private LinkedHashMap<Date, DailyMenuplan> dailymenus = new LinkedHashMap<Date, DailyMenuplan>();

	public void addMenu(Menu menu) {
		Date date = menu.getDate();
		if (dailymenus.containsKey(date)) {
			dailymenus.get(date).add(menu);
		} else {
			DailyMenuplan plan = new DailyMenuplan();
			plan.add(menu);
			dailymenus.put(date, plan);
		}
	}

	@Override
	public Iterator<DailyMenuplan> iterator() {
		return dailymenus.values().iterator();
	}

	public Set<Date> getDays() {
		return dailymenus.keySet();
	}
}
