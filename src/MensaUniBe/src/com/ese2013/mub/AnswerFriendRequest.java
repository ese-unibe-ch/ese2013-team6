package com.ese2013.mub;

import com.ese2013.mub.social.FriendRequest;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

import android.view.View;
import android.view.View.OnClickListener;

public class AnswerFriendRequest implements OnClickListener {
	private FriendRequest friendRequest;
	public AnswerFriendRequest(FriendRequest friendRequest, boolean accept) {
		this.friendRequest = friendRequest;
	}
	@Override
	public void onClick(View v) {
		try {
			new OnlineDBHandler().answerFriendRequest(friendRequest, false);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
