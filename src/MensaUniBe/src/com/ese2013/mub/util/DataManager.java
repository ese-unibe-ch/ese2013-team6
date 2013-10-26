package com.ese2013.mub.util;

import java.text.ParseException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.MensaDataSource;

public class DataManager {
	private MensaDataSource datasource;
	private static DataManager instance;
	
	public DataManager(Activity activity) {
		datasource = new MensaDataSource(activity);
		instance = this;
	}

	public static DataManager getSingleton() {
		return instance;
	}

	public void storeMensaList(JSONArray mensaListJson, JSONArray updateStatusJson) throws JSONException {
		datasource.open();
		datasource.storeMensaList(mensaListJson, updateStatusJson);
		datasource.close();
	}

	public void storeWeeklyMenuplan(JSONObject json, int mensaId) throws JSONException, ParseException {
		datasource.open();
		datasource.storeMenuplan(json.getJSONObject("result").getJSONObject("content").getJSONArray("menus"), mensaId);
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
		WeeklyMenuplan p = datasource.loadMenuplan(mensaId);
		datasource.close();
		return p;
	}

	public boolean isInFavorites(int mensaId) {
		datasource.open();
		boolean isFavorite = datasource.isInFavorites(mensaId);
		datasource.close();
		return isFavorite;
	}

	public void storeFavorites(List<Mensa> mensas) {
		datasource.open();
		datasource.storeFavorites(mensas);
		datasource.close();
	}

	public void closeOpenResources() {
		datasource.close();
	}
	
	public int getMensaTimestamp(int mensaId) {
		datasource.open();
		int timestamp = datasource.getMensaTimestamp(mensaId);
		datasource.close();
		return timestamp;
	}
}