package com.ese2013.mub.social;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.AsyncTask;

import com.ese2013.mub.util.parseDatabase.SocialDBHandler;
import com.parse.ParseException;

/**
 * Allows to login (and register a user). This can be done synchronously without
 * and with a given timeout. The logged in user is also stored by this class and
 * can be retrieved by using "getLoggedInUser()".
 * 
 */
public class LoginService {
	private static CurrentUser loggedInUser;
	private static SocialDBHandler handler = new SocialDBHandler();

	/**
	 * Tries to log in the user with the given email.
	 * 
	 * @param email
	 *            String email address of the user to login. Must not be null.
	 * @throws ParseException
	 *             if the User can't be found or the Parse-Server is not
	 *             available.
	 */
	public static void loginSync(String email) throws ParseException {
		CurrentUser user = new CurrentUser(email);
		loggedInUser = handler.getCurrentUser(user);
	}

	/**
	 * Tries to login the user with the given email in the given time.
	 * 
	 * @param email
	 *            String email address of the user to login. Must not be null.
	 * @param timeoutSeconds
	 *            Time to wait for the login.
	 * @return true if the user could be logged in during the given time. False
	 *         if the time wasn't enough to login.
	 */
	public static boolean loginSyncWithTimeout(String email, int timeoutSeconds) {
		CurrentUser user = new CurrentUser(email);
		try {
			loggedInUser = new AsyncTask<CurrentUser, Void, CurrentUser>() {
				@Override
				protected CurrentUser doInBackground(CurrentUser... user) {
					try {
						return handler.getCurrentUser(user[0]);
					} catch (ParseException e) {
						return null;
					}
				}
			}.execute(user).get(timeoutSeconds, TimeUnit.SECONDS);
			return loggedInUser != null;
		} catch (InterruptedException e) {
			return false;
		} catch (ExecutionException e) {
			return false;
		} catch (TimeoutException e) {
			return false;
		}
	}

	/**
	 * Tries to register and login the user with the given email in the given
	 * time.
	 * 
	 * @param email
	 *            String email address of the user to login/register. Must not
	 *            be null.
	 * @param timeoutSeconds
	 *            Time to wait for the login.
	 * @return true if the user could be registred/logged in during the given
	 *         time. False if the time wasn't enough to login.
	 */
	public static boolean registerAndLoginWithTimout(CurrentUser user, int timoutSeconds) {
		try {
			loggedInUser = new AsyncTask<CurrentUser, Void, CurrentUser>() {
				@Override
				protected CurrentUser doInBackground(CurrentUser... user) {
					try {
						return handler.registerIfNotExists(user[0]);
					} catch (ParseException e) {
						return null;
					}
				}
			}.execute(user).get(timoutSeconds, TimeUnit.SECONDS);
			return loggedInUser != null;
		} catch (InterruptedException e) {
			return false;
		} catch (ExecutionException e) {
			return false;
		} catch (TimeoutException e) {
			return false;
		}
	}

	/**
	 * Returns the currently logged in user. Can be null if user is not logged
	 * in.
	 * 
	 * @return CurrentUser which is logged in. Can be null if the login has
	 *         failed or has never been tried.
	 */
	public static CurrentUser getLoggedInUser() {
		return loggedInUser;
	}

	public static boolean isLoggedIn() {
		return loggedInUser != null;
	}
}
