package com.ese2013.mub.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Menu {
	private String title, description = "";
	private Date date;
	
	public Menu(JSONObject json) throws JSONException {
		parse(json);
	}

	private void parse(JSONObject json) throws JSONException {
		title = json.getString("title");
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
		try {
			date = fm.parse(json.getString("date"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONArray desc = json.getJSONArray("menu");
		for (int i = 0; i < desc.length(); i++) {
			description += desc.getString(i) + "\n";
		}
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Date getDate() {
		return date;
	}
}
