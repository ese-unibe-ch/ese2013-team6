package com.ese2013.mub;

import java.util.Collection;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.User;

public class ShowInvitedListener implements OnClickListener {
	private Invitation invite;
	
	public ShowInvitedListener(Invitation invite) {
		this.invite = invite;
	}
	@Override
	public void onClick(View v) {
		Collection<User> invitedCollection = invite.getTo();

	}

}
