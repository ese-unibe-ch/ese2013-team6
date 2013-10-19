package com.ese2013.mub.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MensaFactory {

	public ArrayList<Mensa> createMensaList() {
		try {
			MenuFactory menuFac = new MenuFactory();
			JSONArray content = DataManager.getSingleton().loadJsonArray("mensaList");
			ArrayList<Mensa> mensas = new ArrayList<Mensa>();
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
				Mensa mensa = builder.parseJSON(mensaJsonObject).build();
				mensas.add(mensa);
				mensa.setMenuplan(menuFac.createMenuplans(mensa));
			}
			return mensas;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
