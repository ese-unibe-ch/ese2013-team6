package com.ese2013.mub.social;

import java.util.ArrayList;
import java.util.List;

public class CurrentUser extends User {

	private List<User> friends = new ArrayList<User>();

	public CurrentUser(String email) {
		super(email);
	}

	public CurrentUser(String email, String nick) {
		super(email, nick);
	}

	public CurrentUser(String id, String email, String nick) {
		super(id, email, nick);
	}

	public List<User> getFriends() {
		return friends;
	}
}