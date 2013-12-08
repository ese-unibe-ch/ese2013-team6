package com.ese2013.mub.util;

import java.util.Collection;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class TranslationTask extends AbstractAsyncTask<Void, Void, Void> {
	private static final String NEW_LINE_CODE = " ; ", DOUBLE_QUOTE_CODE = " ' ";
	private Language newLang;
	private Collection<Menu> menus;
	private String[] newTitles, newDescriptions;

	private TranslationTaskCallback callback;
	private MenuManager menuManager;

	public TranslationTask(MenuManager menuManager, Language newLang, TranslationTaskCallback callback) {
		Translate.setClientId("ESE-Mub");
		Translate.setClientSecret("3N8wC0wPZPj2v6KTT6GR/B28UDythCvpJ/NSWolMzwU=");

		this.newLang = newLang;
		this.menuManager = menuManager;
		this.menus = menuManager.getMenus();
		this.callback = callback;
	}

	public TranslationTask(MenuManager menuManager, Language newLang) {
		this.newLang = newLang;
		this.menuManager = menuManager;
		this.menus = menuManager.getMenus();
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
			menuTitles[i] = menu.getOrigTitle().replaceAll("[^\\x00-\\x7F]", "");
			descriptions[i] = menu.getOrigDescription().replace("\n", NEW_LINE_CODE).replace("\"", DOUBLE_QUOTE_CODE);
			i++;
		}

		try {
			newTitles = Translate.execute(menuTitles, Language.GERMAN, newLang);
			newDescriptions = new String[descriptions.length];
			for (int j = 0; j < newDescriptions.length; j++)
				newDescriptions[j] = Translate.execute(descriptions[j], Language.GERMAN, newLang);

		} catch (Exception e) {
			// Api throws general exception class "Exception"...
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
				menu.setTranslatedDescription(newDescriptions[i].replace(NEW_LINE_CODE.trim(), "\n").replace(
						DOUBLE_QUOTE_CODE, "\""));
				i++;
			}
			menuManager.setTranslationsAvailable(true);
		} else {
			menuManager.setTranslationsAvailable(false);
			logException("TRANSLATION", "Could not translate");
		}
		callback.onTaskFinished(this);
	}
}