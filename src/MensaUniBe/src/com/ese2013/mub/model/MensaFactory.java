package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MensaFactory {

	public ArrayList<Mensa> createMensas() {
		JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_MENSAS);
		request.execute();
		JSONObject json;
		MenuFactory menuFac = new MenuFactory();
		try {
			json = request.get();
			JSONArray content = json.getJSONObject("result").getJSONArray("content");
			ArrayList<Mensa> mensas = new ArrayList<Mensa>();
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
				Mensa mensa = builder.parseJSON(mensaJsonObject).build();
				mensas.add(mensa);
				mensa.setMenuplan(menuFac.createMenuplans(mensa));
			}
			return mensas;
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
