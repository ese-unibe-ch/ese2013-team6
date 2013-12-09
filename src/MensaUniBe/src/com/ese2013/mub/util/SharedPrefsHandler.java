package com.ese2013.mub.util;

import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Encapsulates access to shared preferences. Needs a context to get the shared
 * preferences of the application.
 */
public class SharedPrefsHandler {
	private static final String FIRST_TIME = "first_time", USERMAIL = "usermail",
			NOTIFICATIONS_FOR_ALL_MENSAS = "notificationsForAllMensas", DO_NOTIFICATION = "doNotification",
			DO_TRANSLATION = "doTranslation", TRANSLATION_AVAIL = "translationAvail", CRITERIA_SET = "citeriaSet";

	public static final String PREFS_FILE_NAME = "MUBPrefsFile";
	private Context context;

	public SharedPrefsHandler(Context context) {
		this.context = context;
	}

	private SharedPreferences getPrefs() {
		return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
	}

	private Editor getEditPrefs() {
		return getPrefs().edit();
	}

	public boolean getDoTranslation() {
		return getPrefs().getBoolean(DO_TRANSLATION, false);
	}

	public void setDoTranslation(boolean doTranslation) {
		getEditPrefs().putBoolean(DO_TRANSLATION, doTranslation).commit();
	}

	public boolean getTranslationAvialable() {
		return getPrefs().getBoolean(TRANSLATION_AVAIL, false);
	}

	public void setTranslationAvailable(boolean translationAvailable) {
		getEditPrefs().putBoolean(TRANSLATION_AVAIL, translationAvailable).commit();
	}

	public boolean getDoNotification() {
		return getPrefs().getBoolean(DO_NOTIFICATION, false);
	}

	public void setDoNotification(boolean doNotification) {
		getEditPrefs().putBoolean(DO_NOTIFICATION, doNotification).commit();
	}

	public Set<String> getNotificationListItems() {
		return getPrefs().getStringSet(CRITERIA_SET, new TreeSet<String>());
	}

	public void setNotificationListItems(Set<String> notificationListItems) {
		getEditPrefs().putStringSet(CRITERIA_SET, notificationListItems).commit();
	}

	public boolean getDoNotificationsForAllMensas() {
		return getPrefs().getBoolean(NOTIFICATIONS_FOR_ALL_MENSAS, true);
	}

	public void setDoNotificationsForAllMensas(boolean notificationsForAllMensas) {
		getEditPrefs().putBoolean(NOTIFICATIONS_FOR_ALL_MENSAS, notificationsForAllMensas).commit();
	}

	public String getUserEmail() {
		return getPrefs().getString(USERMAIL, null);
	}

	public void setUserEmail(String usermail) {
		getEditPrefs().putString(USERMAIL, usermail).commit();
	}

	public boolean isFirstTime() {
		return getPrefs().getBoolean(FIRST_TIME, true);
	}

	public void setIsFirstTime(boolean firstTime) {
		getEditPrefs().putBoolean(FIRST_TIME, firstTime).commit();
	}

	public boolean isUserRegistred() {
		return getUserEmail() != null;
	}
}
