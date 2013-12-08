package com.ese2013.mub.social;

/**
 * This class represents a FriendRequest from one user to another user.
 * 
 */
public class FriendRequest {
	private User from, to;
	private String id;

	/**
	 * Creates a FriendRequest from id, the sending User and the retrieving
	 * User. This constructor should be used for FriendRequests which are
	 * already stored on the Server.
	 * 
	 * @param id
	 *            String id of the FriendRequest. Must not be null and must
	 *            correspond to the Id of this FriendRequest on the
	 *            Parse-Server.
	 * @param from
	 *            User who sent the FriendRequest. Must be consistent to the
	 *            User which is saved as Sender on the Parse-Server for the
	 *            given FriendRequest id.
	 * @param to
	 *            User who received the FriendRequest. Must be consistent to the
	 *            User which is saved as Receiver on the Parse-Server for the
	 *            given FriendRequest id.
	 */
	public FriendRequest(String id, User from, User to) {
		this.id = id;
		this.from = from;
		this.to = to;
	}

	/**
	 * Constructs a FriendRequest just from two Users. Should be used to create
	 * a new FriendRequest which has not already been stored on the server. (and
	 * therefore has no String id).
	 * 
	 * @param from
	 *            User who sent the FriendRequest. Must not be null.
	 * @param to
	 *            User who received the FriendRequest. Must not be null
	 */
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