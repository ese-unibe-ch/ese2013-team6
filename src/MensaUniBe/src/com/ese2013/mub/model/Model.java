package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.ese2013.mub.util.DataManager;
import com.ese2013.mub.util.ModelCreationTask;
import com.ese2013.mub.util.ModelSavingTask;
import com.ese2013.mub.util.Observable;

/**
 * Manages the loading and storing of the whole model. This class holds the list
 * of all Mensas. It also initializes and updates this list. If the list of
 * Mensas is updated, all Observers are notified (e.g. GUI classes).
 */
public class Model extends Observable {
	private List<Mensa> mensas = new ArrayList<Mensa>();
	private static Model instance;
	private FragmentActivity activity;

	public Model(FragmentActivity activity) {
		Model.instance = this;
		this.activity = activity;
		init();
	}

	private void init() {
		new DataManager(activity);
		ModelCreationTask task = new ModelCreationTask();
		task.execute();
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

	public static Model getInstance() {
		return instance;
	}

	public boolean noMensasLoaded() {
		return mensas.size() == 0;
	}

	public void saveFavorites() {
		DataManager.getInstance().storeFavorites(mensas);
	}

	public void onCreationFinished(ModelCreationTask task) {
		if (task.wasSuccessful()) {
			this.mensas = task.getMensas();
			if (task.hasDownloadedNewData()) {
				Toast.makeText(activity, "Downloaded new menus!", Toast.LENGTH_LONG).show();
				saveModel();
			} else {
				Toast.makeText(activity, "Menus up to date.", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(activity, "Could not load Menus.", Toast.LENGTH_LONG).show();
		}
		this.notifyChanges();
	}

	private void saveModel() {
		ModelSavingTask savingTask = new ModelSavingTask();
		savingTask.execute();
	}
}
