package com.ese2013.mub.util.parseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.social.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class OnlineMensaDBHandler {
	private static final String MENU_RATING_SUM = "ratingSum", MENU_RATING_CT = "ratingCount", MENU = "Menu",
			USER = "AppUser", USER_RATING = "UserRating", USER_RATING_MENU = "menu", USER_RATING_USER = "user";

	public void getMenuRatings(MenuManager menuManager) throws ParseException {
		Collection<Menu> menus = menuManager.getMenus();
		List<String> menuIds = new ArrayList<String>(menus.size());
		for (Menu m : menus)
			menuIds.add(m.getId());

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Menu");
		List<String> keys = new ArrayList<String>();
		keys.add("ratingCount");
		keys.add("ratingSum");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.selectKeys(keys);
		query.whereContainedIn("objectId", menuIds);
		List<ParseObject> parseMenus = query.find();
		for (ParseObject parseMenu : parseMenus) {
			Menu m = menuManager.getMenu(parseMenu.getObjectId());
			m.setRatingCount(parseMenu.getInt("ratingCount"));
			m.setRatingSum(parseMenu.getInt("ratingSum"));
		}
	}

	public void saveMenuRating(final User user, Menu menu, final int rating) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MENU);
		query.getInBackground(menu.getId(), new GetCallback<ParseObject>() {
			public void done(ParseObject parseMenu, ParseException e) {
				if (e == null) {
					parseMenu.increment(MENU_RATING_CT);
					parseMenu.increment(MENU_RATING_SUM, rating);
					parseMenu.saveEventually();
					ParseObject userRating = new ParseObject(USER_RATING);
					userRating.put(USER_RATING_USER, ParseObject.createWithoutData(USER, user.getId()));
					userRating.put(USER_RATING_MENU, parseMenu);
					userRating.saveEventually();
				}
			}
		});
	}

	public List<String> getRatedMenus(User user) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_RATING);
		query.whereEqualTo(USER_RATING_USER, ParseObject.createWithoutData(USER, user.getId()));
		List<String> keys = new ArrayList<String>();
		keys.add(USER_RATING_MENU);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.selectKeys(keys);
		List<ParseObject> parseUserRatings = query.find();
		List<String> menuIds = new ArrayList<String>();
		for (ParseObject parseUserRating : parseUserRatings)
			menuIds.add(parseUserRating.getParseObject(USER_RATING_MENU).getObjectId());
		return menuIds;
	}
}
