package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.SocialManager;
import com.parse.ParseException;

public class AcceptInviteListener implements OnClickListener {
	private Invitation invite;

	public AcceptInviteListener(Invitation invite) {
		this.invite = invite;
	}

	@Override
	public void onClick(View v) {
		try {
			SocialManager.getInstance().answerInvitation(invite,
					Invitation.Response.ACCEPTED);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
