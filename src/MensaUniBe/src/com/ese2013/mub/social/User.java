package com.ese2013.mub.social;

public class User {
	private String id, email, nick;

	public User(String id, String email, String nick) {
		this.id = id;
		this.email = email;
		this.nick = nick;
	}

	public User(String email, String nick) {
		this(null, email, nick);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public String getNick() {
		return nick;
	}
}
