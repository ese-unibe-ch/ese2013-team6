package ch.ese2013.mub.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.ArrayAdapter;

import ch.ese2013.mub.model.Mensa;
import ch.ese2013.mub.model.ServiceUri;

public class MensasRequest extends AbstractRequest {
	
	private ArrayAdapter<Mensa> mensaListAdapter;

	public MensasRequest(ArrayAdapter<Mensa> mensaListAdapter) {
		this.mensaListAdapter = mensaListAdapter;
	}
	
	@Override
	public String getServiceUri() {
		return ServiceUri.GET_MENSAS;
	}

	@Override
	protected void onPostExecute(String json) {
		if(json==null)
			return;
		try {
			JSONObject jsonObject = new JSONObject(json);
			// get result object
			JSONObject result = jsonObject.getJSONObject("result");
			// content container
			JSONArray content = result.getJSONArray("content");
			List<Mensa> mensas = new ArrayList<Mensa>();
			for (int i = 0; i < content.length(); i++) {
				JSONObject mensaJsonObject = content.getJSONObject(i);
				mensas.add(new Mensa(mensaJsonObject));
			}
			this.mensaListAdapter.clear();
			for(Mensa mensa : mensas)
				this.mensaListAdapter.add(mensa);
			this.mensaListAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}



}
