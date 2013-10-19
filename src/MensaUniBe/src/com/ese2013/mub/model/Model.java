package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.Toast;

public class Model extends Observable implements LoaderManager.LoaderCallbacks<List<Mensa>> {
	private ArrayList<Mensa> mensas = new ArrayList<Mensa>();
	private static Model instance;
	private FragmentActivity activity;
	private static final int LOADER_ID = 1;

	public Model(FragmentActivity activity) {
		Model.instance = this;
		this.activity = activity;
		new DataManager(activity);
		init();
	}

	private void init() {
		activity.getSupportLoaderManager().initLoader(LOADER_ID, null, this);

		LocalDataUpdaterTask updater = new LocalDataUpdaterTask();
		updater.execute();

	}

	public ArrayList<Mensa> getMensas() {
		return mensas;
	}

	// TODO the toasts shouldnt be handled here!
	public void onWebContentRetrieved(boolean success) {
		if (success) {
			activity.getSupportLoaderManager().getLoader(LOADER_ID).onContentChanged();
			Toast.makeText(activity, "Downloaded new data", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(activity, "Could not download new data", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public Loader<List<Mensa>> onCreateLoader(int arg0, Bundle arg1) {
		return new ModelLoader(activity);
	}

	@Override
	public void onLoadFinished(Loader<List<Mensa>> loader, List<Mensa> mensas) {
		this.mensas = new ArrayList<Mensa>(mensas);
		notifyChanges();
	}

	@Override
	public void onLoaderReset(Loader<List<Mensa>> arg0) {
	}

	public static Model getInstance() {
		return instance;
	}
}
