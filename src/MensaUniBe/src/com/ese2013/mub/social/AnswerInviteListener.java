package com.ese2013.mub.social;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.model.Invitation;
import com.ese2013.mub.social.model.SocialManager;

/**
<<<<<<< HEAD
 * Listener for the Invite answerButtons, works for accept and decline requests
 * 
=======
 * Listener for the Invite answerButtons,
 * Works for accepting and declining InvitationRequests
 *
>>>>>>> 9824c229e777ab8b58ad4d9cde344749a5ff0480
 */
public class AnswerInviteListener implements OnClickListener {
	private Invitation invite;
	private boolean isAccepted;

	public AnswerInviteListener(Invitation invite, boolean isAccepted) {
		this.invite = invite;
		this.isAccepted = isAccepted;
	}

	@Override
	public void onClick(View v) {
		if (isAccepted)
			SocialManager.getInstance().answerInvitation(invite, Invitation.Response.ACCEPTED);
		else
			SocialManager.getInstance().answerInvitation(invite, Invitation.Response.DECLINED);
	}
}
