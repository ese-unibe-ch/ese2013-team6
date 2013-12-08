package com.ese2013.mub.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.os.AsyncTask;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.util.database.MensaDataSource;
import com.ese2013.mub.util.parseDatabase.MensaDBHandler;
import com.parse.ParseException;

/**
 * This class creates the list of mensas by either using the Parse-Server or
 * loading from the local data. Also downloads the Menu ratings if possible
 * every time the model is created (as the ratings change more often than the
 * Menus/Mensas).
 * 
 * The decision which data source to use for the Mensas/Menzs is taken by
 * comparing if the stored menus are from a past week.
 * 
 */
public class ModelCreationTask extends AsyncTask<Void, Void, Void> {
	private MensaDataSource dataSource;
	private List<Mensa> mensas;
	private boolean successful, localDataOutdated, downloadedNewData;
	private MenuManager menuManager;
	private List<ModelCreationTaskCallback> callbacks = new ArrayList<ModelCreationTaskCallback>();

	/**
	 * Creates a new ModelCreationTask.
	 * 
	 * @param menuManager
	 *            MenuManager to store the Menus. Must not be null.
	 * @param dataSource
	 *            MensaDataSource to manage the local data. Must not be null and
	 *            must have been initialized by calling init().
	 * @param callbacks
	 *            A list of ModelCreationTaskCallbacks to be notified if the
	 *            Task is done.
	 */
	public ModelCreationTask(MenuManager menuManager, MensaDataSource dataSource, ModelCreationTaskCallback... callbacks) {
		this.menuManager = menuManager;
		this.dataSource = dataSource;

		for (ModelCreationTaskCallback callback : callbacks)
			this.callbacks.add(callback);
	}

	/**
	 * Asynchronously creates the mensa list by using
	 * {@link MensaFromWebFactory} or {@link MensaFromLocalFactory}. Which
	 * factory is used depends on the result of {@link localDataNeedsUpdate()}
	 * 
	 * @return Always null, as the result can be retrieved by using {@link
	 *         getMensas()}
	 */
	@Override
	protected Void doInBackground(Void... params) {
		AbstractMensaFactory fac;
		localDataOutdated = localDataNeedsUpdate();
		if (localDataOutdated) {
			fac = new MensaFromWebFactory(dataSource, menuManager);
			downloadedNewData = true;
		} else {
			fac = new MensaFromLocalFactory(dataSource, menuManager);
		}
		try {
			mensas = fac.createMensaList();
			getRatings();
			successful = true;
		} catch (MensaDownloadException e) {
			downloadedNewData = false;
			retryUsingLocalData(true);
		} catch (MensaLoadException e) {
			successful = false;
		} catch (ParseException e) {
			// if this happens at least one of the other exceptions is thrown
			// first usually. Also, the ParseException is only thrown when
			// retrieving ratings (which is not that important)
			downloadedNewData = false;
			retryUsingLocalData(false);
		}
		return null;
	}

	private void getRatings() throws ParseException {
		new MensaDBHandler().getMenuRatings(menuManager);
	}

	private void retryUsingLocalData(boolean getRatings) {
		AbstractMensaFactory fac = new MensaFromLocalFactory(dataSource, menuManager);
		try {
			mensas = fac.createMensaList();
			if (getRatings)
				getRatings();
			successful = true;
		} catch (IOException e) {
			successful = false;
		} catch (ParseException e) {
			retryUsingLocalData(false);
		}
	}

	/**
	 * Returns a status message resource address to display based on the results
	 * of the task. Should only be called if the task is done.
	 * 
	 * @return int containing resource address of short status message.
	 */
	public int getStatusMsgResource() {
		if (wasSuccessful()) {
			if (hasDownloadedNewData()) {
				return com.ese2013.mub.R.string.loading_download_done;
			} else {
				if (localDataOutdated)
					return com.ese2013.mub.R.string.loading_download_failed;
				else
					return com.ese2013.mub.R.string.loading_no_update_needed;
			}
		} else {
			return com.ese2013.mub.R.string.loading_failure;
		}
	}

	/**
	 * Returns the list of loaded mensas.
	 * 
	 * @return List of {@link Mensa} objects, is null if the loading failed.
	 */
	public List<Mensa> getMensas() {
		return mensas;
	}

	/**
	 * Returns if the loading was successful or not.
	 * 
	 * @return true if the model has been created successfully.
	 */
	public boolean wasSuccessful() {
		return successful;
	}

	/**
	 * Returns if the loading required using the Parse-Server. Also returns true
	 * if the loading tried using the web service but failed.
	 * 
	 * @return true if the web service has been used.
	 */
	public boolean hasDownloadedNewData() {
		return downloadedNewData;
	}

	/**
	 * Checks if the local data needs update.
	 * 
	 * @return true if the data needs to be updated.
	 */
	private boolean localDataNeedsUpdate() {
		try {
			dataSource.open();
			if (menusNotFromCurrentWeek())
				return true;

		} catch (Exception e) {
			// if anything happens during checking, we just update the local
			// data.
			return true;
		} finally {
			dataSource.close();
		}
		return false;
	}

	/**
	 * Called after the task has been executed, informs the
	 * ModelCreationTaskCallbacks that the Task is done (is now again in the
	 * Main Thread).
	 */
	@Override
	protected void onPostExecute(Void v) {
		super.onPostExecute(v);
		for (ModelCreationTaskCallback callback : callbacks)
			callback.onModelCreationTaskFinished(this);
	}

	private boolean menusNotFromCurrentWeek() {
		int currentWeek = Calendar.getInstance(Locale.GERMAN).get(Calendar.WEEK_OF_YEAR);
		int menusWeek = dataSource.getWeekOfStoredMenus();
		if (currentWeek > menusWeek)
			return true;
		else
			return false;
	}
}
