package com.ese2013.mub.model;

import java.text.ParseException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;

public class DataManager {
	private MensaDataSource datasource;
	private Activity activity;
	private static DataManager instance;
	private static final String MENSA_FAV = "MENSA_FAVORIT_";

	public DataManager(Activity activity) {
		this.activity = activity;
		datasource = new MensaDataSource(activity);
		instance = this;
	}

	public static DataManager getSingleton() {
		// TODO assert not null?
		return instance;
	}

	public void storeMensaList(JSONArray content) {
		datasource.open();
		try {
			datasource.storeMensaList(content);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		datasource.close();

	}

	public void storeWeeklyMenuplan(JSONObject json, int mensaId) {
		datasource.open();
		try {
			datasource.storeMenuplan(json.getJSONObject("result").getJSONObject("content").getJSONArray("menus"), mensaId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		datasource.close();
	}

	public List<Mensa> loadMensaList() {
		datasource.open();
		List<Mensa> mensas = datasource.loadMensaList();
		datasource.close();
		return mensas;
	}

	public WeeklyMenuplan loadWeeklyMenuplan(int mensaId) {
		datasource.open();
		WeeklyMenuplan p = null;
		try {
			p = datasource.loadMenuplan(mensaId);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		datasource.close();
		return p;
	}

	public boolean isInFavorites(int mensaId) {
		SharedPreferences prefs = activity.getPreferences(Activity.MODE_PRIVATE);
		return prefs.getBoolean(MENSA_FAV + mensaId, false);
	}

	public void storeFavorites(List<Mensa> mensas) {
		for (Mensa m : mensas) {
			SharedPreferences.Editor prefs = activity.getPreferences(Activity.MODE_PRIVATE).edit();
			prefs.putBoolean(MENSA_FAV + m.getId(), m.isFavorite());
			prefs.commit();
		}
	}
}