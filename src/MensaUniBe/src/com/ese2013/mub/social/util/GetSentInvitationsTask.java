package com.ese2013.mub.social.util;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.social.CurrentUser;
import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.util.AbstractAsyncTask;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class GetSentInvitationsTask extends AbstractAsyncTask<CurrentUser, Void, List<Invitation>> {

	private GetSentInvitationsTaskCallback callback;

	public GetSentInvitationsTask(GetSentInvitationsTaskCallback callback) {
		this.callback = callback;
	}

	@Override
	protected List<Invitation> doInBackground(CurrentUser... user) {
		try {
			return new OnlineDBHandler().getSentInvitations(user[0]);
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