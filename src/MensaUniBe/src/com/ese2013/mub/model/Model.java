package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.Toast;

/**
 * Manages the loading and storing of the whole model. This class holds the list
 * of all Mensas. It also initializes and updates this list. If the list of
 * Mensas is updated, all Observers are notified (e.g. GUI classes).
 */
public class Model extends Observable implements LoaderManager.LoaderCallbacks<List<Mensa>> {
	private List<Mensa> mensas = new ArrayList<Mensa>();
	private static Model instance;
	private FragmentActivity activity;
	private static final int LOADER_ID = 1;

	private LocalDataUpdaterTask updater;
	private boolean localDataFresh = false;

	public Model(FragmentActivity activity) {
		Model.instance = this;
		this.activity = activity;
		new DataManager(activity);
		activity.getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	public List<Mensa> getMensas() {
		return mensas;
	}

	public List<Mensa> getFavoriteMensas() {
		List<Mensa> ret = new ArrayList<Mensa>(3);
		for (Mensa m : mensas)
			if (m.isFavorite())
				ret.add(m);
		return ret;
	}

	public void destroy() {
		updater.cancel(true);
		activity.getSupportLoaderManager().destroyLoader(LOADER_ID);
	}

	// TODO the toasts shouldn't be handled here!
	public void onWebContentRetrieved(boolean success) {
		if (success) {
			activity.getSupportLoaderManager().getLoader(LOADER_ID).onContentChanged();
			Toast.makeText(activity, "Downloaded new data", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(activity, "Could not download new data", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public Loader<List<Mensa>> onCreateLoader(int id, Bundle args) {
		return new ModelLoader(activity);
	}

	@Override
	public void onLoadFinished(Loader<List<Mensa>> loader, List<Mensa> mensas) {
		if (mensas == null) {// TODO Loader should handle failed loading.
			updateLocalData();
			return;
		}
		this.mensas = new ArrayList<Mensa>(mensas);
		if (!localDataFresh) {
			updateLocalData();
		}
		notifyChanges();
	}

	@Override
	public void onLoaderReset(Loader<List<Mensa>> loader) {
	}

	public static Model getInstance() {
		return instance;
	}

	public boolean noMensasLoaded() {
		return mensas.size() == 0;
	}

	private void updateLocalData() {
		updater = new LocalDataUpdaterTask();
		updater.execute();
		localDataFresh = true;
	}

	public void saveLocalData() {
		DataManager.getSingleton().storeFavorites(mensas);
	}
}
