package com.ese2013.mub.social;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.model.Menu;

public class CurrentUser extends User {

	private List<User> friends = new ArrayList<User>();
	private List<FriendRequest> requests = new ArrayList<FriendRequest>();
	private List<String> ratedMenuIds;

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

	public List<FriendRequest> getFriendRequests() {
		return requests;
	}

	public void setRatedMenuIds(List<String> ratedMenuIds) {
		this.ratedMenuIds = ratedMenuIds;
	}

	public boolean hasBeenRated(Menu menu) {
		return ratedMenuIds.contains(menu.getId());
	}

	public void addToRated(Menu menu) {
		ratedMenuIds.add(menu.getId());
	}
}