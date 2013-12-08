package com.ese2013.mub.social.util;

import java.util.List;

import com.ese2013.mub.social.Invitation;

/**
 * Interface to implement for the callback from the GetInvitationsTask.
 */
public interface GetInvitationsTaskCallback {
	public void onGetInvitationsTaskFinished(List<Invitation> invitations);
}
