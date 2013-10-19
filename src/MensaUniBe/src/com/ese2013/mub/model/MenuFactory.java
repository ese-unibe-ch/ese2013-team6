package com.ese2013.mub.model;

import org.json.JSONArray;
import org.json.JSONException;

public class MenuFactory {

	public WeeklyMenuplan createMenuplans(Mensa mensa) {
		try {
			JSONArray menus = DataManager.getSingleton().loadJsonObject("WEEKLY_MENUPLAN_" + mensa.getId()).getJSONObject("result").getJSONObject("content").getJSONArray("menus");
			return new WeeklyMenuplan(menus);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
