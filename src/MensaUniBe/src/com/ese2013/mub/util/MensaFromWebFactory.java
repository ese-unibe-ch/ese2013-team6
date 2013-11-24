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
import com.ese2013.mub.model.Mensa.MensaBuilder;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.MensaDataSource;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MensaFromWebFactory extends AbstractMensaFactory {

	private MensaDataSource dataSource = MensaDataSource.getInstance();
	private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
	private JSONArray updateStatusJson;
	private MenuManager menuManager;

	public MensaFromWebFactory(JSONArray updateStatusJson, MenuManager menuManager) {
		this.updateStatusJson = updateStatusJson;
		this.menuManager = menuManager;
	}

	@Override
	public List<Mensa> createMensaList() throws MensaDownloadException {
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Mensa");
			List<ParseObject> parseMensas = query.find();
			System.out.println(parseMensas.size());
			dataSource.open();
			List<Mensa> mensas = new ArrayList<Mensa>();
			for (int i = 0; i < parseMensas.size(); i++) {
				ParseObject parseMensa = parseMensas.get(i);
				JSONObject mensaJsonUpdate = updateStatusJson.getJSONObject(i);
				Mensa mensa = createMensa(parseMensa, mensaJsonUpdate);
				System.out.println(mensa.getId());
				System.out.println(mensa.getName());
//				Mensa mensa = parseMensaJson(mensaJsonObject, mensaJsonUpdate);
				mensa.setMenuplan(createWeeklyMenuplan(mensa));
				mensas.add(mensa);
			}
			
			
			
//			dataSource.open();
//			MensaWebserviceJsonRequest request = new MensaWebserviceJsonRequest(ServiceUri.GET_MENSAS);
//			JSONObject result = request.execute();
//			JSONArray content = result.getJSONObject("result").getJSONArray("content");
//			List<Mensa> mensas = new ArrayList<Mensa>();
//			for (int i = 0; i < content.length(); i++) {
//				JSONObject mensaJsonObject = content.getJSONObject(i);
//				JSONObject mensaJsonUpdate = updateStatusJson.getJSONObject(i);
//				Mensa mensa = parseMensaJson(mensaJsonObject, mensaJsonUpdate);
//				mensa.setMenuplan(createWeeklyMenuplan(mensa));
//				mensas.add(mensa);
//			}
			return mensas;
		} catch (JSONException e) {
			e.printStackTrace();
			throw new MensaDownloadException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new MensaDownloadException(e);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new MensaDownloadException(e);
		} catch (com.parse.ParseException e) {
			e.printStackTrace();
			throw new MensaDownloadException(e);
		} finally {
			dataSource.close();
		}
	}
	
	private Mensa createMensa(ParseObject parseMensa, JSONObject updateStatus) throws JSONException {
		MensaBuilder builder = new MensaBuilder();
		int mensaId = Integer.parseInt(parseMensa.getString("mensaId"));
		builder.setId(mensaId);
		builder.setName(parseMensa.getString("name"));
		builder.setStreet(parseMensa.getString("street"));
		builder.setZip(parseMensa.getString("plz"));
		builder.setLongitude(Double.parseDouble(parseMensa.getString("lon")));
		builder.setLatitude(Double.parseDouble(parseMensa.getString("lat")));
		builder.setIsFavorite(dataSource.isInFavorites(mensaId));
		builder.setTimestamp(updateStatus.getInt("timestamp"));
		return builder.build();
	}

	private WeeklyMenuplan createWeeklyMenuplan(Mensa m) throws IOException, JSONException, ParseException {
		MensaWebserviceJsonRequest menuRequest = new MensaWebserviceJsonRequest(ServiceUri.GET_WEEKLY_MENUPLAN.replaceFirst(
				":id", "" + m.getId()));
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
		JSONArray desc = menu.getJSONArray("menu");
		String description = "";
		for (int i = 0; i < desc.length(); i++)
			description += desc.getString(i) + "\n";
		return menuManager.createMenu(menu.getString("title"), description, new Day(fm.parse(menu.getString("date"))));
	}
}