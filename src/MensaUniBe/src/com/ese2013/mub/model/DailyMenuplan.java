package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.Locale;

public class DailyMenuplan {
	private ArrayList<Menu> menus = new ArrayList<Menu>();

	public void add(Menu menu) {
		menus.add(menu);
	}

	//TODO could be iterable like weekly menuplan
	public ArrayList<Menu> getMenus() {
		return menus;
	}

	//TODO Needs to be more beautiful
	public String getDateString() {
		return new java.text.SimpleDateFormat("EEEE, dd. MMMM yyyy",
				Locale.getDefault()).format(menus.get(0).getDate());
	}
}
