package com.ese2013.mub.social;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.model.Menu;

/**
 * Represents the currently logged in User. An instance can be retrieved by
 * using LoginService.getLoggedInUser(). Stores also the friends list and the
 * unanswered friend requests.
 * 
 */
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

	public void addFriend(User user) {
		friends.add(user);
	}

	public void removeFriend(User user) {
		friends.remove(user);
	}

	public List<User> getFriends() {
		return friends;
	}

	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

	public List<FriendRequest> getFriendRequests() {
		return requests;
	}

	public void removeFriendRequest(FriendRequest request) {
		requests.remove(request);
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

	public boolean hasFriendWith(String email) {
		for (User u : friends)
			if (u.getEmail().equals(email))
				return true;
		return false;
	}

	public void setFriendRequests(List<FriendRequest> requests) {
		this.requests = requests;
	}
}