package com.ese2013.mub.model;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.ese2013.mub.model.Menu.MenuBuilder;
import com.ese2013.mub.util.TranslationTask;
import com.memetix.mst.language.Language;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MenuManager {
	private HashMap<String, Menu> menuMap = new HashMap<String, Menu>();

	public Menu createMenu(String id, String title, String description, Day day) {
		// TODO there should be a better solution for this, MenuData object?
		Menu menu = menuMap.get(id);
		if (menu != null)
			return menu;

		menu = new MenuBuilder().setDate(day).setTitle(title).setDescription(description).setId(id).build();

		menuMap.put(id, menu);
		return menu;
	}
	
	public Menu createMenu(String id, String title, String description, Day day, int ratingCount, int ratingSum) {
		// TODO there should be a better solution for this, MenuData object?
//		And load user rating from last time
		Menu menu = menuMap.get(id);
		if (menu != null)
			return menu;

		menu = new MenuBuilder().setDate(day).setTitle(title).setDescription(description).setId(id).setRatingCount(ratingCount).setRatingSum(ratingSum).build();

		menuMap.put(id, menu);
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

	// TODO Timm: Das ist so ungef�hr wie ich mir das vorstelle. Du solltest dem
	// Menu ein User Rating geben sowie Rating Sum und Rating Count von Parse.
	// Ebenfalls sollte es m�glich sein abzufragen ob ein menu schon gerated
	// wurde. Dadurch kann der user sein rating �ndern (siehe code unten, falls
	// schon gerated wurde wird das alte abgezogen). Das ganze ist mit increment
	// �brigens atomar, wodurch es keine probleme geben sollte wenn mehrere
	// Leute gleichzeitig ein Rating hochladen. Die Frage ist halt noch ein
	// wenig ob wir wirklich immer direkt hochladen wollen, k�nnen wir aber am
	// Mittwoch fragen. Erstmal das hier zum Laufen bekommen.

	
	public static void updateMenuRating(Menu menu, int newRating) {
		System.out.println("updateMenuRating started");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Menu");

		final boolean hasBeenRated = menu.hasBeenRated();
//		final int ratingUpdate = hasBeenRated ? newRating - menu.getUserRating() : newRating;
		final int ratingUpdate = newRating;
		query.getInBackground(menu.getId(), new GetCallback<ParseObject>() {
			public void done(ParseObject parseMenu, ParseException e) {
				if (e == null) {
//					if (hasBeenRated)
						parseMenu.increment("ratingCount");
					parseMenu.increment("ratingSum", ratingUpdate);
					parseMenu.saveInBackground();
				}
			}
		});
		menu.setUserRating(newRating);
		Model.getInstance().saveModel();
		System.out.println("updateMenuRating ended");
	}

	public Menu createMenu(String id, String title, String description, Day day, int ratingCount, int ratingSum, int userRating) {
		// TODO there should be a better solution for this, MenuData object?
//		And load user rating from last time
		Menu menu = menuMap.get(id);
		if (menu != null)
			return menu;

		menu = new MenuBuilder().setDate(day).setTitle(title).setDescription(description).setId(id).setRatingCount(ratingCount).setRatingSum(ratingSum).setUserRating(userRating).build();

		menuMap.put(id, menu);
		return menu;
	}
	

}
