package com.ese2013.mub.util.parseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Mensa.MensaBuilder;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.WeeklyMenuplan;
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

/**
 * Handles the connection to the Parse-DB for downloading Mensas, Menus and Menu
 * Ratings. All "get" methods are synchronous and all "save" methods are
 * asynchronous.
 */
public class MensaDBHandler {
	private static final String OBJECT_ID = "objectId";
	private SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

	/**
	 * Creates the list of Mensas and all the served Menus.
	 * 
	 * @param menuManager
	 *            The MenuManager to be used to manage the list of Menus.
	 * @return List of Mensas completely instantiated with their specific
	 *         WeeklyMenuplans.
	 * @throws ParseException
	 *             If the Parse-Server reports an error and the mensas can't be
	 *             downloaded.
	 * @throws java.text.ParseException
	 *             if the date for a menu stored on the server is invalid.
	 *             Should usually not happen.
	 */
	public List<Mensa> getMensasAndMenus(MenuManager menuManager) throws ParseException, java.text.ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MenuMensaTable.TABLE_NAME);
		query.include(MenuMensaTable.MENU);
		query.include(MenuMensaTable.MENSA);
		List<ParseObject> parseMenuMensas = query.find();

		HashMap<String, Mensa> mensaMap = new HashMap<String, Mensa>();
		for (int i = 0; i < parseMenuMensas.size(); i++)
			parseMenuMensa(menuManager, mensaMap, parseMenuMensas.get(i));

		return new ArrayList<Mensa>(mensaMap.values());
	}

	private void parseMenuMensa(MenuManager menuManager, HashMap<String, Mensa> mensaMap, ParseObject parseMenuMensa)
			throws ParseException, java.text.ParseException {
		ParseObject parseMensa = parseMenuMensa.getParseObject(MenuMensaTable.MENSA);
		Mensa mensa = mensaMap.get(parseMensa.getObjectId());
		WeeklyMenuplan plan;
		if (mensa != null) {
			plan = mensa.getMenuplan();
		} else {
			plan = new WeeklyMenuplan();
			mensa = parseMensa(parseMensa);
			mensa.setMenuplan(plan);
			mensaMap.put(parseMensa.getObjectId(), mensa);
		}
		plan.add(parseMenu(parseMenuMensa, menuManager), new Day(fm.parse(parseMenuMensa.getString(MenuMensaTable.DATE))));
	}

	private Mensa parseMensa(ParseObject parseMensa) {
		return new MensaBuilder().setId(Integer.parseInt(parseMensa.getString(MensaTable.MENSA_ID)))
				.setName(parseMensa.getString(MensaTable.NAME)).setStreet(parseMensa.getString(MensaTable.STREET))
				.setZip(parseMensa.getString(MensaTable.PLZ))
				.setLongitude(Double.parseDouble(parseMensa.getString(MensaTable.LON)))
				.setLatitude(Double.parseDouble(parseMensa.getString(MensaTable.LAT))).build();
	}

	private Menu parseMenu(ParseObject parseMenuMensa, MenuManager menuManager) throws ParseException {
		ParseObject parseMenu = parseMenuMensa.getParseObject(MenuMensaTable.MENU);
		Menu result = menuManager.createMenu(parseMenu.getObjectId(), parseMenu.getString(MensaTable.TITLE),
				parseMenu.getString(MenuTable.DESCRIPTION));
		return result;
	}

	/**
	 * Downloads all menu ratings and stores them in the existing menu instances
	 * in the given MenuManager. The ratings are cached by using Parse's
	 * automatic caching functionality. This call always first tries to download
	 * the ratings, and then just loads them from cache if download is not
	 * possible.
	 * 
	 * @param menuManager
	 *            MenuManager which stores all Menus.
	 * @throws ParseException
	 *             if the Parse-Server or Parse-Cache reports an error.
	 */
	public void getMenuRatings(MenuManager menuManager) throws ParseException {
		ParseQuery<ParseObject> query = buildRatingsQuery(menuManager);
		List<ParseObject> parseMenus = query.find();
		for (ParseObject parseMenu : parseMenus) {
			Menu m = menuManager.getMenu(parseMenu.getObjectId());
			m.setRatingCount(parseMenu.getInt(MenuTable.RATING_COUNT));
			m.setRatingSum(parseMenu.getInt(MenuTable.RATING_SUM));
		}
	}

	private ParseQuery<ParseObject> buildRatingsQuery(MenuManager menuManager) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MenuTable.TABLE_NAME);
		List<String> keys = new ArrayList<String>();
		keys.add(MenuTable.RATING_COUNT);
		keys.add(MenuTable.RATING_SUM);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.selectKeys(keys);
		query.whereContainedIn(OBJECT_ID, menuManager.getMenuIds());
		return query;
	}

	/**
	 * Saves the new rating for a given menu.
	 * 
	 * @param user
	 *            User who rated the Menu. Must have a valid Id from the
	 *            Parse-Server and must not be null.
	 * @param menu
	 *            The Menu to be rated. Must also have a valid Id from the
	 *            Parse-Server and must not be null.
	 * @param rating
	 *            The new rating of the user. Must be between 0 and 5.
	 */
	public void saveMenuRating(final User user, Menu menu, final int rating) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MenuTable.TABLE_NAME);
		query.getInBackground(menu.getId(), new GetCallback<ParseObject>() {
			public void done(ParseObject parseMenu, ParseException e) {
				if (e == null)
					updateRating(user, rating, parseMenu);
			}
		});
	}

	private void updateRating(final User user, final int rating, ParseObject parseMenu) {
		parseMenu.increment(MenuTable.RATING_COUNT);
		parseMenu.increment(MenuTable.RATING_SUM, rating);
		parseMenu.saveEventually();
		ParseObject userRating = new ParseObject(UserRatingTable.TABLE_NAME);
		userRating.put(UserRatingTable.USER, ParseObject.createWithoutData(UserTable.TABLE_NAME, user.getId()));
		userRating.put(UserRatingTable.MENU, parseMenu);
		userRating.saveEventually();
	}

	/**
	 * Retrieves the list of ids of Menus the user rated. The user ratings are
	 * cached by using Parse's automatic caching functionality. This call always
	 * first tries to download the user ratings, and then just loads them from
	 * cache if download is not possible.
	 * 
	 * @param user
	 *            User to retrieve the rated menus. Must have a valid id from
	 *            the Parse-Server and must not be null.
	 * @return List of Menu Ids (Strings) the user rated.
	 * @throws ParseException
	 *             if the Parse-Server/Cache reports an error.
	 */
	public List<String> getRatedMenus(User user) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(UserRatingTable.TABLE_NAME);
		query.whereEqualTo(UserRatingTable.USER, ParseObject.createWithoutData(UserTable.TABLE_NAME, user.getId()));
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
