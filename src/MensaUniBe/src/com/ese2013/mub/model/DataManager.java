package com.ese2013.mub.model;

import java.util.ArrayList;

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
		
//		try {
//			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(new File(activity.getCacheDir(),"")+"cacheFile.srl")));
//			JSONObject jsonObject = (JSONObject) in.readObject();
//			in.close();
//			return jsonObject;
//		} catch (StreamCorruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OptionalDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
		
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
		
//		try {
//			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File(activity.getCacheDir(),"")+path+".json"));
//			out.writeObject( json );
//			out.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		SharedPreferences.Editor editor = activity.getPreferences(Activity.MODE_PRIVATE).edit();
		editor.putString(path, json.toString());
		editor.commit();
	}

	private JSONArray loadJsonArray(String path) {
		
//		try {
//			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(new File(activity.getCacheDir(),"")+path+".json")));
//			JSONArray jsonArray = (JSONArray) in.readObject();
//			in.close();
//			return jsonArray;
//		} catch (StreamCorruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (OptionalDataException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		return null;
		
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

	public void storeFavorites(ArrayList<Mensa> mensas) {
		for (Mensa m : mensas) {
			SharedPreferences.Editor prefs = activity.getPreferences(Activity.MODE_PRIVATE).edit();
			prefs.putBoolean(MENSA_FAV + m.getId(), m.isFavorite());
			prefs.commit();
		}
	}

}
