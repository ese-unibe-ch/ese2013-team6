package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.User;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;



public class DeleteFriendListener implements OnClickListener {
	private User friend;
	private OnlineDBHandler onlineDBHandler;
	public DeleteFriendListener(User friend) {
		this.friend = friend;
	}

	@Override
	public void onClick(View v) {
		try {
			onlineDBHandler.getUser(friend);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
