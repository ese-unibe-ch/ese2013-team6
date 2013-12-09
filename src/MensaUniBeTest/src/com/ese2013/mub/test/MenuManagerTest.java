package com.ese2013.mub.test;

import java.util.Set;

import junit.framework.TestCase;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;

public class MenuManagerTest extends TestCase {

	private MenuManager menuManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		menuManager = new MenuManager();
	}

	public void testCreateDifferentMenus() {
		Menu menu = menuManager.createMenu("a", "title 1", "description 1");
		assertEquals(menu.getId(), "a");
		assertEquals(menu.getTitle(), "title 1");
		assertEquals(menu.getDescription(), "description 1");
		assertEquals(menu.getTranslatedTitle(), "");
		assertEquals(menu.getTranslatedDescription(), "");

		Menu menu2 = menuManager.createMenu("b", "title 2", "description 2", "translated title", "translated desc.");
		assertEquals(menu2.getId(), "b");
		assertEquals(menu2.getTitle(), "title 2");
		assertEquals(menu2.getDescription(), "description 2");
		assertEquals(menu2.getTranslatedTitle(), "translated title");
		assertEquals(menu2.getTranslatedDescription(), "translated desc.");

		assertEquals(menuManager.getMenus().size(), 2);
		assertTrue(menuManager.getMenus().contains(menu));
		assertTrue(menuManager.getMenus().contains(menu2));
		assertEquals(menuManager.getMenuIds().size(), 2);
		assertTrue(menuManager.getMenuIds().contains("a"));
		assertTrue(menuManager.getMenuIds().contains("b"));
	}

	public void testCreateSameMenu() {
		// same menu <=> same id.
		Menu menu = menuManager.createMenu("a", "title 1", "description 1");
		Menu menu2 = menuManager.createMenu("a", "title 2", "description 2", "translated title", "translated desc.");

		assertSame(menu, menu2);

		assertEquals(menuManager.getMenus().size(), 1);
		assertTrue(menuManager.getMenus().contains(menu));
		assertEquals(menuManager.getMenuIds().size(), 1);
		assertTrue(menuManager.getMenuIds().contains("a"));
	}

	public void testGetIds() {
		menuManager.createMenu("a", "title 1", "description 1");
		menuManager.createMenu("b", "title 2", "description 2", "translated title", "translated desc.");

		Set<String> ids = menuManager.getMenuIds();
		assertEquals(ids.size(), 2);
		assertTrue(menuManager.getMenuIds().contains("a"));
		assertTrue(menuManager.getMenuIds().contains("b"));
	}

	public void testGetMenu() {
		Menu menu = menuManager.createMenu("a", "title 1", "description 1");
		Menu menu2 = menuManager.createMenu("b", "title 2", "description 2", "translated title", "translated desc.");

		assertSame(menu, menuManager.getMenu("a"));
		assertSame(menu2, menuManager.getMenu("b"));
	}
}