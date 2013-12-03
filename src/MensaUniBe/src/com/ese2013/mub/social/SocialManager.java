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

public class SocialManager extends Observable implements GetInvitationsTaskCallback, GetSentInvitationsTaskCallback,
		GetFriendsTaskCallback {
	private static SocialManager instance;

	public static SocialManager getInstance() {
		if (instance == null)
			instance = new SocialManager();
		return instance;
	}

	public void answerFriendRequest(FriendRequest request, boolean accept) {
		new OnlineDBHandler().answerFriendRequest(request, accept);
		currentUser().getFriendRequests().remove(request);
		if (accept)
			currentUser().getFriends().add(request.getFrom());
	}

	private CurrentUser currentUser() {
		return LoginService.getLoggedInUser();
	}

	public void removeFriend(User user) {
		new OnlineDBHandler().removeFriendship(currentUser(), user);
		currentUser().getFriends().remove(user);
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
		notifyChanges();
	}

	@Override
	public void onTaskFinished(List<Invitation> invitations) {
		notifyChanges();
	}

	@Override
	public void onFriendsTaskFinished() {
		notifyChanges();
	}
}
