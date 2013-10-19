package com.ese2013.mub.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuFactory {

	/**
	 * Creates the WeeklyMenuplan for a given Mensa by retrieving the plan from
	 * the DataManager class.
	 * 
	 * @return WeeklyMenuplan for the mensa. Is null if the local data source is
	 *         invalid (happens only if either the data retrieved from the web
	 *         service is garbage or the DataManager has a bug).
	 */
	public WeeklyMenuplan createMenuplans(Mensa mensa) {
		try {
			JSONArray menus = DataManager.getSingleton().loadWeeklyMenuplan(mensa.getId()).getJSONObject("result")
					.getJSONObject("content").getJSONArray("menus");
			return parseWeeklyPlan(menus);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private WeeklyMenuplan parseWeeklyPlan(JSONArray json) throws JSONException, ParseException {
		WeeklyMenuplan weeklyplan = new WeeklyMenuplan();
		for (int i = 0; i < json.length(); i++) {
			JSONObject menuJson = json.getJSONObject(i);
			weeklyplan.addMenu(parseJSON(menuJson));
		}
		return weeklyplan;
	}

	private Menu parseJSON(JSONObject json) throws JSONException, ParseException {
		Menu.MenuBuilder builder = new Menu.MenuBuilder();
		builder.setTitle(json.getString("title"));
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
		builder.setDate(fm.parse(json.getString("date")));
		JSONArray desc = json.getJSONArray("menu");
		String description = "";
		for (int i = 0; i < desc.length(); i++) {
			description += desc.getString(i) + "\n";
		}
		builder.setDescription(description);
		return builder.build();
	}
}