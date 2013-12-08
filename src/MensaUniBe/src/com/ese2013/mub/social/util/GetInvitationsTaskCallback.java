package com.ese2013.mub.social.util;

import java.util.List;

import com.ese2013.mub.social.Invitation;

public interface GetInvitationsTaskCallback {
	public void onGetInvitationsTaskFinished(List<Invitation> invitations);
}
