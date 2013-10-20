package com.ese2013.mub.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * This class is just a stub to handle local files. Should be improved as soon
 * as possible. Also not quite sure if this should really be singleton, but
 * seams to easiest way...
 * 
 * Also: does not check if data even exist. There should be a way to check that
 * and also return that to the model so that the model knows it needs to
 * download data first.
 */

public class DataManager {
	private static DataManager instance;
	private Activity activity;

	private static final String WEEKLYPLAN_PATH = "WEEKLY_MENUPLAN_", MENSALIST_PATH = "MENSA_LIST",
			MENSA_FAV = "MENSA_FAVORIT_";

	public DataManager(Activity activity) {
		this.activity = activity;
		instance = this;
	}

	public static DataManager getSingleton() {
		// TODO assert not null?
		return instance;
	}
	
	public boolean doesWeeklyMenuplanExist(int i){
		return (loadWeeklyMenuplan(i) != null);		
	}
	
	public boolean doesMensaListExist(JSONArray json){
		return (loadMensaList() != json);
	}

	public JSONObject loadWeeklyMenuplan(int i) {
		return loadJsonObject(WEEKLYPLAN_PATH + i);
	}

	public JSONArray loadMensaList() {
		return loadJsonArray(MENSALIST_PATH);
	}

	public void storeMensaList(JSONArray content) {
		storeJsonArray(content, MENSALIST_PATH);
	}

	public void storeWeeklyMenuplan(JSONObject json, int mensaId) {
		storeJsonObject(json, WEEKLYPLAN_PATH + mensaId);
	}

	private void storeJsonObject(JSONObject json, String path) {
		SharedPreferences.Editor editor = activity.getPreferences(Activity.MODE_PRIVATE).edit();
		editor.putString(path, json.toString());
		editor.commit();
	}

	private JSONObject loadJsonObject(String path) {
		SharedPreferences prefs = activity.getPreferences(Activity.MODE_PRIVATE);
		String restoredText = prefs.getString(path, null);
		try {
			return new JSONObject(restoredText);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void storeJsonArray(JSONArray json, String path) {
		
//		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File(getCacheDir(),"")+path+".json"));
//		out.writeObject( json );
//		out.close();
		
		SharedPreferences.Editor editor = activity.getPreferences(Activity.MODE_PRIVATE).edit();
		editor.putString(path, json.toString());
		editor.commit();
	}

	private JSONArray loadJsonArray(String path) {
		SharedPreferences prefs = activity.getPreferences(Activity.MODE_PRIVATE);
		String restoredText = prefs.getString(path, null);
		try {
			return new JSONArray(restoredText);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isInFavorites(int mensaId) {
		SharedPreferences prefs = activity.getPreferences(Activity.MODE_PRIVATE);
		return prefs.getBoolean(MENSA_FAV + mensaId, false);
	}

}
