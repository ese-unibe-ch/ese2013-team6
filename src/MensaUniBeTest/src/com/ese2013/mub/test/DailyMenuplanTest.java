package com.ese2013.mub.test;

import junit.framework.TestCase;
import static com.ese2013.mub.test.Util.assertNotEquals;
import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.Menu.MenuBuilder;

public class DailyMenuplanTest extends TestCase {
	private DailyMenuplan plan;

	protected void setUp() throws Exception {
		super.setUp();
		plan = new DailyMenuplan(new Day(23, 11, 2013));
	}

	public void testAddMenuAndContains() {
		Menu menu = new MenuBuilder().setTitle("some title").setDescription("some desc.").setId("a").build();
		assertNotNull(plan.getMenus());
		assertEquals(plan.getMenus().size(), 0);
		assertFalse(plan.contains(menu));
		
		plan.add(menu);
		assertEquals(plan.getMenus().size(), 1);
		assertEquals(plan.getMenus().get(0), menu);
		assertTrue(plan.contains(menu));
	}

	public void testAddMultipleMenus() {
		MenuBuilder builder = new MenuBuilder().setTitle("some title").setDescription("some desc.").setId("a");
		Menu menu1 = builder.build();
		Menu menu2 = builder.setTitle("some other title").build();

		plan.add(menu1);
		plan.add(menu2);
		assertEquals(plan.getMenus().size(), 2);
		assertTrue(plan.contains(menu1));
		assertTrue(plan.contains(menu2));
	}

	public void testEquals() {
		DailyMenuplan otherPlan = new DailyMenuplan(new Day(23, 11, 2013));
		assertEquals(plan, plan);
		assertEquals(plan, otherPlan);

		MenuBuilder builder = new MenuBuilder().setTitle("some title").setDescription("some desc.").setId("a");
		Menu menu1 = builder.build();
		Menu menu2 = builder.setTitle("some other title").build();

		plan.add(menu1);
		assertNotEquals(plan, otherPlan);
		otherPlan.add(menu1);
		assertEquals(plan, otherPlan);

		plan.add(menu2);
		assertNotEquals(plan, otherPlan);

		// new instance with same content, check if equals is called recursively
		menu2 = builder.build();
		otherPlan.add(menu2);
		assertEquals(plan, otherPlan);
	}
}
