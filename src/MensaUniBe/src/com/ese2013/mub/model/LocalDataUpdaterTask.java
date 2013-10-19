package com.ese2013.mub.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/**
 * This class loads asynchronously all data from the mensa web service and
 * stores it locally on the phone using the DataManager class.
 */
public class LocalDataUpdaterTask extends AsyncTask<Void, Void, Void> {
	private boolean success = false;
	private static final int CODE_SUCCESS = 200;

	/**
	 * Downloads all menus and mensas from the web service. Does not provide a
	 * meaningful return value (always returns zero).
	 */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_MENSAS);
			JSONObject result = request.execute();
			if (result == null || result.getJSONObject("result").getInt("code") != CODE_SUCCESS)
				return null;

			JSONArray content = result.getJSONObject("result").getJSONArray("content");
			DataManager.getSingleton().storeMensaList(content);
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				int mensaId = mensaJsonObject.getInt("id");
				JsonDataRequest menuRequest = new JsonDataRequest(ServiceUri.GET_WEEKLY_MENUPLAN.replaceFirst(":id", ""
						+ mensaId));
				DataManager.getSingleton().storeWeeklyMenuplan(menuRequest.execute(), mensaId);
			}
			success = true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Called after the task has been executed, informs the Model of the content
	 * changes and also tells if anything went wrong during download.
	 */
	@Override
	protected void onPostExecute(Void v) {
		Model.getInstance().onWebContentRetrieved(success);
	}
}
