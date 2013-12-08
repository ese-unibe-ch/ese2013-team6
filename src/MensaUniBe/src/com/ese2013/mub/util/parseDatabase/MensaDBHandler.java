package com.ese2013.mub.util.parseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.model.Mensa.MensaBuilder;
import com.ese2013.mub.social.User;
import com.ese2013.mub.util.parseDatabase.tables.MensaTable;
import com.ese2013.mub.util.parseDatabase.tables.MenuMensaTable;
import com.ese2013.mub.util.parseDatabase.tables.MenuTable;
import com.ese2013.mub.util.parseDatabase.tables.UserRatingTable;
import com.ese2013.mub.util.parseDatabase.tables.UserTable;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MensaDBHandler {
	private static final String OBJECT_ID = "objectId";
	private SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

	public List<Mensa> getMensasAndMenus(MenuManager menuManager) throws ParseException, java.text.ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MenuMensaTable.TABLE_NAME);
		query.include(MenuMensaTable.MENU);
		query.include(MenuMensaTable.MENSA);
		List<ParseObject> parseMenuMensas = query.find();

		HashMap<String, Mensa> mensaMap = new HashMap<String, Mensa>();
		for (int i = 0; i < parseMenuMensas.size(); i++) {
			WeeklyMenuplan plan;
			ParseObject parseMenuMensa = parseMenuMensas.get(i);
			ParseObject parseMensa = parseMenuMensa.getParseObject(MenuMensaTable.MENSA);
			Mensa mensa = mensaMap.get(parseMensa.getObjectId());
			if (mensa != null) {
				plan = mensa.getMenuplan();
			} else {
				plan = new WeeklyMenuplan();
				mensa = parseMensa(parseMensa);
				mensa.setMenuplan(plan);
				mensaMap.put(parseMensa.getObjectId(), mensa);
			}
			plan.add(parseMenu(parseMenuMensa, menuManager),
					new Day(fm.parse(parseMenuMensa.getString(MenuMensaTable.DATE))));
		}
		return new ArrayList<Mensa>(mensaMap.values());
	}

	private Mensa parseMensa(ParseObject parseMensa) {
		MensaBuilder builder = new MensaBuilder();
		builder.setId(Integer.parseInt(parseMensa.getString(MensaTable.MENSA_ID)));
		builder.setName(parseMensa.getString(MensaTable.NAME));
		builder.setStreet(parseMensa.getString(MensaTable.STREET));
		builder.setZip(parseMensa.getString(MensaTable.PLZ));
		builder.setLongitude(Double.parseDouble(parseMensa.getString(MensaTable.LON)));
		builder.setLatitude(Double.parseDouble(parseMensa.getString(MensaTable.LAT)));
		return builder.build();
	}

	private Menu parseMenu(ParseObject parseMenuMensa, MenuManager menuManager) throws ParseException {
		ParseObject parseMenu = parseMenuMensa.getParseObject(MenuMensaTable.MENU);
		Menu result = menuManager.createMenu(parseMenu.getObjectId(), parseMenu.getString(MensaTable.TITLE),
				parseMenu.getString(MenuTable.DESCRIPTION));
		return result;
	}

	public void getMenuRatings(MenuManager menuManager) throws ParseException {
		Collection<Menu> menus = menuManager.getMenus();
		List<String> menuIds = new ArrayList<String>(menus.size());
		for (Menu m : menus)
			menuIds.add(m.getId());

		ParseQuery<ParseObject> query = ParseQuery.getQuery(MenuTable.TABLE_NAME);
		List<String> keys = new ArrayList<String>();
		keys.add(MenuTable.RATING_COUNT);
		keys.add(MenuTable.RATING_SUM);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.selectKeys(keys);
		query.whereContainedIn(OBJECT_ID, menuIds);
		List<ParseObject> parseMenus = query.find();
		for (ParseObject parseMenu : parseMenus) {
			Menu m = menuManager.getMenu(parseMenu.getObjectId());
			m.setRatingCount(parseMenu.getInt(MenuTable.RATING_COUNT));
			m.setRatingSum(parseMenu.getInt(MenuTable.RATING_SUM));
		}
	}

	public void saveMenuRating(final User user, Menu menu, final int rating) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MenuTable.TABLE_NAME);
		query.getInBackground(menu.getId(), new GetCallback<ParseObject>() {
			public void done(ParseObject parseMenu, ParseException e) {
				if (e == null) {
					parseMenu.increment(MenuTable.RATING_COUNT);
					parseMenu.increment(MenuTable.RATING_SUM, rating);
					parseMenu.saveEventually();
					ParseObject userRating = new ParseObject(UserRatingTable.TABLE_NAME);
					userRating.put(UserRatingTable.USER,
							ParseObject.createWithoutData(UserTable.TABLE_NAME, user.getId()));
					userRating.put(UserRatingTable.MENU, parseMenu);
					userRating.saveEventually();
				}
			}
		});
	}

	public List<String> getRatedMenus(User user) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(UserRatingTable.TABLE_NAME);
		query.whereEqualTo(UserRatingTable.USER,
				ParseObject.createWithoutData(UserTable.TABLE_NAME, user.getId()));
		List<String> keys = new ArrayList<String>();
		keys.add(UserRatingTable.MENU);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.selectKeys(keys);
		List<ParseObject> parseUserRatings = query.find();
		List<String> menuIds = new ArrayList<String>();
		for (ParseObject parseUserRating : parseUserRatings)
			menuIds.add(parseUserRating.getParseObject(UserRatingTable.MENU).getObjectId());
		return menuIds;
	}
}
