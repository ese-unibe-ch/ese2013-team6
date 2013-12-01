package com.ese2013.mub.social;

public class CurrentUser extends User {

	private FriendsList friends = new FriendsList();

	public CurrentUser(String email) {
		super(email);
	}

	public CurrentUser(String email, String nick) {
		super(email, nick);
	}

	public CurrentUser(String id, String email, String nick) {
		super(id, email, nick);
	}

	public FriendsList getFriends() {
		return friends;
	}
}