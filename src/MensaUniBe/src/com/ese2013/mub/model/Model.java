package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.ese2013.mub.util.ModelCreationTask;
import com.ese2013.mub.util.ModelCreationTaskCallback;
import com.ese2013.mub.util.ModelSavingTask;
import com.ese2013.mub.util.Observable;
import com.ese2013.mub.util.Preferences;
import com.ese2013.mub.util.TranslationTask;
import com.ese2013.mub.util.TranslationTaskCallback;
import com.ese2013.mub.util.database.MensaDataSource;
import com.memetix.mst.language.Language;

/**
 * Manages the loading and storing of the whole model. This class holds the list
 * of all Mensas. It also initializes and updates this list. If the list of
 * Mensas is updated, all Observers are notified (e.g. GUI classes).
 */
public class Model extends Observable implements ModelCreationTaskCallback, TranslationTaskCallback {
	private List<Mensa> mensas = new ArrayList<Mensa>();
	private MenuManager menuManager;
	private static Model instance;
	private Context context;
	private MensaDataSource dataSource;

	public Model(Context context) {
		Model.instance = this;
		this.context = context;
		init();
	}

	private void init() {
		menuManager = new MenuManager();
		menuManager.setTranslationsEnabled(new Preferences().getDoTranslation(context));
		dataSource = MensaDataSource.getInstance();
		dataSource.init(context, menuManager);

		ModelCreationTask task = new ModelCreationTask(menuManager, dataSource, this);
		task.execute();
	}

	public List<Mensa> getMensas() {
		return mensas;
	}

	public MenuManager getMenuManager() {
		return menuManager;
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
		return !mensasLoaded();
	}

	public boolean mensasLoaded() {
		return mensas.size() > 0;
	}

	public boolean favoritesExist() {
		for (Mensa m : mensas)
			if (m.isFavorite())
				return true;
		return false;
	}

	public void saveFavorites() {
		dataSource.open();
		dataSource.storeFavorites(mensas);
		dataSource.close();
	}

	@Override
	public void onTaskFinished(ModelCreationTask task) {
		Toast.makeText(context, context.getString(task.getStatusMsgResource()), Toast.LENGTH_LONG).show();
		if (task.wasSuccessful()) {
			mensas = task.getMensas();
			if (menuManager.isTranslationEnabled() && !menuManager.translationsAvailable()) {
				System.out.println("dooing translation");
				new TranslationTask(menuManager, Language.ENGLISH, this).execute();
			}

			if (task.hasDownloadedNewData())
				saveModel();
			notifyChanges();
		}
	}

	public void onTranslationFinised(TranslationTask task) {
		System.out.println("translation done");
		notifyChanges();
	}

	public void saveModel() {
		ModelSavingTask savingTask = new ModelSavingTask();
		savingTask.execute();
	}

	@Override
	public void onTaskFinished(TranslationTask task) {
		// TODO Auto-generated method stub

	}
}
