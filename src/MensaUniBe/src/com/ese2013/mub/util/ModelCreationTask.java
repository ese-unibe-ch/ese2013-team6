package com.ese2013.mub.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.ese2013.mub.model.Mensa;
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
	private MensaDataSource dataSource = MensaDataSource.getInstance();
	private List<Mensa> mensas;
	private boolean successful, downloadedNewData;
	
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
		if (localDataNeedsUpdate()) {
			fac = new MensaFromWebFactory(updateStatusJson);
			downloadedNewData = true;
		} else {
			fac = new MensaFromLocalFactory();
		}
		try {
			mensas = fac.createMensaList();
			successful = true;
		} catch (IOException e) {
			successful = false;
		}
		return null;
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
			return true;
		} finally {
			dataSource.close();
		}
		return false;
	}

	/**
	 * Called after the task has been executed, informs the {@link Model} that the Task
	 * is done (is now again in the Main Thread).
	 */
	@Override
	protected void onPostExecute(Void v) {
		Model.getInstance().onCreationFinished(this);
	}

	private void retrieveUpdatesPage() throws JSONException, IOException {
		JsonDataRequest updateStatusRequest = new JsonDataRequest(ServiceUri.GET_UPDATE_STATUS);
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
