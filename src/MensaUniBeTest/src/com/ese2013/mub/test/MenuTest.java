package com.ese2013.mub.test;

import static com.ese2013.mub.test.Util.assertNotEquals;
import junit.framework.TestCase;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.Menu.MenuBuilder;

public class MenuTest extends TestCase {
	private MenuBuilder builder;
	private Menu menu;
	private Menu changedMenu;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		builder = new MenuBuilder();
		builder.setId("a");
		builder.setTitle("Menu title");
		builder.setDescription("Menu description");
		menu = builder.build();
	}

	public void testDefaultValues() {
		MenuBuilder builder = new MenuBuilder();
		assertNotNull(builder);
		Menu menu = builder.build();
		// all string values have defaults
		assertNotNull(menu.getTitle());
		assertNotNull(menu.getDescription());
	}

	public void testBuilder() {
		assertNotNull(builder);
		menu = builder.build();
		assertEquals(menu.getTitle(), "Menu title");
		assertEquals(menu.getDescription(), "Menu description");
	}

	public void testSelfEquals() {
		assertFalse(menu.equals(null));
		assertEquals(menu, menu);
	}

	public void testIdComparison() {
		changedMenu = builder.setId("d").build();
		assertNotEquals(menu, changedMenu);
	}

	public void testTitleComparison() {
		changedMenu = builder.setTitle("New Title").build();
		assertNotEquals(menu, changedMenu);
	}

	public void testDescriptionComparison() {
		changedMenu = builder.setTitle("New Description").build();
		assertNotEquals(menu, changedMenu);
	}
}