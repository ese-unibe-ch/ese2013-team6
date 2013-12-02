package com.ese2013.mub.social;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Invitation {
	private User from;
	private Map<User, Response> to;
	private String id, message;
	private int mensaId;
	private Date time;

	public Invitation(String id, User from, List<User> to, String message, int mensaId, Date time) {
		this.id = id;
		this.from = from;
		this.message = message;
		this.mensaId = mensaId;
		this.time = time;
		this.to = new HashMap<User, Response>();
		for (User u : to)
			this.to.put(u, Response.UNKNOWN);
	}

	public Invitation(String id, User from, HashMap<User, Invitation.Response> to, String message, int mensaId,
			Date time) {
		this.id = id;
		this.from = from;
		this.message = message;
		this.mensaId = mensaId;
		this.time = time;
		this.to = new HashMap<User, Response>(to);
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

	public Response getResponseOf(User user) {
		return to.get(user);
	}

	public static enum Response {
		UNKNOWN, ACCEPTED, DECLINED;
	}

	public String getId() {
		return id;
	}
}