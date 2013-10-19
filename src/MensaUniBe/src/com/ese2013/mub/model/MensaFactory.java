package com.ese2013.mub.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MensaFactory {

	/**
	 * Creates all Mensas which are stored in the local data on the phone. Also
	 * retrieves all menus for the created mensas using the MenuFactory.
	 * 
	 * @return ArrayList of Mensas. Is null if the mensas couldn't be retrieved
	 *         (happens only if either the data retrieved from the web service
	 *         is garbage or the DataManager has a bug).
	 */
	public ArrayList<Mensa> createMensaList() {
		try {
			MenuFactory menuFac = new MenuFactory();
			JSONArray content = DataManager.getSingleton().loadMensaList();
			ArrayList<Mensa> mensas = new ArrayList<Mensa>();
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				Mensa mensa = parseJson(mensaJsonObject);
				mensas.add(mensa);
				mensa.setMenuplan(menuFac.createMenuplans(mensa));
			}
			return mensas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Mensa parseJson(JSONObject json) throws JSONException {
		Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
		builder.setId(json.getInt("id")).setName(json.getString("mensa")).setStreet(json.getString("street"))
				.setZip(json.getString("plz")).setLongitude(json.getDouble("lon")).setLatitude(json.getDouble("lat"));
		return builder.build();
	}
}