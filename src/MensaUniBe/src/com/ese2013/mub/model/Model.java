package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.ese2013.mub.util.ModelCreationTask;
import com.ese2013.mub.util.ModelSavingTask;
import com.ese2013.mub.util.Observable;
import com.ese2013.mub.util.database.MensaDataSource;

/**
 * Manages the loading and storing of the whole model. This class holds the list
 * of all Mensas. It also initializes and updates this list. If the list of
 * Mensas is updated, all Observers are notified (e.g. GUI classes).
 */
public class Model extends Observable {
	private List<Mensa> mensas = new ArrayList<Mensa>();
	private static Model instance;
	private Context context;
	private MensaDataSource dataSource;

	public Model(Context context) {
		Model.instance = this;
		this.context = context;
		init();
	}

	private void init() {
		dataSource = new MensaDataSource(context);
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
		dataSource.open();
		dataSource.storeFavorites(mensas);
		dataSource.close();
	}

	public void onCreationFinished(ModelCreationTask task) {
		if (task.wasSuccessful()) {
			this.mensas = task.getMensas();
			if (task.hasDownloadedNewData()) {
				Toast.makeText(context, "Downloaded new menus!", Toast.LENGTH_LONG).show();
				saveModel();
			} else {
				Toast.makeText(context, "Menus up to date.", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(context, "Could not load Menus.", Toast.LENGTH_LONG).show();
		}
		this.notifyChanges();
	}

	private void saveModel() {
		ModelSavingTask savingTask = new ModelSavingTask();
		savingTask.execute();
	}
}
