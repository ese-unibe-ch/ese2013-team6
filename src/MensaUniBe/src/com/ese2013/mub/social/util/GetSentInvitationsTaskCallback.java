package com.ese2013.mub.social.util;

import java.util.List;

import com.ese2013.mub.social.Invitation;

public interface GetSentInvitationsTaskCallback {
	public void onGetSentInvitationsTaskFinished(List<Invitation> invitations);
}
