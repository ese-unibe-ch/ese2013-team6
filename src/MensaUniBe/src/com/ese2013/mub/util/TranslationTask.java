package com.ese2013.mub.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class TranslationTask extends AbstractAsyncTask<Void, Void, Void> {
	private Language newLang;
	private Collection<Menu> menus;
	private String[] newTitles, newDescriptions;
	private static final String NEW_LINE = " ; ";
	private List<TranslationTaskCallback> callbacks = new ArrayList<TranslationTaskCallback>();
	private MenuManager menuManager;

	public TranslationTask(MenuManager menuManager, Language newLang, TranslationTaskCallback... callbacks) {
		this.newLang = newLang;
		this.menuManager = menuManager;
		this.menus = menuManager.getMenus();

		for (TranslationTaskCallback callback : callbacks)
			this.callbacks.add(callback);
	}

	public TranslationTask(Collection<Menu> menus, Language newLang) {
		this.newLang = newLang;
		this.menus = menus;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		String[] menuTitles = new String[menus.size()];
		String[] descriptions = new String[menus.size()];

		int i = 0;
		for (Menu menu : menus) {
			menuTitles[i] = menu.getTitle();
			descriptions[i] = menu.getDescription().replace("\n", NEW_LINE).replace("\"", "'");
			i++;
		}

		try {
			newTitles = Translate.execute(menuTitles, Language.GERMAN, newLang);
			newDescriptions = Translate.execute(descriptions, Language.GERMAN, newLang);
		} catch (Exception e) {
			// Api throws general exception...
			setException(e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void arg0) {
		super.onPostExecute(arg0);
		if (hasSucceeded()) {
			int i = 0;
			for (Menu menu : menus) {
				menu.setTranslatedTitle(newTitles[i]);
				menu.setTranslatedDescription(newDescriptions[i].replace(NEW_LINE.trim(), "\n").replace("'", "\""));
				i++;
			}
			menuManager.setTranslationsAvailable(true);
		} else {
			menuManager.setTranslationsAvailable(false);
			logException("TRANSLATION", "Could not translate");
		}
		for (TranslationTaskCallback callback : callbacks)
			callback.onTaskFinished(this);
	}
}