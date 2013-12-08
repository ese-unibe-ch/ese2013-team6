package com.ese2013.mub.util;

import java.util.ArrayList;

/**
 * This class should be extended by any class which should be able to be
 * observed by classes implementing the Observer interface. Allows to add and
 * remove observers and notify changes to observers.
 * 
 */
public class Observable {
	private ArrayList<Observer> observers = new ArrayList<Observer>();

	/**
	 * Adds an observer,
	 * 
	 * @param observer
	 *            Observer to be added. Must not be null.
	 */
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	/**
	 * Removes an observer if possible.
	 * 
	 * @param observer
	 *            Observer to be removed.
	 */
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	/**
	 * Notifies Observers which have been added of any changes.
	 * 
	 * @param message
	 *            Any Object(s) can be passed to the Observers.
	 */
	public void notifyChanges(Object... message) {
		for (Observer o : observers)
			o.onNotifyChanges(message);
	}
}
