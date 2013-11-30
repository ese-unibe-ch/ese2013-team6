package com.ese2013.mub.util.parseDatabase;

import com.ese2013.mub.model.Menu;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class OnlineDataSource {
	public void updateMenuRating(Menu menu, final int rating) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Menu");

		query.getInBackground(menu.getId(), new GetCallback<ParseObject>() {
			public void done(ParseObject parseMenu, ParseException e) {
				if (e == null) {
					parseMenu.increment("ratingCount");
					parseMenu.increment("ratingSum", rating);
					parseMenu.saveInBackground();
				}
			}
		});
		menu.setUserRating(rating);
	}
}