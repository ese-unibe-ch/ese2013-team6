package com.ese2013.mub.model;

import java.util.Collection;
import java.util.HashMap;

import com.ese2013.mub.model.Menu.MenuBuilder;

public class MenuManager {
	private HashMap<String, Menu> menuMap = new HashMap<String, Menu>();
	private boolean translationsEnabled, translationsAvailable;

	public Collection<Menu> getMenus() {
		return menuMap.values();
	}

	public Menu createMenu(String id, String title, String description, String translTitle, String translDesc) {

		Menu menu = menuMap.get(id);
		if (menu != null)
			return menu;

		menu = new Menu(id, title, description, translTitle, translDesc);

		// // menu = new
		// MenuBuilder().setDate(day).setTitle(title).setDescription(description).setId(id)
		// .setRatingCount(ratingCount).setRatingSum(ratingSum).setUserRating(userRating).build();

		menuMap.put(id, menu);
		return menu;
	}

	public Menu createMenu(String id, String title, String description) {
		Menu menu = menuMap.get(id);
		if (menu != null)
			return menu;

		menu = new MenuBuilder().setTitle(title).setDescription(description).setId(id).build();

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

	public Menu getMenu(String objectId) {
		return menuMap.get(objectId);
	}
}
