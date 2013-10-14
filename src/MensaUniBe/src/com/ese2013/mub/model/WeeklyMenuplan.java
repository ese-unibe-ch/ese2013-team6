package com.ese2013.mub.model;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeeklyMenuplan implements Iterable<DailyMenuplan> {
	private LinkedHashMap<Date, DailyMenuplan> dailymenus;
	
	public WeeklyMenuplan(JSONArray json) throws JSONException {
		parse(json);
	}
	
	private void parse(JSONArray json) throws JSONException {
		dailymenus = new LinkedHashMap<Date, DailyMenuplan>();
		for (int i = 0; i < json.length(); i++) {
			JSONObject menuJson = json.getJSONObject(i);
			Menu menu = new Menu(menuJson);
			Date date = menu.getDate();
			if (dailymenus.containsKey(date)) {
				dailymenus.get(date).add(menu);
			} else {
				DailyMenuplan plan = new DailyMenuplan();
				plan.add(menu);
				dailymenus.put(date, plan);
			}
		}
	}

	@Override
	public Iterator<DailyMenuplan> iterator() {
		return dailymenus.values().iterator();
	}
}
