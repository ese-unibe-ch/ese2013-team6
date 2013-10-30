package com.ese2013.mub.util;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;

/**
 * This class loads asynchronously all data from the mensa web service and
 * stores it locally on the phone using the DataManager class.
 */
public class ModelCreationTask extends AsyncTask<Void, Void, Void> {
	private JSONArray updateStatusJson;
	private DataManager dataManager = DataManager.getSingleton();
	private List<Mensa> mensas;
	private boolean successful, downloadedNewData;

	/**
	 * Downloads all menus and mensas from the web service. Does not provide a
	 * meaningful return value (always returns zero).
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
		} catch (MensaDownloadException e) {
			successful = false;
			e.printStackTrace();
			return null;
		} catch (MensaLoadException e) {
			successful = false;
			e.printStackTrace();
			return null;
		}
		successful = true;
		return null;
	}

	public List<Mensa> getMensas() {
		return mensas;
	}

	public boolean wasSuccessful() {
		return successful;
	}

	public boolean hasDownloadedNewData() {
		return downloadedNewData;
	}

	// TODO Is this really in the right class here?
	private boolean localDataNeedsUpdate() {
		try {
			JsonDataRequest updateStatusRequest = new JsonDataRequest(ServiceUri.GET_UPDATE_STATUS);
			updateStatusJson = updateStatusRequest.execute().getJSONObject("result").getJSONArray("content");
			for (int i = 0; i < updateStatusJson.length(); i++) {
				JSONObject mensaJson = updateStatusJson.getJSONObject(i);
				int timestamp = dataManager.getMensaTimestamp(mensaJson.getInt("id"));
				if (timestamp < mensaJson.getInt("timestamp"))
					return true;
			}
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	/**
	 * Called after the task has been executed, informs the Model that the Task
	 * is done (is now again in the Main Thread).
	 */
	@Override
	protected void onPostExecute(Void v) {
		Model.getInstance().onCreationFinished(this);
	}
}
