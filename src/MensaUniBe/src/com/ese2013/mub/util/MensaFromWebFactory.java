package com.ese2013.mub.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
	private SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
	private MenuManager menuManager;

	public MensaFromWebFactory(MenuManager menuManager) {
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
					plan = new WeeklyMenuplan();
					mensa = parseMensa(parseMensa);
					mensa.setMenuplan(plan);
					mensaMap.put(parseMensa.getObjectId(), mensa);
				}
				plan.add(parseMenu(parseMenuMensa), new Day(fm.parse(parseMenuMensa.getString("date"))));
			}
			List<Mensa> mensas = new ArrayList<Mensa>(mensaMap.values());
			sortMensaList(mensas);
			return mensas;
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

	private void sortMensaList(List<Mensa> mensas) {
		Collections.sort(mensas, new Comparator<Mensa>() {
			@Override
			public int compare(Mensa lhs, Mensa rhs) {
				return lhs.getId() - rhs.getId();
			}
		});
	}

	private Mensa parseMensa(ParseObject parseMensa) {
		MensaBuilder builder = new MensaBuilder();
		int mensaId = Integer.parseInt(parseMensa.getString("mensaId"));
		builder.setId(mensaId);
		builder.setName(parseMensa.getString("name"));
		builder.setStreet(parseMensa.getString("street"));
		builder.setZip(parseMensa.getString("plz"));
		builder.setLongitude(Double.parseDouble(parseMensa.getString("lon")));
		builder.setLatitude(Double.parseDouble(parseMensa.getString("lat")));
		builder.setIsFavorite(dataSource.isInFavorites(mensaId));
		return builder.build();
	}

	private Menu parseMenu(ParseObject parseMenuMensa) throws ParseException {
		ParseObject parseMenu = parseMenuMensa.getParseObject("menu");
		Menu result = menuManager.createMenu(parseMenu.getObjectId(), parseMenu.getString("title"),
				parseMenu.getString("description"));
		return result;
	}
}