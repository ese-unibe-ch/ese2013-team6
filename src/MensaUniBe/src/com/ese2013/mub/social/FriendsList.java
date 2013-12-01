package com.ese2013.mub.social;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FriendsList implements Iterable<User> {
	List<User> friends = new ArrayList<User>();

	public void addFriend(User user) {
		friends.add(user);
	}

	public void removeFriend(User user) {
		friends.remove(user);
	}

	public User get(int i) {
		return friends.get(i);
	}

	@Override
	public Iterator<User> iterator() {
		return friends.iterator();
	}
}
