package com.ese2013.mub.social;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an invitation from one User to a group of Users for a given Mensa
 * and time. Also stores the responses of the addressees.
 */
public class Invitation implements Comparable<Invitation> {
	private User from;
	private Map<User, Response> to;
	private String id, message;
	private int mensaId;
	private Date time;

	/**
	 * Creates a new Invitation which is not already on the Server. This
	 * constructor should be used to create invitations to send.
	 * 
	 * @param from
	 *            User which sends this invitation. Must not be null and have a
	 *            valid id.
	 * @param to
	 *            List of Users which receive this invitation. Must not be null
	 *            and all User objects in the list must have a valid id.
	 * @param message
	 *            String message to be sent. Must not be null.
	 * @param mensaId
	 *            int id of the Mensa where the User "from" invites to. Must be
	 *            a valid Mensa id.
	 * @param time
	 *            Date when the appointment is. Must not be in the past and must
	 *            not be null.
	 */
	public Invitation(User from, List<User> to, String message, int mensaId, Date time) {
		this.from = from;
		this.message = message;
		this.mensaId = mensaId;
		this.time = time;
		this.to = new HashMap<User, Response>();
		for (User u : to)
			this.to.put(u, Response.UNKNOWN);
	}

	/**
	 * Creates a new Invitation which is already on the Server. This constructor
	 * should be used to create invitations which habe been received. All
	 * arguments must be consistent with the data stored on the Parse-Server
	 * under the given String id.
	 * 
	 * @param id
	 *            String id of this Invitation on the Parse-Server.
	 * 
	 * @param from
	 *            User which sends this invitation. Must not be null and have a
	 *            valid id.
	 * @param to
	 *            Map of Users to their responses. Must not be null and all User
	 *            objects in the list must have a valid id and all responses
	 *            should be not null.
	 * @param message
	 *            String message to be sent. Must not be null.
	 * @param mensaId
	 *            int id of the Mensa where the User "from" invites to. Must be
	 *            a valid Mensa id.
	 * @param time
	 *            Date when the appointment is. Must not be in the past and must
	 *            not be null.
	 */
	public Invitation(String id, User from, HashMap<User, Invitation.Response> to, String message, int mensaId, Date time) {
		this.id = id;
		this.from = from;
		this.message = message;
		this.mensaId = mensaId;
		this.time = time;
		this.to = new HashMap<User, Response>(to);
	}

	public String getId() {
		return id;
	}

	public User getFrom() {
		return from;
	}

	public Collection<User> getTo() {
		return to.keySet();
	}

	public String getMessage() {
		return message;
	}

	public int getMensa() {
		return mensaId;
	}

	public Date getTime() {
		return time;
	}

	/**
	 * Returns the response of a given User.
	 * 
	 * @param user
	 *            User to get response of, must not be null and must be invited
	 *            by this Invitation.
	 * @return Response of the User, is not null as long as the given User is
	 *         invited by this Invitation (i.e. is in the Collection of Users
	 *         which can be retrieved by calling getTo()).
	 */
	public Response getResponseOf(User user) {
		return to.get(user);
	}

	/**
	 * Sets the response of a given User.
	 * 
	 * @param user
	 *            User who answers. Must not be null.
	 * @param response
	 *            Response of the User. Must not be UNKNOWN or null.
	 */
	public void setResponseOf(User user, Response response) {
		if (getTo().contains(user))
			to.put(user, response);
	}

	public static enum Response {
		UNKNOWN, ACCEPTED, DECLINED;
	}

	/**
	 * Sort by the time when the invitation is due.
	 */
	@Override
	public int compareTo(Invitation other) {
		return getTime().compareTo(other.getTime());
	}

}