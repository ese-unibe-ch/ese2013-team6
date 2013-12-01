package com.ese2013.mub.util.parseDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.social.CurrentUser;
import com.ese2013.mub.social.FriendRequest;
import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

public class OnlineDBHandler {
	// TODO Refactor to different classes
	private static final String INV_USER_RESPONSE = "Response", INV_USER_INVITEE = "Invitee",
			INV_USER_INVITATION = "Invitation", INVITATION_TIME = "Time", INVITATION_FROM = "From",
			INVITATION_MENSA = "Mensa", INVITATION_MESSAGE = "Message", INVITATION = "Invitation",
			INVITATION_USER = "InvitationUser", FRIENDSHIP = "Friendship", MENU_RATING_SUM = "ratingSum",
			MENU_RATING_CT = "ratingCount", USER_NICKNAME = "nickname", USER_EMAIL = "email", MENU = "Menu",
			USER = "AppUser", USER_1 = "user1", USER_2 = "user2", FRIEND_REQUEST_TO = "To", FRIEND_REQUEST_FROM = "From",
			FRIEND_REQUEST = "FriendRequest";

	public void saveMenuRating(Menu menu, final int rating) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MENU);
		query.getInBackground(menu.getId(), new GetCallback<ParseObject>() {
			public void done(ParseObject parseMenu, ParseException e) {
				if (e == null) {
					parseMenu.increment(MENU_RATING_CT);
					parseMenu.increment(MENU_RATING_SUM, rating);
					parseMenu.saveInBackground();
				}
			}
		});
	}

	public User getUser(User user) throws ParseException {
		ParseObject u = getUserByMail(user);
		user.setId(u.getObjectId());
		user.setNick(u.getString(USER_NICKNAME));
		return user;
	}

	public CurrentUser getCurrentUser(CurrentUser user) throws ParseException {
		ParseObject u = getUserByMail(user);
		user.setId(u.getObjectId());
		user.setNick(u.getString(USER_NICKNAME));
		return user;
	}

	public CurrentUser registerIfNotExists(CurrentUser user) throws ParseException {
		ParseObject u;
		try {
			u = getUserByMail(user);
		} catch (ParseException e) {
			u = new ParseObject(USER);
			u.put(USER_EMAIL, user.getEmail());
			u.put(USER_NICKNAME, user.getNick());
			u.save();
		}
		user.setId(u.getObjectId());
		user.setNick(u.getString(USER_NICKNAME));
		return user;
	}

	public void addAsFriend(CurrentUser user, String otherEmail) throws ParseException {
		addFriendship(user, getUserByMail(otherEmail));
	}

	private void addFriendship(User user1, ParseObject user2) {
		ParseObject friendship = new ParseObject(FRIENDSHIP);
		friendship.put(USER_1, ParseObject.createWithoutData(USER, user1.getId()));
		friendship.put(USER_2, user2);
		friendship.saveEventually();
	}

	private void addFriendship(User user1, User user2) {
		ParseObject friendship = new ParseObject(FRIENDSHIP);
		friendship.put(USER_1, ParseObject.createWithoutData(USER, user1.getId()));
		friendship.put(USER_2, ParseObject.createWithoutData(USER, user2.getId()));
		friendship.saveEventually();
	}

	public void retrieveFriends(CurrentUser user) throws ParseException {
		List<ParseObject> parseRelationships = getFriendsQuery(user).find();
		List<User> friends = user.getFriends();
		for (ParseObject parseRelationship : parseRelationships)
			friends.add(getOtherUser(parseRelationship, user));
	}

	private ParseQuery<ParseObject> getFriendsQuery(User user) {
		List<ParseQuery<ParseObject>> or = new ArrayList<ParseQuery<ParseObject>>();
		ParseObject currentUserObject = ParseObject.createWithoutData(USER, user.getId());
		or.add(ParseQuery.getQuery(FRIENDSHIP).whereEqualTo(USER_1, currentUserObject));
		or.add(ParseQuery.getQuery(FRIENDSHIP).whereEqualTo(USER_2, currentUserObject));
		ParseQuery<ParseObject> query = ParseQuery.or(or);
		query.include(USER_1);
		query.include(USER_2);
		return query;
	}

	private User getOtherUser(ParseObject parseRelationship, User current) {
		ParseObject user1 = parseRelationship.getParseObject(USER_1);
		ParseObject user2 = parseRelationship.getParseObject(USER_2);
		ParseObject otherUser = user1.getObjectId().equals(current.getId()) ? user2 : user1;
		return parseUser(otherUser);
	}

	private User parseUser(ParseObject parseUser) {
		return new User(parseUser.getObjectId(), parseUser.getString(USER_EMAIL), parseUser.getString(USER_NICKNAME));
	}

	public void sendInvitation(Invitation invitation) {
		ParseObject i = new ParseObject(INVITATION);
		i.put(INVITATION_MESSAGE, invitation.getMessage());
		i.put(INVITATION_MENSA, invitation.getMensa());
		i.put(INVITATION_FROM, ParseObject.createWithoutData(USER, invitation.getFrom().getId()));
		i.put(INVITATION_TIME, invitation.getTime());
		i.saveEventually();

		for (User u : invitation.getTo()) {
			ParseObject p = new ParseObject(INVITATION_USER);
			p.put(INV_USER_INVITEE, ParseObject.createWithoutData(USER, u.getId()));
			p.put(INV_USER_RESPONSE, invitation.getResponseOf(u).ordinal());
			p.put(INV_USER_INVITATION, i);
			p.saveEventually();
		}
		sendPushNotfication(invitation);
	}

	private void sendPushNotfication(Invitation invitation) {
		LinkedList<String> channels = new LinkedList<String>();
		for (User u : invitation.getTo())
			channels.add("user_" + u.getId());

		ParsePush push = new ParsePush();
		push.setChannels(channels);
		push.setMessage("New invitation from " + invitation.getFrom().getNick() + ": " + invitation.getMessage());
		push.sendInBackground();
	}

	private ParseObject getUserByMail(User user) throws ParseException {
		return getUserByMail(user.getEmail());
	}

	private ParseObject getUserByMail(String email) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER);
		query.whereMatches(USER_EMAIL, email);
		return query.getFirst();
	}

	public List<Invitation> getRetrievedInvitations(User user) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(INVITATION_USER);
		query.whereEqualTo(INV_USER_INVITEE, ParseObject.createWithoutData(USER, user.getId()));
		query.include(INV_USER_INVITATION);
		query.include(INV_USER_INVITATION + "." + INVITATION_FROM);
		List<ParseObject> parseInvitations = query.find();
		List<Invitation> invitations = new ArrayList<Invitation>();
		for (ParseObject invitation : parseInvitations) {
			ParseObject parseInv = invitation.getParseObject(INV_USER_INVITATION);
			ParseObject parseFrom = parseInv.getParseObject(INVITATION_FROM);
			invitations.add(new Invitation(parseUser(parseFrom), new ArrayList<User>(), parseInv
					.getString(INVITATION_MESSAGE), parseInv.getInt(INVITATION_MENSA), parseInv.getDate(INVITATION_TIME))

			);
		}
		return invitations;
	}

	public List<Invitation> getSentInvitations(User user) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(INVITATION);
		query.whereEqualTo(INVITATION_FROM, ParseObject.createWithoutData(USER, user.getId()));
		List<ParseObject> parseInvitations = query.find();
		List<Invitation> invitations = new ArrayList<Invitation>();
		for (ParseObject parseInvite : parseInvitations)
			invitations.add(new Invitation(user, getInvitees(parseInvite), parseInvite.getString(INVITATION_MESSAGE),
					parseInvite.getInt(INVITATION_MENSA), parseInvite.getDate(INVITATION_TIME)));

		return invitations;
	}

	private List<User> getInvitees(ParseObject parseInvite) throws ParseException {
		ParseQuery<ParseObject> inviteesQuery = ParseQuery.getQuery(INVITATION_USER);
		inviteesQuery.whereEqualTo(INV_USER_INVITATION, parseInvite);
		inviteesQuery.include(INV_USER_INVITEE);
		List<ParseObject> parseInvitees = inviteesQuery.find();
		List<User> invitees = new ArrayList<User>();
		for (ParseObject parseUser : parseInvitees)
			invitees.add(parseUser(parseUser.getParseObject(INV_USER_INVITEE)));

		return invitees;
	}

	public void sendFriendRequest(FriendRequest request) {
		ParseObject parseRequest = ParseObject.create(FRIEND_REQUEST);
		parseRequest.put(FRIEND_REQUEST_FROM, ParseObject.createWithoutData(USER, request.getFrom().getId()));
		parseRequest.put(FRIEND_REQUEST_TO, ParseObject.createWithoutData(USER, request.getTo().getId()));
		parseRequest.saveEventually();
	}

	public List<FriendRequest> getFriendRequests(User user) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(FRIEND_REQUEST);
		query.whereEqualTo(FRIEND_REQUEST_TO, ParseObject.createWithoutData(USER, user.getId()));
		query.include(FRIEND_REQUEST_FROM);
		List<ParseObject> parseRequests = query.find();
		List<FriendRequest> requests = new ArrayList<FriendRequest>();
		for (ParseObject parseRequest : parseRequests) {
			requests.add(new FriendRequest(parseRequest.getObjectId(), parseUser(parseRequest
					.getParseObject(FRIEND_REQUEST_FROM)), user));
		}
		return requests;
	}

	public void answerFriendRequest(FriendRequest request, boolean acceptFriendship) throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(FRIEND_REQUEST);
		ParseObject parseRequest = query.get(request.getId());
		if (acceptFriendship)
			addFriendship(request.getTo(), request.getFrom());
		parseRequest.deleteInBackground();
	}
}