package com.ese2013.mub;

import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

import android.view.View;
import android.view.View.OnClickListener;

public class CancelInviteRequest implements OnClickListener {
	private Invitation invite;
	public CancelInviteRequest(Invitation invite) {
		this.invite = invite;
	}
	@Override
	public void onClick(View arg0) {
		try {
			new OnlineDBHandler().answerInvitation(invite, Invitation.Response.DECLINED, LoginService.getLoggedInUser());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
