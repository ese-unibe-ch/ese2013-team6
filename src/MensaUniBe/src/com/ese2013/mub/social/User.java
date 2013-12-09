package com.ese2013.mub.social;

/**
 * Represents a simple user by storing an id, email and nickname.
 */
public class User implements Comparable<User> {
	private String id, email, nick;

	/**
	 * Creates a User given an email. This should only be used if the User
	 * object is used to query the database for the User Id and Nickname.
	 * 
	 * @param email
	 *            String containing the user's email. Must not be null.
	 */

	public User(String email) {
		this(null, email, null);
	}

	/**
	 * Creates a User from id, email and nick name.
	 * 
	 * @param id
	 *            String id, should be the id retrieved from the Parse-Server.
	 * @param email
	 *            String email, should be the email associated to the id. Must
	 *            not be null.
	 * @param nick
	 *            String nick name, should be the nick name associated to the
	 *            id. Must not be null.
	 */
	public User(String id, String email, String nick) {
		this.id = id;
		this.email = email;
		this.nick = nick;
	}

	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the User to the given id.
	 * 
	 * @param id
	 *            String id for the User. Must not be null and must correspond
	 *            to the id of the user associated to the email on the
	 *            Parse-Server.
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	/**
	 * Sets the nick of the User.
	 * 
	 * @param nick
	 *            String nick name. Must not be null and must correspond to the
	 *            nick name of the user on the Parse-Server.
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	@Override
	public String toString() {
		return nick;
	}

	@Override
	public int compareTo(User another) {
		return another.getNick().compareTo(this.getNick());
	}
}
