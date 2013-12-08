package com.ese2013.mub.util;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Extension of the standard AsyncTask from the Android framework. Mainly makes
 * exception handling easier.
 */
public abstract class AbstractAsyncTask<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
	private Exception exception;

	public Exception getException() {
		return exception;
	}

	/**
	 * Sets the exception. This means the class which extends AbstractAsyncTask
	 * can call this to keep an occurred exception for later handling.
	 * 
	 * @param e
	 *            Exception to be stored.
	 */
	protected void setException(Exception e) {
		exception = e;
	}

	/**
	 * Returns if the task has succeeded by checking if an exception has been
	 * set.
	 * 
	 * @return true if the task has succeeded, false otherwise.
	 */
	public boolean hasSucceeded() {
		return exception == null;
	}

	public void logException(String tag, String msg) {
		Log.e(tag, msg, exception);
	}
}