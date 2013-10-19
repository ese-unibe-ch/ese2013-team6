package com.ese2013.mub.model;

import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

public class Model extends Observable implements LoaderManager.LoaderCallbacks<List<Mensa>>{
	private ArrayList<Mensa> mensas = new ArrayList<Mensa>();
	private static Model instance;
	private FragmentActivity activity;
	private static final int LOADER_ID = 1;
	public Model(FragmentActivity activity) {
		Model.instance = this;
		this.activity = activity;
		init();
	}

	private void init() {
		//start downloading data after everything else created
		Log.d("Model", "init local data source loading");

		new DataManager(activity);
		LocalDataUpdater updater = new LocalDataUpdater();
		updater.execute();
		
		Log.d("Model", "creating loader");
		activity.getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
	public ArrayList<Mensa> getMensas() {
		return mensas;
	}
	
	public static Model getInstance() {
		assertNotNull(instance);
		return instance;
	}
	
	@Override
	public Loader<List<Mensa>> onCreateLoader(int arg0, Bundle arg1) {
		return new ModelLoader(activity);
	}
	@Override
	public void onLoadFinished(Loader<List<Mensa>> loader, List<Mensa> mensas) {
		Log.d("Model", "LOADER DONE");
		this.mensas = new ArrayList<Mensa>(mensas);
		notifyChanges();
		

	}
	@Override
	public void onLoaderReset(Loader<List<Mensa>> arg0) {
	}
}
