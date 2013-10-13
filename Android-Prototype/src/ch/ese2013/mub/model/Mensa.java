package ch.ese2013.mub.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Mensa {

	private static final String MENSA_NAME_KEY = "mensa";
	private static final String MENSA_ID_KEY = "id";
	private static final String MENSA_STREET_KEY = "street";
	private static final String MENSA_PLZ_KEY = "plz";

	private JSONObject jsonObject;

	public Mensa(JSONObject mensaJsonObject) {
		this.jsonObject = mensaJsonObject;
	}

	public String getName() {
		try {
			return this.jsonObject.getString(MENSA_NAME_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getId() {
		try {
			return this.jsonObject.getString(MENSA_ID_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getStreet() {
		try {
			return this.jsonObject.getString(MENSA_STREET_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getPlz() {
		try {
			return this.jsonObject.getString(MENSA_PLZ_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String toString(){
		return this.getName();
	}

}
