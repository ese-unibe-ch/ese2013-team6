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
 * Downloads the received invitations of the passed in user asynchronously and
 * makes a callback to the given interface when the download is done.
 */
public class GetInvitationsTask extends AbstractAsyncTask<CurrentUser, Void, List<Invitation>> {

	private GetInvitationsTaskCallback callback;

	public GetInvitationsTask(GetInvitationsTaskCallback callback) {
		this.callback = callback;
	}

	@Override
	protected List<Invitation> doInBackground(CurrentUser... user) {
		try {
			List<Invitation> invitations = new SocialDBHandler().getRetrievedInvitations(user[0]);
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
			callback.onGetInvitationsTaskFinished(result);
		else
			callback.onGetInvitationsTaskFinished(new ArrayList<Invitation>());
	}
}