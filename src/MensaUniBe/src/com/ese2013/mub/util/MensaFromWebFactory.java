package com.ese2013.mub.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

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
			ParseQuery<ParseObject> query = ParseQuery.getQuery("MenuMensa");
			query.include("menu");
			query.include("mensa");
			List<ParseObject> parseMenuMensas = query.find();

			HashMap<String, Mensa> mensaMap = new HashMap<String, Mensa>();
			dataSource.open();
			for (int i = 0; i < parseMenuMensas.size(); i++) {
				WeeklyMenuplan plan;
				ParseObject parseMenuMensa = parseMenuMensas.get(i);
				ParseObject parseMensa = parseMenuMensa.getParseObject("mensa");
				Mensa mensa = mensaMap.get(parseMensa.getObjectId());
				if (mensa != null) {
					plan = mensa.getMenuplan();
				} else {
					int timestamp = getUpdateTimestamp(parseMensa);
					plan = new WeeklyMenuplan();
					mensa = parseMensa(parseMensa, timestamp);
					mensa.setMenuplan(plan);
					mensaMap.put(parseMensa.getObjectId(), mensa);
				}
				plan.add(parseMenu(parseMenuMensa));
			}
			List<Mensa> mensas = new ArrayList<Mensa>(mensaMap.values());
			sortMensaList(mensas);
			return mensas;
		} catch (JSONException e) {
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

	private int getUpdateTimestamp(ParseObject parseMensa) throws JSONException {
		for (int j = 0; j < updateStatusJson.length(); j++)
			if (updateStatusJson.getJSONObject(j).getInt("id") == Integer.parseInt(parseMensa.getString("mensaId")))
				return updateStatusJson.getJSONObject(j).getInt("timestamp");
		return 0;
	}

	private void sortMensaList(List<Mensa> mensas) {
		Collections.sort(mensas, new Comparator<Mensa>() {
			@Override
			public int compare(Mensa lhs, Mensa rhs) {
				return lhs.getId() - rhs.getId();
			}
		});
	}

	private Mensa parseMensa(ParseObject parseMensa, int timestamp) throws JSONException {
		MensaBuilder builder = new MensaBuilder();
		int mensaId = Integer.parseInt(parseMensa.getString("mensaId"));
		builder.setId(mensaId);
		builder.setName(parseMensa.getString("name"));
		builder.setStreet(parseMensa.getString("street"));
		builder.setZip(parseMensa.getString("plz"));
		builder.setLongitude(Double.parseDouble(parseMensa.getString("lon")));
		builder.setLatitude(Double.parseDouble(parseMensa.getString("lat")));
		builder.setIsFavorite(dataSource.isInFavorites(mensaId));
		builder.setTimestamp(timestamp);
		return builder.build();
	}

	private Menu parseMenu(ParseObject parseMenuMensa) throws ParseException {
		ParseObject parseMenu = parseMenuMensa.getParseObject("menu");
//		Menu result = menuManager.createMenu(parseMenu.getObjectId(), parseMenu.getString("title"),
//				parseMenu.getString("description"), new Day(fm.parse(parseMenuMensa.getString("date"))),
//				parseMenu.getInt("ratingCount"), parseMenu.getInt("ratingSum"));
		Menu result = menuManager.createMenu(parseMenu.getObjectId(), parseMenu.getString("title"),
				parseMenu.getString("description"), new Day(fm.parse(parseMenuMensa.getString("date"))),
				0,0);
		return result;
	}
}