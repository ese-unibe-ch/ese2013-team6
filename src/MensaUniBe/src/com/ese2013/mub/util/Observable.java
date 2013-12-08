package com.ese2013.mub.util;

import java.util.ArrayList;

public class Observable {
	ArrayList<Observer> observers = new ArrayList<Observer>();

	public void addObserver(Observer o) {
		observers.add(o);
	}

	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	public void notifyChanges(Object... message) {
		for (Observer o : observers)
			o.onNotifyChanges(message);
	}
}
