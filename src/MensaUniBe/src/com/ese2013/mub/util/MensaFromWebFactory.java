package com.ese2013.mub.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.MensaDataSource;

public class MensaFromWebFactory extends AbstractMensaFactory {

	private MensaDataSource dataSource = MensaDataSource.getInstance();
	private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
	private JSONArray updateStatusJson;

	public MensaFromWebFactory(JSONArray updateStatusJson) {
		this.updateStatusJson = updateStatusJson;
	}

	@Override
	public List<Mensa> createMensaList() throws MensaDownloadException {
		try {
			dataSource.open();
			JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_MENSAS);
			JSONObject result = request.execute();
			JSONArray content = result.getJSONObject("result").getJSONArray("content");
			List<Mensa> mensas = new ArrayList<Mensa>();
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				JSONObject mensaJsonUpdate = updateStatusJson.getJSONObject(i);
				Mensa mensa = parseMensaJson(mensaJsonObject, mensaJsonUpdate);
				mensa.setMenuplan(createWeeklyMenuplan(mensa));
				mensas.add(mensa);
			}
			return mensas;
		} catch (JSONException e) {
			throw new MensaDownloadException(e);
		} catch (IOException e) {
			throw new MensaDownloadException(e);
		} catch (ParseException e) {
			throw new MensaDownloadException(e);
		} finally {
			dataSource.close();
		}
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
		builder.setIsFavorite(dataSource.isInFavorites(mensaId));
		builder.setTimestamp(mensaJsonUpdate.getInt("timestamp"));
		return builder.build();
	}

	private WeeklyMenuplan createWeeklyMenuplan(Mensa m) throws IOException, JSONException, ParseException {
		JsonDataRequest menuRequest = new JsonDataRequest(ServiceUri.GET_WEEKLY_MENUPLAN.replaceFirst(":id", "" + m.getId()));
		JSONArray menus = menuRequest.execute().getJSONObject("result").getJSONObject("content").getJSONArray("menus");
		return parseWeeklyMenuplan(menus);
	}

	private WeeklyMenuplan parseWeeklyMenuplan(JSONArray menus) throws JSONException, ParseException {
		WeeklyMenuplan plan = new WeeklyMenuplan();
		for (int i = 0; i < menus.length(); i++) {
			JSONObject menu = menus.getJSONObject(i);
			plan.add(parseMenu(menu));
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
		builder.setDate(new Day(fm.parse(menu.getString("date"))));
//		builder.setHash(menu.getInt("hash"));
		return builder.build();
	}
}