package ch.ese2013.mub.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.ese2013.mub.request.MensaDataRequest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

public class Model {
	
	private static final int CONNECTION_TIMEOUT = 15;
	private static final int DATARETRIEVAL_TIMEOUT = 15;
	public ArrayList<Mensa> mensas;

	public Model(Activity activity){
		 task = new MensaDataRequest(activity).execute();
		try {AsyncTask<String, Void, ArrayList<Mensa>>
			mensas = task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	protected ArrayList<Mensa> parseJSON(JSONObject json) {
		if(json==null)
			return null;
		try {
			// get result object
			JSONObject result = json.getJSONObject("result");
			// content container
			JSONArray content = result.getJSONArray("content");
			ArrayList<Mensa> mensasResult = new ArrayList<Mensa>();
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				mensasResult.add(new Mensa(mensaJsonObject));
			}
			return mensasResult;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	public static JSONObject requestWebService(String serviceUrl) {
	    disableConnectionReuseIfNecessary();
	 
	    HttpURLConnection urlConnection = null;
	    try {
	        // create connection
	        URL urlToRequest = new URL(serviceUrl);
	        urlConnection = (HttpURLConnection) 
	            urlToRequest.openConnection();
	        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
	        urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);
	         
	        // handle issues
	        int statusCode = urlConnection.getResponseCode();
	        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
	            // handle unauthorized (if service requires user login)
	        } else if (statusCode != HttpURLConnection.HTTP_OK) {
	            // handle any other errors, like 404, 500,..
	        }
	         
	        // create JSON object from content
	        InputStream in = new BufferedInputStream(
	            urlConnection.getInputStream());
	        return new JSONObject(getResponseText(in));
	         
	    } catch (MalformedURLException e) {
	    	System.out.println("----------------MalformedURLException-------------------");
	    } catch (SocketTimeoutException e) {
	        // data retrieval or connection timed out
	    	System.out.println("----------------SocketTimeoutException-------------------");
	    } catch (IOException e) {
	        // could not read response body 
	        // (could not create input stream)
	    	System.out.println("----------------IOException-------------------");
	    } catch (JSONException e) {
	        // response body is no valid JSON string#
	    	System.out.println("----------------JSONException-------------------");
	    } finally {
	        if (urlConnection != null) {
	            urlConnection.disconnect();
	        }
	    }       
	     
	    return null;
	}
	 
	/**
	 * required in order to prevent issues in earlier Android version.
	 */
	private static void disableConnectionReuseIfNecessary() {
	    // see HttpURLConnection API doc
	    if (Integer.parseInt(Build.VERSION.SDK) 
	            < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}
	 
	private static String getResponseText(InputStream inStream) {
	    // very nice trick from 
	    // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
	    return new Scanner(inStream).useDelimiter("\\A").next();
	}


	public ArrayList<Mensa> getMensas() {
		return mensas;
	}
}
