package com.ese2013.mub.test;

import static com.ese2013.mub.test.Util.assertNotEquals;

import java.util.List;

import junit.framework.TestCase;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.Menu.MenuBuilder;
import com.ese2013.mub.model.WeeklyMenuplan;

public class WeeklyMenuplanTest extends TestCase {
	private WeeklyMenuplan plan;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		plan = new WeeklyMenuplan();
	}

	public void testAddMenu() {
		Day day = new Day(4, 11, 2013);
		Menu menu = createMenu("a", "title 1", day);
		assertNull(plan.getDailymenuplan(day));
		plan.add(menu);
		List<Menu> menus = plan.getDailymenuplan(day).getMenus();
		assertEquals(menus.size(), 1);
		assertEquals(menus.get(0), menu);
	}

	public void testMultipleMenusOnSameDay() {
		Day day = new Day(4, 11, 2013);
		assertNull(plan.getDailymenuplan(day));
		Menu menu = createMenu("a", "title 1", day);
		plan.add(menu);
		Menu menu2 = createMenu("b", "title 2", day);
		plan.add(menu2);
		List<Menu> menus = plan.getDailymenuplan(day).getMenus();
		assertEquals(menus.size(), 2);
		assertTrue(menus.contains(menu));
		assertTrue(menus.contains(menu2));
	}

	public void testMultipleMenusOnDifferentDays() {
		Day day1 = new Day(4, 11, 2013);
		assertNull(plan.getDailymenuplan(day1));
		Menu menu = createMenu("a", "title 1", day1);
		plan.add(menu);

		Day day2 = new Day(6, 11, 2013);
		assertNull(plan.getDailymenuplan(day2));
		Menu menu2 = createMenu("b", "title 2", day2);
		plan.add(menu2);

		List<Menu> menusDay1 = plan.getDailymenuplan(day1).getMenus();
		assertEquals(menusDay1.size(), 1);
		assertTrue(menusDay1.contains(menu));

		List<Menu> menusDay2 = plan.getDailymenuplan(day2).getMenus();
		assertEquals(menusDay2.size(), 1);
		assertTrue(menusDay2.contains(menu2));
	}

	public void testGetDays() {
		Day day1 = new Day(4, 11, 2013);
		Menu menu = createMenu("a", "title 1", day1);
		plan.add(menu);

		Day day2 = new Day(6, 11, 2013);
		Menu menu2 = createMenu("b", "title 2", day2);
		plan.add(menu2);

		assertEquals(plan.getDays().size(), 2);
		assertTrue(plan.getDays().contains(day1));
		assertTrue(plan.getDays().contains(day2));

		Day day3 = new Day(5, 11, 2013);
		assertFalse(plan.getDays().contains(day3));
	}

	public void testGetWeekNumber() {
		Day day = new Day(4, 11, 2013);
		Menu menu = createMenu("a", "title 1", day);
		plan.add(menu);
		assertEquals(day.getWeekNumber(), plan.getWeekNumber());
	}

	public void testEquals() {
		Day day1 = new Day(4, 11, 2013);
		Menu menu = createMenu("a", "title 1", day1);
		plan.add(menu);

		Day day2 = new Day(6, 11, 2013);
		Menu menu2 = createMenu("b", "title 2", day2);
		plan.add(menu2);

		WeeklyMenuplan otherPlan = new WeeklyMenuplan();
		// create new instances with same data, assures that equals recursively
		// uses equals on the Menus.
		menu = createMenu("a", "title 1", day1);
		otherPlan.add(menu);
		menu2 = createMenu("b", "title 2", day2);
		otherPlan.add(menu2);
		assertEquals(plan, otherPlan);

		Menu menu3 = createMenu("c", "title 2", day2);
		otherPlan.add(menu3);
		assertNotEquals(plan, otherPlan);

		menu3 = createMenu("d", "title 3", day2);
		plan.add(menu3);
		assertNotEquals(plan, otherPlan);
	}

	private static Menu createMenu(String id, String title, Day day) {
		return new MenuBuilder().setId(id).setTitle(title).setDescription("some description").setDate(day).build();
	}

}
