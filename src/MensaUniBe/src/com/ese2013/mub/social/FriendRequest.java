package com.ese2013.mub.social;

public class FriendRequest {
	private User from, to;
	private String id;

	public FriendRequest(String id, User from, User to) {
		this.id = id;
		this.from = from;
		this.to = to;
	}

	public FriendRequest(User from, User to) {
		this(null, from, to);
	}

	public String getId() {
		return id;
	}

	public User getFrom() {
		return from;
	}

	public User getTo() {
		return to;
	}
}