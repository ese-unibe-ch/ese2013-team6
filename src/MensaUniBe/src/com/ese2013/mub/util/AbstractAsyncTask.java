package com.ese2013.mub.util;

import android.os.AsyncTask;
import android.util.Log;

public abstract class AbstractAsyncTask<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
	private Exception exception;

	public Exception getException() {
		return exception;
	}

	protected void setException(Exception e) {
		exception = e;
	}

	public boolean hasSucceeded() {
		return exception == null;
	}

	public void logException(String tag, String msg) {
		Log.e(tag, msg, exception);
	}
}