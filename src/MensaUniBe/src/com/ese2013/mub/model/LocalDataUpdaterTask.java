package com.ese2013.mub.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/**
 * This class loads asynchronously all data from the mensa webservice and stores
 * it locally on the phone using the DataManager class.
 */
public class LocalDataUpdaterTask extends AsyncTask<Void, Void, Void> {
	private boolean success = false;
	private static final int CODE_SUCCESS = 200;

	@Override
	protected Void doInBackground(Void... params) {
		try {
			JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_MENSAS);
			JSONObject result = request.execute();
			if (result == null || result.getJSONObject("result").getInt("code") != CODE_SUCCESS)
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
			success = true;
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Void v) {
		Model.getInstance().onWebContentRetrieved(success);
	}
}
