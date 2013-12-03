package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.SocialManager;
import com.parse.ParseException;

public class CancelInviteRequest implements OnClickListener {
	private Invitation invite;
	public CancelInviteRequest(Invitation invite) {
		this.invite = invite;
	}
	@Override
	public void onClick(View arg0) {
		try {
			SocialManager.getInstance().answerInvitation(invite, Invitation.Response.DECLINED);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
