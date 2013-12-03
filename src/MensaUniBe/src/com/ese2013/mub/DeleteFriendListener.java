package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.SocialManager;
import com.ese2013.mub.social.User;

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
