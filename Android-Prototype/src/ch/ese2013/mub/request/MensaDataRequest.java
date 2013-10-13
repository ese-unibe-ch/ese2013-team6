package ch.ese2013.mub.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import ch.ese2013.mub.model.Mensa;
import ch.ese2013.mub.model.ServiceUri;

public class MensaDataRequest extends AsyncTask<String, Void, ArrayList<Mensa>>{
	/** progress dialog to show user that the backup is processing. */
	private ProgressDialog dialog;
	
	public MensaDataRequest(Activity activity){
		dialog = new ProgressDialog(activity);
	}
	
	protected void onPreExecute() {
        this.dialog.setMessage("Progress start");
        this.dialog.show();
    }
	
	public String getServiceUri() {
		return ServiceUri.GET_MENSAS;
	}
	
	




	protected ArrayList<Mensa> onPostExecute(String json) {
		if(json==null)
			return null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			// get result object
			JSONObject result = jsonObject.getJSONObject("result");
			// content container
			JSONArray content = result.getJSONArray("content");
			ArrayList<Mensa> mensas = new ArrayList<Mensa>();
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				mensas.add(new Mensa(mensaJsonObject));
			}
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
			return mensas;
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	@Override
	protected ArrayList<Mensa> doInBackground(String... params) {
		DefaultHttpClient client = new DefaultHttpClient();
		Uri serviceURI = Uri.parse(this.getServiceUri());
		Builder uriBuilder = serviceURI.buildUpon();
		System.out.println("----------------httpget constructor-------------------");
		System.out.println(uriBuilder.build().toString());
		HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
		try {
			HttpResponse response = client.execute(httpGet);
			System.out.println("----------------response-------------------");
			System.out.println(response.toString());
			InputStream is = response.getEntity().getContent();
			System.out.println("----------------JSON-------------------");
			String inputStream = this.convertInputStreamToString(is);
			System.out.println(inputStream);

			ArrayList<Mensa> result = onPostExecute(inputStream);
			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private String convertInputStreamToString(InputStream is) throws IOException {
		BufferedReader bf = new BufferedReader(
				new InputStreamReader(is));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = bf.readLine()) != null) {
			sb.append(line);
		}
		bf.close();
		return sb.toString();
	}

	
}
