package com.ese2013.mub.model;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuFactory {

	public WeeklyMenuplan createMenuplans(Mensa mensa) {
		JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_WEEKLY_MENUPLAN.replaceFirst(":id", "" + mensa.getId()));
		request.execute();
		JSONObject json;
		try {
			json = request.get();
			JSONArray menus = json.getJSONObject("result").getJSONObject("content").getJSONArray("menus");
			return new WeeklyMenuplan(menus);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
