package com.ese2013.mub.util;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.ese2013.mub.model.Model;

/**
 * This class loads asynchronously all data from the mensa web service and
 * stores it locally on the phone using the DataManager class.
 */
public class LocalDataUpdaterTask extends AsyncTask<Void, Void, Void> {
	private boolean success = false, downloadedNewData = false;
	private JSONArray updateStatusJson;
	
	/**
	 * Downloads all menus and mensas from the web service. Does not provide a
	 * meaningful return value (always returns zero).
	 */
	@Override
	protected Void doInBackground(Void... params) {
		if (!localDataNeedsUpdate()) {
			success = true;
			return null;
		}
		try {
			JsonDataRequest request = new JsonDataRequest(ServiceUri.GET_MENSAS);
			JSONObject result = request.execute();
			if (result.getJSONObject("result").getInt("code") != JsonDataRequest.CODE_SUCCESS)
				return null;
			
			JSONArray content = result.getJSONObject("result").getJSONArray("content");
			DataManager.getSingleton().storeMensaList(content, updateStatusJson);
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				int mensaId = mensaJsonObject.getInt("id");
				JsonDataRequest menuRequest = new JsonDataRequest(ServiceUri.GET_WEEKLY_MENUPLAN.replaceFirst(":id", ""
						+ mensaId));
				JSONObject json = menuRequest.execute();
				DataManager.getSingleton().storeWeeklyMenuplan(json, mensaId);
			}
			downloadedNewData = true;
			success = true;

			// no need to handle here as success stays false in case of any of
			// these exceptions.
		} catch (JSONException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (ParseException e) {
		} finally {
			DataManager.getSingleton().closeOpenResources();
		}
		return null;
	}

	private boolean localDataNeedsUpdate() {
		try {
			JsonDataRequest updateStatusRequest = new JsonDataRequest(ServiceUri.GET_UPDATE_STATUS);
			updateStatusJson = updateStatusRequest.execute().getJSONObject("result").getJSONArray("content");
			for (int i = 0; i < updateStatusJson.length(); i++) {
				JSONObject mensaJson = updateStatusJson.getJSONObject(i);
				int timestamp = DataManager.getSingleton().getMensaTimestamp(mensaJson.getInt("id"));
				if (timestamp < mensaJson.getInt("timestamp"))
					return true;
			}
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	/**
	 * Called after the task has been executed, informs the Model of the content
	 * changes and also tells if anything went wrong during download. (Runs in
	 * the main thread again)
	 */
	@Override
	protected void onPostExecute(Void v) {
		Model.getInstance().onLocalDataUpdateFinished(success, downloadedNewData);
	}
}
