package ch.ese2013.mub.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Menu {

	private static final String MENU_KEY_TITLE = "title";
	private static final String MENU_KEY_CONTENT = "menu";
	private static final String NEW_LINE = "\n";
	
	private JSONObject menuJsonObject;

	public Menu(JSONObject menuJsonObject) {
		this.menuJsonObject = menuJsonObject;
	}
	
	public String getTitle(){
		try {
			return this.menuJsonObject.getString(MENU_KEY_TITLE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getContent(){
		try {
			JSONArray content = this.menuJsonObject.getJSONArray(MENU_KEY_CONTENT);
			StringBuilder sb = new StringBuilder();
			for(int i=0; i < content.length(); i++){
				String line = content.getString(i);
				sb.append(line);
				sb.append(NEW_LINE);
			}
			return sb.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

}
