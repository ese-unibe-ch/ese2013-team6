package com.ese2013.mub.social;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.model.SocialManager;
import com.ese2013.mub.social.model.User;

/**
 * Listener for the FriendsList friend field delete a friend button
 * 
 */
public class DeleteFriendListener implements OnClickListener {
	private User friend;

	public DeleteFriendListener(User friend) {
		this.friend = friend;
	}

	@Override
	public void onClick(View v) {
		SocialManager.getInstance().removeFriend(friend);
	}
}
