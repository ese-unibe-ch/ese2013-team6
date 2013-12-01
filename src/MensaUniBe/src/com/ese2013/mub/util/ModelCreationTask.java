package com.ese2013.mub.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.database.MensaDataSource;

/**
 * This class creates the list of mensas by either using the mensa web service
 * or loading from the local data.
 * 
 * The decision which data source to use is taken by comparing if the stored
 * menus are from a past week and checking if the web service was updated (by
 * checking the updates page on the web service).
 * 
 */
public class ModelCreationTask extends AsyncTask<Void, Void, Void> {
	private JSONArray updateStatusJson;
	private MensaDataSource dataSource;
	private List<Mensa> mensas;
	private boolean successful, localDataOutdated, downloadedNewData;
	private MenuManager menuManager;
	private List<ModelCreationTaskCallback> callbacks = new ArrayList<ModelCreationTaskCallback>();

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
			fac = new MensaFromWebFactory(updateStatusJson, menuManager);
			downloadedNewData = true;
		} else {
			fac = new MensaFromLocalFactory();
		}

		try {
			mensas = fac.createMensaList();
			successful = true;
		} catch (MensaDownloadException e) {
			downloadedNewData = false;
			retryUsingLocalData();
		} catch (MensaLoadException e) {
			successful = false;
		}
		return null;
	}

	private void retryUsingLocalData() {
		AbstractMensaFactory fac = new MensaFromLocalFactory();
		try {
			mensas = fac.createMensaList();
			successful = true;
		} catch (IOException e) {
			successful = false;
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
	 * Returns if the loading required using the web service. Also returns true
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
			retrieveUpdatesPage();
			dataSource.open();
			if (menusNotFromCurrentWeek() || webDataUpdated())
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
	 * Called after the task has been executed, informs the {@link Model} that
	 * the Task is done (is now again in the Main Thread).
	 */
	@Override
	protected void onPostExecute(Void v) {
		super.onPostExecute(v);
		for (ModelCreationTaskCallback callback : callbacks)
			callback.onTaskFinished(this);
	}

	private void retrieveUpdatesPage() throws JSONException, IOException {
		MensaWebserviceJsonRequest updateStatusRequest = new MensaWebserviceJsonRequest(ServiceUri.GET_UPDATE_STATUS);
		updateStatusJson = updateStatusRequest.execute().getJSONObject("result").getJSONArray("content");
	}

	private boolean menusNotFromCurrentWeek() {
		int currentWeek = Calendar.getInstance(Locale.GERMAN).get(Calendar.WEEK_OF_YEAR);
		int menusWeek = dataSource.getWeekOfStoredMenus();
		if (currentWeek > menusWeek)
			return true;
		else
			return false;
	}

	/**
	 * This is used only as a additional check, as the updates page sometimes is
	 * still buggy.
	 * 
	 * @return
	 * @throws JSONException
	 */
	private boolean webDataUpdated() throws JSONException {
		for (int i = 0; i < updateStatusJson.length(); i++) {
			JSONObject mensaJson = updateStatusJson.getJSONObject(i);
			int timestamp = dataSource.getMensaTimestamp(mensaJson.getInt("id"));
			if (timestamp < mensaJson.getInt("timestamp"))
				return true;
		}
		return false;
	}
}
