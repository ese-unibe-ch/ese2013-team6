package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.SocialManager;
import com.ese2013.mub.social.User;
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
