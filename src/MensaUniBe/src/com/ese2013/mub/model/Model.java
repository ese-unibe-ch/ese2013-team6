package com.ese2013.mub.model;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.util.ModelCreationTask;
import com.ese2013.mub.util.ModelCreationTaskCallback;
import com.ese2013.mub.util.ModelSavingTask;
import com.ese2013.mub.util.Observable;
import com.ese2013.mub.util.SharedPrefsHandler;
import com.ese2013.mub.util.TranslationTask;
import com.ese2013.mub.util.TranslationTaskCallback;
import com.ese2013.mub.util.database.MensaDataSource;
import com.memetix.mst.language.Language;

/**
 * Manages the loading and storing of the whole model. This class holds the list
 * of all Mensas. It also initializes and updates this list. If the list of
 * Mensas is updated, all Observers are notified (e.g. GUI classes). This class
 * is a singleton and should be initialised by calling init().
 */
public class Model extends Observable implements ModelCreationTaskCallback, TranslationTaskCallback {
	private List<Mensa> mensas = new ArrayList<Mensa>();
	private MenuManager menuManager;
	private MensaDataSource dataSource;
	private SharedPrefsHandler prefs;
	private static Model instance;

	/**
	 * Private constructor to enforce singleton property of this claass.
	 */
	private Model() {
	}

	/**
	 * Returns the instance of Model. If none exists, a new one is created. To
	 * use the Model the caller needs to assure to call init() after first
	 * calling getInstance() in the application.
	 * 
	 * @return Model object, unique instance. New instance if none exited,
	 *         existing if already an instance has been created before.
	 */
	public static Model getInstance() {
		if (instance == null)
			instance = new Model();
		return instance;
	}

	/**
	 * Used to initialize the Model asynchronously. This must be called once on
	 * startup. The instance is created automatically when getInstance() is
	 * called, but the loading of the Model is started by calling this method.
	 * 
	 * @param dataSource
	 *            MensaDataSource to be used for possible local data. Must not
	 *            be null and must be initialized by calling
	 *            mensaDataSource.init(Context) on it.
	 * @param doTranslation
	 *            boolean to enable or disable translations. This boolean is
	 *            passed in such as the Model does not need to access the
	 *            preferences file using SharedPrefsHandler.
	 */
	public void init(MensaDataSource dataSource, SharedPrefsHandler prefs) {
		menuManager = new MenuManager();
		menuManager.setTranslationsEnabled(prefs.getDoTranslation());
		menuManager.setTranslationsAvailable(prefs.getTranslationAvialable());
		this.dataSource = dataSource;
		this.prefs = prefs;

		ModelCreationTask task = new ModelCreationTask(menuManager, dataSource, this);
		task.execute();
	}

	/**
	 * Returns the list of loaded Mensas.
	 * 
	 * @return List of Mensa objects.
	 */
	public List<Mensa> getMensas() {
		return mensas;
	}

	/**
	 * Returns the menu manager which holds the loaded menus.
	 * 
	 * @return MenuManager object which holds all loaded menus.
	 */
	public MenuManager getMenuManager() {
		return menuManager;
	}

	/**
	 * Returns the list of favorite Mensas.
	 * 
	 * @return List of favorite Mensas. This means isFavoriteMensa returns true
	 *         for every Mensa in this list.
	 */
	public List<Mensa> getFavoriteMensas() {
		List<Mensa> ret = new ArrayList<Mensa>(3);
		for (Mensa m : mensas)
			if (m.isFavorite())
				ret.add(m);
		return ret;
	}

	/**
	 * Returns if no Mensas are loaded currently.
	 * 
	 * @return true if no Mensas are loaded, false otherwise.
	 */
	public boolean noMensasLoaded() {
		return mensas.isEmpty();
	}

	/**
	 * Returns if any Mensas are set as favorite Mensas.
	 * 
	 * @return true if at least one Mensa is a favorite Mensa.
	 */
	public boolean favoritesExist() {
		for (Mensa m : mensas)
			if (m.isFavorite())
				return true;
		return false;
	}

	/**
	 * Returns the Mensa represented by the given Id.
	 * 
	 * @param mensaId
	 *            Int id of the Mensa.
	 * @return Mensa with the given mensaId, null if no such Mensa exists.
	 */
	public Mensa getMensaById(int mensaId) {
		Mensa mensa = null;
		for (Mensa m : mensas)
			if (m.getId() == mensaId)
				mensa = m;
		return mensa;
	}

	/**
	 * Saves the favorite Mensas to the local data source.
	 */
	public void saveFavorites() {
		dataSource.open();
		dataSource.storeFavorites(mensas);
		dataSource.close();
	}

	@Override
	public void onModelCreationTaskFinished(ModelCreationTask task) {
		if (task.wasSuccessful()) {
			mensas = task.getMensas();
			if (menuManager.isTranslationEnabled() && !menuManager.translationsAvailable())
				new TranslationTask(menuManager, Language.ENGLISH, this).execute();

			if (task.hasDownloadedNewData())
				saveModel();
		}
		notifyChanges(task.getStatusMsgResource());
	}

	@Override
	public void onTranslationTaskFinished(TranslationTask task) {
		if (task.hasSucceeded()) {
			prefs.setTranslationAvailable(true);
			menuManager.setTranslationsAvailable(true);
			saveModel();
			notifyChanges();
		} else {
			prefs.setTranslationAvailable(false);
			menuManager.setTranslationsAvailable(false);
		}
	}

	/**
	 * Initiates asynchronous saving of the whole Model, this means saving
	 * Mensas and their Menus to the local database.
	 */
	private void saveModel() {
		ModelSavingTask savingTask = new ModelSavingTask(mensas, dataSource);
		savingTask.execute();
	}
}
