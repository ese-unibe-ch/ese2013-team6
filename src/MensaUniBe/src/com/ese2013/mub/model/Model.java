package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.util.DataManager;
import com.ese2013.mub.util.LocalDataUpdaterTask;
import com.ese2013.mub.util.ModelLoader;
import com.ese2013.mub.util.Observable;

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

	public Model(FragmentActivity activity) {
		Model.instance = this;
		this.activity = activity;
		new DataManager(activity);
		updateLocalData();
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

	// TODO the toasts shouldn't be handled here!
	public void onLocalDataUpdateFinished(boolean success, boolean downloadedNewData) {
		activity.getSupportLoaderManager().initLoader(LOADER_ID, null, this);
		activity.getSupportLoaderManager().getLoader(LOADER_ID).onContentChanged();
		if (success) {
			if (downloadedNewData)
				Toast.makeText(activity, "Downloaded new menus!", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(activity, "Menus up to date.", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(activity, "No new menus were downloaded", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public Loader<List<Mensa>> onCreateLoader(int id, Bundle args) {
		return new ModelLoader(activity);
	}

	@Override
	public void onLoadFinished(Loader<List<Mensa>> loader, List<Mensa> mensas) {
		if (mensas == null) {// TODO Loader should handle failed loading.
			//updateLocalData();
			return;
		}
		this.mensas = mensas;
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
		LocalDataUpdaterTask updater = new LocalDataUpdaterTask();
		updater.execute();
	}

	public void saveFavorites() {
		DataManager.getSingleton().storeFavorites(mensas);
	}
}
