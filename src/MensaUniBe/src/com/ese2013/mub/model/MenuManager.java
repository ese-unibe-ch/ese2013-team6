package com.ese2013.mub.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.ese2013.mub.model.Menu.MenuBuilder;

/**
 * Responsible to manage the list of all menus. Assures that the Menu objects
 * are unique by there id. All Menus should be created by calling the
 * MenuManager.
 * 
 */
public class MenuManager {
	private HashMap<String, Menu> menuMap = new HashMap<String, Menu>();
	private boolean translationsEnabled, translationsAvailable;

	public Collection<Menu> getMenus() {
		return menuMap.values();
	}
	
	public Set<String> getMenuIds() {
		return menuMap.keySet();
	}

	/**
	 * Creates or returns a menu with the given id, title, description and
	 * translations.
	 * 
	 * @param id
	 *            String id of the Menu. Must not be null.
	 * @param title
	 *            String title of the Menu. Must not be null.
	 * @param description
	 *            String description of the Menu must not be null.
	 * @param translTitle
	 *            String translated title of the Menu. Must not be null. If no
	 *            translation is available, the method which does't need this
	 *            parameter should be called.
	 * @param translDesc
	 *            String translated description of the Menu. Must not be null.
	 *            If no translation is available, the method which does't need
	 *            this parameter should be called.
	 * @return A new Menu object if no Menu with the given id exists or the
	 *         existing instance if already an instance with this id exists.
	 */
	public Menu createMenu(String id, String title, String description, String translTitle, String translDesc) {

		if (menuMap.containsKey(id))
			return menuMap.get(id);

		Menu menu = new MenuBuilder().setId(id).setTitle(title).setDescription(description).setTranslatedTitle(translTitle)
				.setTranslatedDescription(translDesc).build();

		menuMap.put(id, menu);
		return menu;
	}

	/**
	 * Creates or returns a menu with the given id, title, description.
	 * 
	 * @param id
	 *            String id of the Menu. Must not be null.
	 * @param title
	 *            String title of the Menu. Must not be null.
	 * @param description
	 *            String description of the Menu must not be null.
	 * @return A new Menu object if no Menu with the given id exists or the
	 *         existing instance if already an instance with this id exists.
	 */
	public Menu createMenu(String id, String title, String description) {
		return createMenu(id, title, description, "", "");
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

	/**
	 * Returns the menu with the given id.
	 * 
	 * @param objectId
	 *            String id of the menu to return.
	 * @return The Menu with the given id. Null if there is no menu with the
	 *         given id.
	 */
	public Menu getMenu(String objectId) {
		return menuMap.get(objectId);
	}
}
