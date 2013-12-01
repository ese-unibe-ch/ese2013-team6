package com.ese2013.mub.util.parseDatabase;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.social.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class OnlineDBHandler {
	private static final String MENU_RATING_SUM = "ratingSum";
	private static final String MENU_RATING_CT = "ratingCount";
	private static final String USER_NICKNAME = "nickname";
	private static final String USER_EMAIL = "email";
	private static final String MENU = "Menu";
	private static final String USER = "AppUser";

	public void saveMenuRating(Menu menu, final int rating) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MENU);
		query.getInBackground(menu.getId(), new GetCallback<ParseObject>() {
			public void done(ParseObject parseMenu, ParseException e) {
				if (e == null) {
					parseMenu.increment(MENU_RATING_CT);
					parseMenu.increment(MENU_RATING_SUM, rating);
					parseMenu.saveInBackground();
				}
			}
		});
	}

	public User registerOrLoginUser(User user) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER);
		query.whereMatches(USER_EMAIL, user.getEmail());
		ParseObject u;
		try {
			u = query.getFirst();
		} catch (ParseException e) {
			u = new ParseObject(USER);
			u.put(USER_EMAIL, user.getEmail());
			u.put(USER_NICKNAME, user.getNick());
			u.save();
		}
		System.out.println(u.getObjectId());
		return new User(u.getObjectId(), user.getEmail(), user.getNick());
	}
}