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
		plan = new DailyMenuplan();
	}

	public void testAddMenu() {
		Menu menu = new MenuBuilder().setDate(new Day(23, 11, 2013)).setTitle("some title").setDescription("some desc.")
				.setId("a").build();
		assertNotNull(plan.getMenus());
		assertEquals(plan.getMenus().size(), 0);

		plan.add(menu);
		assertEquals(plan.getMenus().size(), 1);
		assertEquals(plan.getMenus().get(0), menu);
	}

	public void testAddMultipleMenus() {
		MenuBuilder builder = new MenuBuilder().setDate(new Day(23, 11, 2013)).setTitle("some title")
				.setDescription("some desc.").setId("a");
		Menu menu1 = builder.build();
		Menu menu2 = builder.setTitle("some other title").build();

		plan.add(menu1);
		plan.add(menu2);
		assertEquals(plan.getMenus().size(), 2);
		assertTrue(plan.getMenus().contains(menu1));
		assertTrue(plan.getMenus().contains(menu2));
	}

	public void testEquals() {
		DailyMenuplan otherPlan = new DailyMenuplan();
		assertEquals(plan, plan);
		assertEquals(plan, otherPlan);

		MenuBuilder builder = new MenuBuilder().setDate(new Day(23, 11, 2013)).setTitle("some title")
				.setDescription("some desc.").setId("a");
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
