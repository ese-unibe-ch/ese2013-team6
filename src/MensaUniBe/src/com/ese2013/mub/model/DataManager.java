package com.ese2013.mub.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * This class is just a stub to handle local files. Should be improved as soon
 * as possible...
 * 
 */

public class DataManager {
	private static DataManager instance;
	private Activity activity;

	public DataManager(Activity activity) {
		this.activity = activity;
		instance = this;
	}

	public static DataManager getSingleton() {
		// TODO assert not null
		return instance;
	}

	public void storeJsonObject(JSONObject json, String path) {
		SharedPreferences.Editor editor = activity.getPreferences(
				Activity.MODE_PRIVATE).edit();
		editor.putString(path, json.toString());
		editor.commit();
	}

	public JSONObject loadJsonObject(String path) {
		SharedPreferences prefs = activity
				.getPreferences(Activity.MODE_PRIVATE);
		String restoredText = prefs.getString(path, null);
		try {
			return new JSONObject(restoredText);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void storeJsonArray(JSONArray content, String path) {
		SharedPreferences.Editor editor = activity.getPreferences(
				Activity.MODE_PRIVATE).edit();
		editor.putString(path, content.toString());
		editor.commit();
	}

	public JSONArray loadJsonArray(String path) {
		SharedPreferences prefs = activity
				.getPreferences(Activity.MODE_PRIVATE);
		String restoredText = prefs.getString(path, null);
		try {
			return new JSONArray(restoredText);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
