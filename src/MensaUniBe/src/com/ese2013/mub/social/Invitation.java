package com.ese2013.mub.social;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Invitation {
	private User from;
	private Map<User, Response> to = new HashMap<User, Response>();
	private String message;
	private int mensaId;
	private Date time;

	public Invitation(User from, List<User> to, String message, int mensaId, Date time) {
		this.from = from;
		this.message = message;
		this.mensaId = mensaId;
		this.time = time;

		for (User u : to)
			this.to.put(u, Response.UNKNOWN);
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
}