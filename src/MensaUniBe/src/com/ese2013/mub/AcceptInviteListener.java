package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class AcceptInviteListener implements OnClickListener {
	private Invitation invite;
	public AcceptInviteListener(Invitation invite) {
		this.invite = invite;
	}
	@Override
	public void onClick(View v) {
		try {
			new OnlineDBHandler().answerInvitation(invite, Invitation.Response.ACCEPTED, LoginService.getLoggedInUser());
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
