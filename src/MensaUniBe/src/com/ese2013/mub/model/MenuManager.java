package com.ese2013.mub.model;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.ese2013.mub.model.Menu.MenuBuilder;
import com.ese2013.mub.util.TranslationTask;
import com.memetix.mst.language.Language;

public class MenuManager {
	private HashMap<String, Menu> menuMap = new HashMap<String, Menu>();
	private int nextId = 0;

	public Menu createMenu(String title, String description, Day day) {
		// TODO there should be a better solution for this, MenuData object?
		String key = title + description + day.format(new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN));
		Menu menu = menuMap.get(key);
		if (menu != null)
			return menu;

		menu = new MenuBuilder().setDate(day).setTitle(title).setDescription(description).setId(nextId).build();
		nextId++;

		menuMap.put(key, menu);
		return menu;
	}

	public void translateAllMenus() {
		new TranslationTask(menuMap.values(), Language.ENGLISH).execute();
	}

	public void translateAllMenusSync() {
		try {
			TranslationTask task = new TranslationTask(menuMap.values(), Language.ENGLISH);
			task.execute();
			task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
