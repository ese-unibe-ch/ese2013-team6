package com.ese2013.mub.test;

import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.test.AndroidTestCase;

import com.ese2013.mub.util.SharedPrefsHandler;

public class SharedPrefsHandlerTest extends AndroidTestCase {
	private SharedPrefsHandler handler;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		handler = new SharedPrefsHandler(getContext());
		clearSharedPrefsFile();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		clearSharedPrefsFile();
	}

	private void clearSharedPrefsFile() {
		getContext().getSharedPreferences(SharedPrefsHandler.PREFS_FILE_NAME, Context.MODE_PRIVATE).edit().clear().commit();
	}

	public void testGetAndSetDoTranslation() {
		assertFalse(handler.getDoTranslation());
		handler.setDoTranslation(true);
		assertTrue(handler.getDoTranslation());
		handler.setDoTranslation(false);
		assertFalse(handler.getDoTranslation());
	}

	public void testGetAndSetTranslationsAvailable() {
		assertFalse(handler.getTranslationAvialable());
		handler.setTranslationAvailable(true);
		assertTrue(handler.getTranslationAvialable());
		handler.setTranslationAvailable(false);
		assertFalse(handler.getTranslationAvialable());
	}

	public void testNotificationSettings() {
		assertFalse(handler.getDoNotification());
		handler.setDoNotification(true);
		assertTrue(handler.getDoNotification());
		handler.setDoNotification(false);
		assertFalse(handler.getDoNotification());

		assertTrue(handler.getNotificationListItems().isEmpty());
		Set<String> items = new TreeSet<String>();
		items.add("Schnitzel");
		items.add("Pasta");
		handler.setNotificationListItems(items);
		assertFalse(handler.getNotificationListItems().isEmpty());
		assertEquals(items, handler.getNotificationListItems());

		assertTrue(handler.getDoNotificationsForAllMensas());
		handler.setDoNotificationsForAllMensas(false);
		assertFalse(handler.getDoNotificationsForAllMensas());
		handler.setDoNotificationsForAllMensas(true);
		assertTrue(handler.getDoNotificationsForAllMensas());
	}

	public void testGetAndSetIsFirstTime() {
		assertTrue(handler.isFirstTime());
		handler.setIsFirstTime(false);
		assertFalse(handler.isFirstTime());
		handler.setIsFirstTime(true);
		assertTrue(handler.isFirstTime());
	}

	public void testGetAndSetEmail() {
		assertNull(handler.getUserEmail());
		assertFalse(handler.isUserRegistred());
		handler.setUserEmail("some.random@email.com");
		assertEquals("some.random@email.com", handler.getUserEmail());
		assertTrue(handler.isUserRegistred());
	}
}