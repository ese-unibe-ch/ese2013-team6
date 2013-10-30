package com.ese2013.mub.util;

import java.util.List;

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

	public static DataManager getInstance() {
		return instance;
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

	public void deleteLocalMenus() {
		datasource.open();
		datasource.deleteMenus();
		datasource.close();
	}

	public void storeMensaList(List<Mensa> mensas) {
		datasource.open();
		datasource.storeMensaList(mensas);
		datasource.close();
	}

	public void storeWeeklyMenuplan(Mensa m) {
		datasource.open();
		datasource.storeWeeklyMenuplan(m);
		datasource.close();
	}
}