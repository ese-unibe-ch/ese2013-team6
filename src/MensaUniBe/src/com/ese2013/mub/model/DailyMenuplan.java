package com.ese2013.mub.model;

import java.util.ArrayList;

public class DailyMenuplan {
	private ArrayList<Menu> menus = new ArrayList<Menu>();

	public void add(Menu menu) {
		menus.add(menu);
	}

	public ArrayList<Menu> getMenus() {
		return menus;
	}

	public String getDateString() {
		return menus.get(0).getDateString();
	}
}