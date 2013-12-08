package com.ese2013.mub.util;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.social.CurrentUser;
import com.ese2013.mub.social.FriendRequest;
import com.ese2013.mub.social.User;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class GetFriendsTask extends AbstractAsyncTask<CurrentUser, Void, Void> {

	private GetFriendsTaskCallback callback;
	private List<FriendRequest> requests;
	private List<User> friends;
	private CurrentUser currentUser;

	public GetFriendsTask(GetFriendsTaskCallback callback) {
		this.callback = callback;
	}

	@Override
	protected Void doInBackground(CurrentUser... user) {
		try {
			currentUser = user[0];
			requests = new OnlineDBHandler().getFriendRequests(currentUser);
			friends = new OnlineDBHandler().getFriends(currentUser);
		} catch (ParseException e) {
			requests = new ArrayList<FriendRequest>();
			friends = new ArrayList<User>();
			setException(e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		currentUser.setFriends(friends);
		currentUser.setFriendRequests(requests);
		callback.onFriendsTaskFinished();
	}
}