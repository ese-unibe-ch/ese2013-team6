package com.ese2013.mub.model;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;

public class SyncJsonDataRequest {

	private String serviceUri;

	public SyncJsonDataRequest(String uri) {
		this.serviceUri = uri;
	}

	public JSONObject execute() {
		DefaultHttpClient client = new DefaultHttpClient();
		Uri serviceURI = Uri.parse(serviceUri);
		Builder uriBuilder = serviceURI.buildUpon();
		HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
		try {
			HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			String inputStream = JsonDataRequest.inputStreamToString(is);
			return new JSONObject(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
