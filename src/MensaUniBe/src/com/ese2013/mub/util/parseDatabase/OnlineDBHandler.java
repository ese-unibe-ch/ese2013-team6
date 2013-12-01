package com.ese2013.mub.util.parseDatabase;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.social.CurrentUser;
import com.ese2013.mub.social.FriendsList;
import com.ese2013.mub.social.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class OnlineDBHandler {
	private static final String FRIENDSHIP = "Friendship", MENU_RATING_SUM = "ratingSum", MENU_RATING_CT = "ratingCount",
			USER_NICKNAME = "nickname", USER_EMAIL = "email", MENU = "Menu", USER = "AppUser", USER_1 = "user1",
			USER_2 = "user2";

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

	public CurrentUser loginUser(CurrentUser user) throws ParseException {
		ParseObject u = getUserByMail(user);
		user.setId(u.getObjectId());
		user.setNick(u.getString(USER_NICKNAME));
		return user;
	}

	public CurrentUser registerIfNotExists(CurrentUser user) throws ParseException {
		ParseObject u;
		try {
			u = getUserByMail(user);
		} catch (ParseException e) {
			u = new ParseObject(USER);
			u.put(USER_EMAIL, user.getEmail());
			u.put(USER_NICKNAME, user.getNick());
			u.save();
		}
		user.setId(u.getObjectId());
		user.setNick(u.getString(USER_NICKNAME));
		return user;
	}

	public void addAsFriend(CurrentUser user, String otherEmail) throws ParseException {
		addFriendship(user, getUserByMail(otherEmail));
	}

	private void addFriendship(User user1, ParseObject user2) {
		ParseObject f = new ParseObject(FRIENDSHIP);
		f.put(USER_1, ParseObject.createWithoutData(USER, user1.getId()));
		f.put(USER_2, user2);
		f.saveEventually();
	}

	public void getFriends(CurrentUser user) throws ParseException {
		FriendsList friends = user.getFriends();

		List<ParseQuery<ParseObject>> or = new ArrayList<ParseQuery<ParseObject>>();
		ParseObject currentUserObject = ParseObject.createWithoutData(USER, user.getId());
		or.add(ParseQuery.getQuery(FRIENDSHIP).whereEqualTo(USER_1, currentUserObject));
		or.add(ParseQuery.getQuery(FRIENDSHIP).whereEqualTo(USER_2, currentUserObject));
		ParseQuery<ParseObject> query = ParseQuery.or(or);
		query.include(USER_1);
		query.include(USER_2);
		List<ParseObject> parseRelationships = query.find();
		for (ParseObject parseRelationship : parseRelationships) {
			ParseObject user1 = parseRelationship.getParseObject(USER_1);
			ParseObject user2 = parseRelationship.getParseObject(USER_2);
			ParseObject toAdd;
			if (user1.getObjectId().equals(currentUserObject.getObjectId()))
				toAdd = user2;
			else
				toAdd = user1;

			friends.addFriend(new User(toAdd.getObjectId(), toAdd.getString(USER_EMAIL), toAdd.getString(USER_NICKNAME)));
		}
	}

	private ParseObject getUserByMail(User user) throws ParseException {
		return getUserByMail(user.getEmail());
	}

	private ParseObject getUserByMail(String email) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER);
		query.whereMatches(USER_EMAIL, email);
		return query.getFirst();
	}
}