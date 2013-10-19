package com.ese2013.mub.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/**
 * This class loads asynchronously all data from the mensa webservice and stores
 * it locally on the phone using the DataManager class.
 */
public class LocalDataUpdater extends AsyncTask<Void, Void, Void> {
	@Override
	protected Void doInBackground(Void... params) {
		SyncJsonDataRequest request = new SyncJsonDataRequest(ServiceUri.GET_MENSAS);

		JSONArray content;
		try {
			content = request.execute().getJSONObject("result").getJSONArray("content");
			DataManager.getSingleton().storeJsonArray(content, "mensaList");
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				int mensaId = mensaJsonObject.getInt("id");
				SyncJsonDataRequest menuRequest = new SyncJsonDataRequest(ServiceUri.GET_WEEKLY_MENUPLAN.replaceFirst(":id",
						"" + mensaId));
				DataManager.getSingleton().storeJsonObject(menuRequest.execute(), "WEEKLY_MENUPLAN_" + mensaId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
