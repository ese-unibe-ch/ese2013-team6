package com.ese2013.mub.social.util;

import java.util.List;

import com.ese2013.mub.social.Invitation;

/**
 * Interface to implement for the callback from the GetSentInvitationsTask.
 */
public interface GetSentInvitationsTaskCallback {
	public void onGetSentInvitationsTaskFinished(List<Invitation> invitations);
}
