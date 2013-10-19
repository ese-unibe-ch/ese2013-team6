package com.ese2013.mub.model;

import java.util.ArrayList;

import android.util.Log;

public class Observable {
	ArrayList<Observer> observers  = new ArrayList<Observer>();
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void notifyChanges() {
		Log.d("Observer", "notifying");
		for (Observer o : observers) 
			o.onNotifyChanges();
	}
}
