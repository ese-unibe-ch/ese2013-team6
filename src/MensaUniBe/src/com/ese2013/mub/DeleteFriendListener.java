package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.User;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;

public class DeleteFriendListener implements OnClickListener {
	private User friend;
	private OnlineDBHandler onlineDBHandler = new OnlineDBHandler();

	public DeleteFriendListener(User friend) {
		this.friend = friend;
	}

	@Override
	public void onClick(View v) {
		onlineDBHandler.removeFriendship(LoginService.getLoggedInUser(), friend);
	}
}
