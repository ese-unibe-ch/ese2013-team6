package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.SocialManager;
import com.parse.ParseException;

public class AnswerInviteListener implements OnClickListener {
	private Invitation invite;
	private boolean isAccepted;

	public AnswerInviteListener(Invitation invite, boolean isAccepted) {
		this.invite = invite;
		this.isAccepted = isAccepted;
	}

	@Override
	public void onClick(View v) {
		try {
			if(isAccepted)
			SocialManager.getInstance().answerInvitation(invite, Invitation.Response.ACCEPTED);
			else
				SocialManager.getInstance().answerInvitation(invite, Invitation.Response.DECLINED);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
