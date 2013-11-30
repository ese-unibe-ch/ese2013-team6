package com.ese2013.mub.model;

import java.util.Collection;
import java.util.HashMap;

import com.ese2013.mub.model.Menu.MenuBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MenuManager {
	private HashMap<String, Menu> menuMap = new HashMap<String, Menu>();
	private boolean translationsEnabled, translationsAvailable;

	public Menu createMenu(String id, String title, String description, Day day, int ratingCount, int ratingSum) {
		// TODO there should be a better solution for this, MenuData object?
		// And load user rating from last time
		Menu menu = menuMap.get(id);
		if (menu != null)
			return menu;

		menu = new MenuBuilder().setDate(day).setTitle(title).setDescription(description).setId(id)
				.setRatingCount(ratingCount).setRatingSum(ratingSum).build();

		menuMap.put(id, menu);
		return menu;
	}

	public Collection<Menu> getMenus() {
		return menuMap.values();
	}

	public static void updateMenuRating(Menu menu, int newRating) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Menu");
		final boolean hasBeenRated = menu.hasBeenRated();
		// final int ratingUpdate = hasBeenRated ? newRating -
		// menu.getUserRating() : newRating;
		final int ratingUpdate = newRating;
		query.getInBackground(menu.getId(), new GetCallback<ParseObject>() {
			public void done(ParseObject parseMenu, ParseException e) {
				if (e == null) {
					// if (hasBeenRated)
					parseMenu.increment("ratingCount");
					parseMenu.increment("ratingSum", ratingUpdate);
					parseMenu.saveInBackground();
				}
			}
		});
		menu.setUserRating(newRating);
		Model.getInstance().saveModel();
	}

	public Menu createMenu(String id, String title, String description, Day day, int ratingCount, int ratingSum,
			int userRating) {

		return createMenu(id, title, description, "", "", day, ratingCount, ratingSum, userRating);
	}

	public Menu createMenu(String id, String title, String description, String translTitle, String translDesc, Day day,
			int ratingCount, int ratingSum, int userRating) {

		Menu menu = menuMap.get(id);
		if (menu != null)
			return menu;

		menu = new Menu(id, title, description, translTitle, translDesc, day, ratingCount, ratingSum, userRating);

		// // menu = new
		// MenuBuilder().setDate(day).setTitle(title).setDescription(description).setId(id)
		// .setRatingCount(ratingCount).setRatingSum(ratingSum).setUserRating(userRating).build();

		menuMap.put(id, menu);
		return menu;
	}

	public boolean isTranslationEnabled() {
		return translationsEnabled;
	}

	public boolean translationsAvailable() {
		return translationsAvailable;
	}

	public void setTranslationsEnabled(boolean translationsEnabled) {
		this.translationsEnabled = translationsEnabled;
	}

	public void setTranslationsAvailable(boolean translationsAvailable) {
		this.translationsAvailable = translationsAvailable;
	}
}
