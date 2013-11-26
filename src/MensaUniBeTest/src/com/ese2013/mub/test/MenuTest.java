package com.ese2013.mub.test;

import junit.framework.TestCase;
import static com.ese2013.mub.test.Util.assertNotEquals;

import com.ese2013.mub.model.Day;
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
		builder.setDate(new Day(4, 11, 2013));
		menu = builder.build();
	}

	public void testDefaultValues() {
		MenuBuilder builder = new MenuBuilder();
		assertNotNull(builder);
		Menu menu = builder.build();
		// all string values have defaults
		assertNotNull(menu.getTitle());
		assertNotNull(menu.getDescription());
		// at least the date needs to be set in a menu to make sense.
		assertNull(menu.getDate());
	}

	public void testBuilder() {
		assertNotNull(builder);
		menu = builder.build();
		assertEquals(menu.getTitle(), "Menu title");
		assertEquals(menu.getDescription(), "Menu description");
		assertEquals(menu.getDate(), new Day(4, 11, 2013));
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

	public void testDateComparison() {
		changedMenu = builder.setDate(new Day(5, 11, 2013)).build();
		assertNotEquals(menu, changedMenu);

		// test if Day.equals gets called and not only the Pointers are compared
		changedMenu = builder.setDate(new Day(4, 11, 2013)).build();
		assertEquals(menu, changedMenu);
	}

}
