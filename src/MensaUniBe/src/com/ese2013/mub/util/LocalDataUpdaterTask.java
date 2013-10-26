package com.ese2013.mub.util;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ese2013.mub.model.Model;

import android.os.AsyncTask;

/**
 * This class loads asynchronously all data from the mensa web service and
 * stores it locally on the phone using the DataManager class.
 */
public class LocalDataUpdaterTask extends AsyncTask<Void, Void, Void> {
	private boolean success;

	/**
	 * Downloads all menus and mensas from the web service. Does not provide a
	 * meaningful return value (always returns zero).
	 */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_MENSAS);
			JSONObject result = request.execute();
			if (result.getJSONObject("result").getInt("code") != JsonDataRequest.CODE_SUCCESS)
				success = false;

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
			success = false;
		} catch (ClientProtocolException e) {
			success = false;
		} catch (IOException e) {
			success = false;
		} catch (ParseException e) {
			success = false;
		} finally {
			DataManager.getSingleton().closeOpenResources();
		}
		return null;
	}

	/**
	 * Called after the task has been executed, informs the Model of the content
	 * changes and also tells if anything went wrong during download. (Runs in
	 * the main thread again)
	 */
	@Override
	protected void onPostExecute(Void v) {
		Model.getInstance().onWebContentRetrieved(success);
	}
}
