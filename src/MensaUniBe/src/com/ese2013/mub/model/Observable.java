package com.ese2013.mub.model;

import java.util.ArrayList;

public class Observable {
	ArrayList<Observer> observers  = new ArrayList<Observer>();
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void notifyChanges() {
		for (Observer o : observers) 
			o.onNotifyChanges();
	}
}
