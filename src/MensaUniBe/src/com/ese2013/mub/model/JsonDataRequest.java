package com.ese2013.mub.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;

public class JsonDataRequest {

	private String serviceUri;

	public JsonDataRequest(String uri) {
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
			String inputStream = inputStreamToString(is);
			return new JSONObject(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String inputStreamToString(InputStream is) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = bf.readLine()) != null) {
			sb.append(line);
		}
		bf.close();
		return sb.toString();
	}
}
