package com.ese2013.mub.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Encapsulates access to shared preferences. Needs a context to get the shared
 * preferences of the application.
 */
public class SharedPrefsHandler {
	private static final String FIRST_TIME = "first_time", USERMAIL = "usermail", NOTIFICATION_FOOD = "notificationFood",
			NOTIFICATION_MENSAS = "notificationMensas", DO_NOTIFICATION = "doNotification", LANGUAGE = "language",
			DO_TRANSLATION = "doTranslation";
	private Context context;

	public SharedPrefsHandler(Context context) {
		this.context = context;
	}

	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences("MUBPrefsFile", Context.MODE_PRIVATE);
	}

	public boolean getDoTranslation() {
		return getPrefs(context).getBoolean(DO_TRANSLATION, false);
	}

	public int getLanguage() {
		return getPrefs(context).getInt(LANGUAGE, 0);
	}

	public boolean getDoNotification() {
		return getPrefs(context).getBoolean(DO_NOTIFICATION, false);
	}

	public int getNotificationMensas() {
		return getPrefs(context).getInt(NOTIFICATION_MENSAS, 0);
	}

	public String getNotificationFood() {
		return getPrefs(context).getString(NOTIFICATION_FOOD, "");
	}

	public void setDoTranslation(boolean doTranslation) {
		getPrefs(context).edit().putBoolean(DO_TRANSLATION, doTranslation).commit();
	}

	public void setLanguage(int language) {
		getPrefs(context).edit().putInt(LANGUAGE, language).commit();
	}

	public void setDoNotification(boolean doNotification) {
		getPrefs(context).edit().putBoolean(DO_NOTIFICATION, doNotification).commit();
	}

	public void setNotificationMensas(int notificationMensas) {
		getPrefs(context).edit().putInt(NOTIFICATION_MENSAS, notificationMensas).commit();
	}

	public void setNotificationFood(String notificationFood) {
		getPrefs(context).edit().putString(NOTIFICATION_FOOD, notificationFood).commit();
	}

	public String getUserEmail() {
		return getPrefs(context).getString(USERMAIL, null);
	}

	public void setUserEmail(String usermail) {
		getPrefs(context).edit().putString(USERMAIL, usermail).commit();
	}

	public boolean isFirstTime() {
		return getPrefs(context).getBoolean(FIRST_TIME, true);
	}

	public void setIsFirstTime(boolean firstTime) {
		getPrefs(context).edit().putBoolean(FIRST_TIME, firstTime).commit();
	}

	public boolean isUserRegistred() {
		return getUserEmail() != null;
	}
}
