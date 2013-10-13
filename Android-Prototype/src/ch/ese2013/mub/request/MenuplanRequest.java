package ch.ese2013.mub.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

import ch.ese2013.mub.model.Menu;
import ch.ese2013.mub.model.NoneMenu;
import ch.ese2013.mub.model.ServiceUri;
import ch.xonix.mensa.unibe.MenuListAdapter;
import ch.xonix.mensa.unibe.MenuplanActivity;
import ch.xonix.mensa.unibe.R;

public class MenuplanRequest extends AbstractRequest {

	private String mensaId;
	private MenuListAdapter menuListAdapter;
	private MenuplanActivity menuplanActivity;

	public MenuplanRequest(MenuplanActivity menuplanActivity, MenuListAdapter menuListAdapter, String mensaId) {
		this.menuListAdapter = menuListAdapter;
		this.mensaId = mensaId;
		this.menuplanActivity = menuplanActivity;
	}

	@Override
	public String getServiceUri() {
		String requestUri = ServiceUri.GET_CURRENT_MENUPLAN.replaceFirst(":id",
				this.mensaId);
		return requestUri;
	}
	
	@Override
	protected void onPostExecute(String json) {
		List<Menu> menus = new ArrayList<Menu>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			// get result object
			JSONObject result = jsonObject.getJSONObject("result");
			// content container
			JSONObject content = result.getJSONObject("content");
			String dateString = content.getString("date");
			String mensa = content.getString("mensa");
			
			TextView textView = (android.widget.TextView) this.menuplanActivity
					.findViewById(R.id.menuplan_title);
			String title = textView.getText().toString();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
			Date date;
			try {
				date = formatter.parse(dateString);
				formatter.applyPattern("EEEE, d. MMMM yyyy");
				dateString = formatter.format(date);
			} catch (ParseException e) {	
				e.printStackTrace();
			}
			
			textView.setText(title+"\n"+dateString);
			
			JSONArray menusJsonArray = content.getJSONArray("menus");
			
			
			
			for (int i = 0; i < menusJsonArray.length(); i++) {
				JSONObject menuJsonObject = menusJsonArray.getJSONObject(i);
				menus.add(new Menu(menuJsonObject));
			}
			
		} catch (JSONException e) {
			menus.add(new NoneMenu());	
			
			e.printStackTrace();
		}	finally {
			this.menuListAdapter.clear();
			this.menuListAdapter.addAll(menus);
			this.menuListAdapter.notifyDataSetChanged();
		}
	}


}
