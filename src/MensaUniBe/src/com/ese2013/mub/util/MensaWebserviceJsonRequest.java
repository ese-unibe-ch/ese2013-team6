package com.ese2013.mub.util;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Performs a simple HTTP request to the address specified in the constructor.
 * The result is returned as a JSONObject. Checks if the result is valid by
 * checking if the returned error code equals 200.
 * 
 * This class mustn't be used in the main thread. It has to be used inside
 * another thread (i.e. a asynchronous download thread).
 */
public class MensaWebserviceJsonRequest extends JsonDataRequest {
	public static final int CODE_SUCCESS = 200;

	/**
	 * Creates a JsonDataRequest given a URI.
	 * 
	 * @param uri
	 *            String containing the address to be accessed. Must not be null
	 *            and should be a valid HTTP address.
	 */
	public MensaWebserviceJsonRequest(String uri) {
		super(uri);
	}

	/**
	 * Performs the HTTP request.
	 * 
	 * @return JSONObject which contains the result of the request. Is null if
	 *         anything went wrong: this means either that no connection could
	 *         be established or the response of the HTTP server was no valid
	 *         JSON string or the Mensa Web Service reported an error.
	 * @throws IOException
	 *             If download did not succeed or web service did not provide a
	 *             valid JSON file or the web service reported an error using
	 *             the error code.
	 */
	public JSONObject execute() throws IOException {
		try {
			JSONObject json = super.execute();
			int errorCode = json.getJSONObject("result").getInt("code");
			if (errorCode == CODE_SUCCESS) {
				return json;
			} else {
				throw new IOException("Failed downloading: exit with error code: " + errorCode);
			}
		} catch (JSONException e) {
			throw new IOException(e);
		}
	}
}