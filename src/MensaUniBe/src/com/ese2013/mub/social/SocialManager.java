package com.ese2013.mub.social;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.social.Invitation.Response;
import com.ese2013.mub.social.util.GetFriendsTask;
import com.ese2013.mub.social.util.GetFriendsTaskCallback;
import com.ese2013.mub.social.util.GetInvitationsTask;
import com.ese2013.mub.social.util.GetInvitationsTaskCallback;
import com.ese2013.mub.social.util.GetSentInvitationsTask;
import com.ese2013.mub.social.util.GetSentInvitationsTaskCallback;
import com.ese2013.mub.util.Observable;
import com.ese2013.mub.util.parseDatabase.SocialDBHandler;
import com.parse.ParseException;

/**
 * Manages all the social related features. All saving and loading of users,
 * friends etc. should be done using this class (except of login which is done
 * by LoginService). This class is observable and will notice observers of any
 * major state change occurred.
 */
public class SocialManager extends Observable implements GetSentInvitationsTaskCallback, GetFriendsTaskCallback,
		GetInvitationsTaskCallback {
	private List<Invitation> sentInvitations = new ArrayList<Invitation>();
	private List<Invitation> receivedInvitations = new ArrayList<Invitation>();
	private static SocialManager instance;
	private SocialDBHandler onlineDBHandler = new SocialDBHandler();

	private SocialManager() {
	}

	public static SocialManager getInstance() {
		if (instance == null)
			instance = new SocialManager();
		return instance;
	}

	public List<Invitation> getSentInvitations() {
		return sentInvitations;
	}

	public List<Invitation> getReceivedInvitations() {
		return receivedInvitations;
	}

	/**
	 * Sends a friend request to the user with the given email.
	 * 
	 * @param email
	 *            String email of the user to be added. Must not be null.
	 * @throws ParseException
	 *             if no user can't be found or the request can't be sent.
	 */
	public void sendFriendRequest(String email) throws ParseException {
		if (!currentUser().hasFriendWith(email)) {
			User friend = new User(email);
			onlineDBHandler.getUser(friend);
			onlineDBHandler.sendFriendRequest(new FriendRequest(currentUser(), friend));
		}
	}

	/**
	 * Answers a friend request. Does add friend if the request is accepted,
	 * else only the friend request is removed from the server.
	 * 
	 * @param request
	 *            FriendRequest to answer. Must be a FriendRequest which has
	 *            been downloaded from the Parse-Server and has a valid id. Must
	 *            not be null.
	 * @param accept
	 *            true if the FriendRequest should be accepted, false otherwise.
	 */
	public void answerFriendRequest(FriendRequest request, boolean accept) {
		onlineDBHandler.answerFriendRequest(request, accept);
		currentUser().removeFriendRequest(request);
		if (accept)
			currentUser().addFriend(request.getFrom());
		notifyChanges();
	}

	public void removeFriend(User user) {
		onlineDBHandler.removeFriendship(currentUser(), user);
		currentUser().removeFriend(user);
		notifyChanges();
	}

	/**
	 * Answers an Invitation.
	 * 
	 * @param invitation
	 *            Invitation to be answered. Must have a valid id from the
	 *            Parse-Server and not be null.
	 * @param response
	 *            Response to send. Must not be null and must not be "UNKNOWN".
	 */
	public void answerInvitation(Invitation invitation, Response response) {
		if (response == Response.DECLINED)
			receivedInvitations.remove(invitation);
		invitation.setResponseOf(currentUser(), response);
		onlineDBHandler.answerInvitation(invitation, response, currentUser());
		notifyChanges();
	}

	/**
	 * Loads all invitations (both sent and received) and the friends list.
	 */
	public void load() {
		loadInvites();
		loadSentInvites();
		loadFriends();
	}

	/**
	 * Asynchronously loads the received invitations if the user is logged in.
	 */
	public void loadInvites() {
		if (LoginService.isLoggedIn())
			new GetInvitationsTask(this).execute(LoginService.getLoggedInUser());
	}

	/**
	 * Asynchronously loads the sent invitations if the user is logged in.
	 */
	public void loadSentInvites() {
		if (LoginService.isLoggedIn())
			new GetSentInvitationsTask(this).execute(LoginService.getLoggedInUser());
	}

	/**
	 * Asynchronously loads the friends list if the user is logged in.
	 */
	public void loadFriends() {
		if (LoginService.isLoggedIn())
			new GetFriendsTask(this).execute(LoginService.getLoggedInUser());
	}

	@Override
	public void onGetInvitationsTaskFinished(List<Invitation> invitations) {
		receivedInvitations = invitations;
		notifyChanges();
	}

	@Override
	public void onGetSentInvitationsTaskFinished(List<Invitation> invitations) {
		sentInvitations = invitations;
		notifyChanges();
	}

	@Override
	public void onGetFriendsTaskFinished() {
		notifyChanges();
	}

	/**
	 * Sends the given Invitation to all receivers.
	 * 
	 * @param invitation
	 *            Invitation to be sent. Must not be null and contain at least
	 *            one receiver.
	 */
	public void sendInvitation(Invitation invitation) {
		onlineDBHandler.sendInvitation(invitation);
	}

	private CurrentUser currentUser() {
		return LoginService.getLoggedInUser();
	}
}