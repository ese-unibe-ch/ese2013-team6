package com.ese2013.mub.util;

/**
 * Interface to be implemented in any class which needs to observe an
 * Observable.
 * 
 */
public interface Observer {
	public void onNotifyChanges(Object... message);
}
