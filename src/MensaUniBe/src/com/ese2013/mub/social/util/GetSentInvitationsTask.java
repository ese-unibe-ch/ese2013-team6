package com.ese2013.mub.social.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ese2013.mub.social.CurrentUser;
import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.util.AbstractAsyncTask;
import com.ese2013.mub.util.parseDatabase.SocialDBHandler;
import com.parse.ParseException;

/**
 * Downloads the sent invitations of the passed in user asynchronously and makes
 * a callback to the given interface when the download is done.
 */
public class GetSentInvitationsTask extends AbstractAsyncTask<CurrentUser, Void, List<Invitation>> {

	private GetSentInvitationsTaskCallback callback;

	public GetSentInvitationsTask(GetSentInvitationsTaskCallback callback) {
		this.callback = callback;
	}

	@Override
	protected List<Invitation> doInBackground(CurrentUser... user) {
		try {
			List<Invitation> invitations = new SocialDBHandler().getSentInvitations(user[0]);
			Collections.sort(invitations);
			return invitations;
		} catch (ParseException e) {
			setException(e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<Invitation> result) {
		super.onPostExecute(result);
		if (hasSucceeded())
			callback.onGetSentInvitationsTaskFinished(result);
		else
			callback.onGetSentInvitationsTaskFinished(new ArrayList<Invitation>());
	}
}