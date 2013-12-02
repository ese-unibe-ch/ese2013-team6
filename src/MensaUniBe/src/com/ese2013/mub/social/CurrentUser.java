package com.ese2013.mub.social;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.model.Menu;

public class CurrentUser extends User {

	private List<User> friends = new ArrayList<User>();
	private List<String> menuIds;

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

	public void setRatedMenuIds(List<String> menuIds) {
		this.menuIds = menuIds;
	}

	public boolean hasBeenRated(Menu menu) {
		return menuIds.contains(menu.getId());
	}

	public void addToRated(Menu menu) {
		menuIds.add(menu.getId());
	}
}