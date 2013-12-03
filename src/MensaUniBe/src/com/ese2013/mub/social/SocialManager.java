package com.ese2013.mub.social;

import java.util.List;

import com.ese2013.mub.util.GetFriendsTask;
import com.ese2013.mub.util.GetFriendsTaskCallback;
import com.ese2013.mub.util.GetInvitationsTask;
import com.ese2013.mub.util.GetInvitationsTaskCallback;
import com.ese2013.mub.util.GetSentInvitationsTask;
import com.ese2013.mub.util.GetSentInvitationsTaskCallback;
import com.ese2013.mub.util.Observable;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class SocialManager extends Observable implements GetSentInvitationsTaskCallback, GetFriendsTaskCallback,
		GetInvitationsTaskCallback {
	private List<Invitation> sentInvitations;
	private List<Invitation> receivedInvitations;
	private static SocialManager instance;
	private OnlineDBHandler onlineDBHandler = new OnlineDBHandler();

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

	public void sendFriendRequest(String email) throws ParseException {
		if (!currentUser().hasFriendWith(email)) {
			User friend = new User(email);
			onlineDBHandler.getUser(friend);
			sendFriendRequest(new FriendRequest(currentUser(), friend));
		}
	}

	public void sendFriendRequest(FriendRequest request) {
		onlineDBHandler.sendFriendRequest(request);
	}

	public void answerFriendRequest(FriendRequest request, boolean accept) {
		onlineDBHandler.answerFriendRequest(request, accept);
		currentUser().getFriendRequests().remove(request);
		if (accept)
			currentUser().getFriends().add(request.getFrom());
		notifyChanges();
	}

	private CurrentUser currentUser() {
		return LoginService.getLoggedInUser();
	}

	public void removeFriend(User user) {
		onlineDBHandler.removeFriendship(currentUser(), user);
		currentUser().getFriends().remove(user);
		notifyChanges();
	}

	public void loadInvites() {
		if (LoginService.isLoggedIn())
			new GetInvitationsTask(this).execute(LoginService.getLoggedInUser());
	}

	public void loadSentInvites() {
		if (LoginService.isLoggedIn())
			new GetSentInvitationsTask(this).execute(LoginService.getLoggedInUser());
	}

	public void loadFriends() {
		if (LoginService.isLoggedIn())
			new GetFriendsTask(this).execute(LoginService.getLoggedInUser());
	}

	@Override
	public void onInvitesTaskFinished(List<Invitation> invitations) {
		receivedInvitations = invitations;
		notifyChanges();
	}

	@Override
	public void onTaskFinished(List<Invitation> invitations) {
		sentInvitations = invitations;
		notifyChanges();
	}

	@Override
	public void onFriendsTaskFinished() {
		notifyChanges();
	}

	public void sendInvitation(Invitation invitation) {
		onlineDBHandler.sendInvitation(invitation);
	}
}
