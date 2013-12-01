package com.ese2013.mub.social;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.AsyncTask;

import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class LoginService {
	private static CurrentUser loggedInUser;
	private static OnlineDBHandler handler = new OnlineDBHandler();

	public static boolean login(CurrentUser user) {
		try {
			loggedInUser = new AsyncTask<CurrentUser, Void, CurrentUser>() {
				@Override
				protected CurrentUser doInBackground(CurrentUser... user) {
					try {
						return handler.loginUser(user[0]);
					} catch (ParseException e) {
						return null;
					}
				}
			}.execute(user).get(2, TimeUnit.SECONDS);
			return true;
		} catch (InterruptedException e) {
			return false;
		} catch (ExecutionException e) {
			return false;
		} catch (TimeoutException e) {
			return false;
		}
	}

	public static boolean registerAndLogin(CurrentUser user) {
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
			}.execute(user).get(2, TimeUnit.SECONDS);
			return true;
		} catch (InterruptedException e) {
			return false;
		} catch (ExecutionException e) {
			return false;
		} catch (TimeoutException e) {
			return false;
		}
	}

	public static CurrentUser getLoggedInUser() {
		return loggedInUser;
	}

	public static boolean isLoggedIn() {
		return loggedInUser != null;
	}
}
