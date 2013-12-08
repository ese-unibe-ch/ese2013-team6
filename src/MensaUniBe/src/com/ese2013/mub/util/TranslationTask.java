package com.ese2013.mub.util;

import java.util.Collection;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Asynchronous task to translate existing Menus from German to another Language
 * using the Microsoft Translating Api.
 */
public class TranslationTask extends AbstractAsyncTask<Void, Void, Void> {
	private static final String NEW_LINE_CODE = " ; ", DOUBLE_QUOTE_CODE = " ' ",
			NONE_ASCII_CHARACTERS_REGEX = "[^\\x00-\\x7F]";
	private Language newLang;
	private Collection<Menu> menus;
	private String[] newTitles, newDescriptions;
	private TranslationTaskCallback callback;

	/**
	 * Creates a new TranslationTask.
	 * 
	 * @param menuManager
	 *            MenuManager which contains the Menus to be translated. Must
	 *            not be null.
	 * @param newLang
	 *            Language which the Menus should be translated into.Must not be
	 *            null.
	 * @param callback
	 *            TranslationTaskCallback to be called when the task is done.
	 *            Must not be null.
	 */
	public TranslationTask(MenuManager menuManager, Language newLang, TranslationTaskCallback callback) {
		Translate.setClientId("ESE-Mub");
		Translate.setClientSecret("3N8wC0wPZPj2v6KTT6GR/B28UDythCvpJ/NSWolMzwU=");

		this.newLang = newLang;
		this.menus = menuManager.getMenus();
		this.callback = callback;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		String[] menuTitles = new String[menus.size()];
		String[] descriptions = new String[menus.size()];

		int i = 0;
		for (Menu menu : menus) {
			menuTitles[i] = menu.getOrigTitle().replaceAll(NONE_ASCII_CHARACTERS_REGEX, "");
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
		} else {
			logException("TRANSLATION", "Could not translate");
		}
		callback.onTranslationTaskFinished(this);
	}
}