package com.ese2013.mub.social.util;

import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.util.AbstractAsyncTask;
import com.parse.ParseException;

/**
 * Asynchronously tries to login the user with the given email and makes a
 * callback to the given interface once it's done. This task does directly
 * change the logged in user, so the caller should be careful and not launch
 * more than one login task at the same time if possible.
 */
public class LoginTask extends AbstractAsyncTask<String, Void, Void> {
	private LoginTaskCallback callback;

	public LoginTask(LoginTaskCallback callback) {
		this.callback = callback;
	}

	@Override
	protected Void doInBackground(String... email) {
		try {
			LoginService.loginSync(email[0]);
		} catch (ParseException e) {
			setException(e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
		super.onPostExecute(v);
		callback.onTaskFinished(this);
	}
}
