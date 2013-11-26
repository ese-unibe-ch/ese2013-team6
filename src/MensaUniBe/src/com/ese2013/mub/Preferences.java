/**
 * For a direct use of the shared preferences from any other class in die App.
 * You will need the context.
 */

package com.ese2013.mub;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences extends Activity {
		
	public static SharedPreferences getPrefs(Context context) {
	    return context.getSharedPreferences("MUBPrefsFile", Context.MODE_PRIVATE);
	}
	
	
	public boolean getDoTranslation(Context context){
		return getPrefs(context).getBoolean("doTranslation", false);
	}
	
	public int getLanguage(Context context){
		return getPrefs(context).getInt("language", 0);
	}
	
	public boolean getDoNotification(Context context){
		return getPrefs(context).getBoolean("doNotification", false);
	}
	
	public String getNotificationFood(Context context){
		return getPrefs(context).getString("notificationFood", "");
	}
	
	
	public void setDoTranslation(Context context, boolean doTranslation){
		getPrefs(context).edit().putBoolean("doTranslation", doTranslation).commit();
	}
	
	public void setLanguage(Context context, int language){
		getPrefs(context).edit().putInt("language", language).commit();
	}
	
	public void setDoNotification(Context context, boolean doNotification){
		getPrefs(context).edit().putBoolean("doNotification", doNotification).commit();
	}
	
	public void setNotificationFood(Context context, String notificationFood){
		getPrefs(context).edit().putString("notificationFood", notificationFood).commit();
	}

}
