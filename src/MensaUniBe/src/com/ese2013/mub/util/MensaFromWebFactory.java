package com.ese2013.mub.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;

public class MensaFromWebFactory extends AbstractMensaFactory {

	private DataManager dataManager = DataManager.getSingleton();
	private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
	private JSONArray updateStatusJson;

	public MensaFromWebFactory(JSONArray updateStatusJson) {
		this.updateStatusJson = updateStatusJson;
	}

	@Override
	public List<Mensa> createMensaList() throws MensaDownloadException {
		try {
			JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_MENSAS);
			JSONObject result = request.execute();
			if (result.getJSONObject("result").getInt("code") == JsonDataRequest.CODE_SUCCESS) {
				JSONArray content = result.getJSONObject("result").getJSONArray("content");
				List<Mensa> mensas = new ArrayList<Mensa>();
				for (int i = 0; i < content.length(); i++) {
					JSONObject mensaJsonObject = content.getJSONObject(i);
					JSONObject mensaJsonUpdate = updateStatusJson.getJSONObject(i);
					Mensa mensa = parseMensaJson(mensaJsonObject, mensaJsonUpdate);
					int mensaId = mensa.getId();
					mensa.setMenuplan(createWeeklyMenuplan(mensa));
					mensa.setIsFavorite(dataManager.isInFavorites(mensaId));
					mensas.add(mensa);
				}
				return mensas;
			}
		} catch (Exception e) {
			throw new MensaDownloadException(e);
		} finally {
			dataManager.closeOpenResources();
		}
		throw new MensaDownloadException("Failed downloading");
	}

	private Mensa parseMensaJson(JSONObject mensaJson, JSONObject mensaJsonUpdate) throws JSONException {
		Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
		int mensaId = mensaJson.getInt("id");
		builder.setId(mensaId);
		builder.setName(mensaJson.getString("mensa"));
		builder.setStreet(mensaJson.getString("street"));
		builder.setZip(mensaJson.getString("plz"));
		builder.setLongitude(mensaJson.getDouble("lon"));
		builder.setLatitude(mensaJson.getDouble("lat"));
		builder.setIsFavorite(DataManager.getSingleton().isInFavorites(mensaId));
		builder.setTimestamp(mensaJsonUpdate.getInt("timestamp"));
		return builder.build();
	}

	private WeeklyMenuplan createWeeklyMenuplan(Mensa m) throws Exception {
		JsonDataRequest menuRequest = new JsonDataRequest(ServiceUri.GET_WEEKLY_MENUPLAN.replaceFirst(":id", "" + m.getId()));
		JSONArray menus = menuRequest.execute().getJSONObject("result").getJSONObject("content").getJSONArray("menus");
		return parseWeeklyMenuplan(menus);
	}

	private WeeklyMenuplan parseWeeklyMenuplan(JSONArray menus) throws JSONException, ParseException {
		WeeklyMenuplan plan = new WeeklyMenuplan();
		for (int i = 0; i < menus.length(); i++) {
			JSONObject menu = menus.getJSONObject(i);
			plan.addMenu(parseMenu(menu));
		}
		return plan;
	}

	private Menu parseMenu(JSONObject menu) throws JSONException, ParseException {
		Menu.MenuBuilder builder = new Menu.MenuBuilder();
		builder.setTitle(menu.getString("title"));
		JSONArray desc = menu.getJSONArray("menu");
		String description = "";
		for (int i = 0; i < desc.length(); i++)
			description += desc.getString(i) + "\n";

		builder.setDescription(description);
		builder.setDate(fm.parse(menu.getString("date")));
		builder.setHash(menu.getInt("hash"));
		return builder.build();
	}
}
