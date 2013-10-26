package com.ese2013.mub.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;

/**
 * Performs a simple HTTP request to the address specified in the constructor.
 * The result is returned as a JSONObject.
 * 
 * This class mustn't be used in the main thread. It has to be used inside
 * another thread (i.e. a asynchronous download thread).
 */
public class JsonDataRequest {
	public static final int CODE_SUCCESS = 200;
	private String serviceUri;

	/**
	 * Creates a JsonDataRequest given a uri.
	 * 
	 * @param uri
	 *            String containing the address to be accessed. Must not be null
	 *            and should be a valid http address.
	 */
	public JsonDataRequest(String uri) {
		this.serviceUri = uri;
	}

	/**
	 * Performs the http request.
	 * 
	 * @return JSONObject which contains the result of the request. Is null if
	 *         anything went wrong: this means either that no connection could
	 *         be established or the response of the http server was no valid
	 *         JSON string.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public JSONObject execute() throws ClientProtocolException, IOException, JSONException {
		DefaultHttpClient client = new DefaultHttpClient();
		Uri serviceURI = Uri.parse(serviceUri);
		Builder uriBuilder = serviceURI.buildUpon();
		HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
		HttpResponse response = client.execute(httpGet);
		InputStream is = response.getEntity().getContent();
		String inputStream = inputStreamToString(is);
		return new JSONObject(inputStream);
	}

	/**
	 * Converts a InputStream to a String.
	 * 
	 * @param is
	 *            InputStream to be converted.
	 * @return String with the exact same content as the input stream.
	 * @throws IOException
	 *             If the InputStream couldn't be read.
	 */
	private static String inputStreamToString(InputStream is) throws IOException {
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
