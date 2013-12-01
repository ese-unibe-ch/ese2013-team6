package com.ese2013.mub.social;

import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class LoginService {
	private static User loggedInUser;

	public static boolean login(User user) {
		try {
			OnlineDBHandler handler = new OnlineDBHandler();
			loggedInUser = handler.registerOrLoginUser(user);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean isLoggedIn() {
		return loggedInUser != null;
	}
}
