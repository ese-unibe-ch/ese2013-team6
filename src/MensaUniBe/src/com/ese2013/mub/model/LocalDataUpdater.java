package com.ese2013.mub.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * This class loads asynchronously all data from the mensa webservice and stores
 * it locally on the phone using the DataManager class.
 */
public class LocalDataUpdater extends AsyncTask<Void, Void, Void> {
	boolean success = false;
	@Override
	protected Void doInBackground(Void... params) {
		try {
			JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_MENSAS);
			JSONObject result = request.execute();
			if (result == null)
				return null;
			JSONArray content = result.getJSONObject("result").getJSONArray("content");
			DataManager.getSingleton().storeJsonArray(content, "mensaList");
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				int mensaId = mensaJsonObject.getInt("id");
				JsonDataRequest menuRequest = new JsonDataRequest(ServiceUri.GET_WEEKLY_MENUPLAN.replaceFirst(":id", ""
						+ mensaId));
				DataManager.getSingleton().storeJsonObject(menuRequest.execute(), "WEEKLY_MENUPLAN_" + mensaId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		success = true;
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
		Log.d("Updater", "FINISHED REDOWNLOADING DATA");
		Model.getInstance().onWebContentRetrieved(success);
	}
}
