package com.ese2013.mub.test;

import junit.framework.TestCase;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;

public class MensaTest extends TestCase {

	private Mensa.MensaBuilder builder;
	private Mensa mensa;
	private Mensa changedMensa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		builder = new Mensa.MensaBuilder();
		builder.setId(1);
		builder.setIsFavorite(true);
		builder.setLatitude(42.123451);
		builder.setLongitude(14.12451);
		builder.setName("Some mensa");
		builder.setStreet("Some Street 123");
		builder.setTimestamp(123456789);
		builder.setZip("3010 Bern");
		mensa = builder.build();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDefaultValues() {
		Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
		assertNotNull(builder);
		Mensa mensa = builder.build();
		// all string values have defaults as these are immutable
		assertNotNull(mensa.getName());
		assertNotNull(mensa.getStreet());
		assertNotNull(mensa.getZip());
		// the weekly plan is null as it can be modified by using the setter
		assertNull(mensa.getMenuplan());
	}

	public void testBuilder() {
		assertNotNull(builder);
		mensa = builder.build();
		assertEquals(mensa.getId(), 1);
		assertEquals(mensa.getLatitude(), 42.123451);
		assertEquals(mensa.getLongitude(), 14.12451);
		assertEquals(mensa.getName(), "Some mensa");
		assertEquals(mensa.getStreet(), "Some Street 123");
		assertEquals(mensa.getZip(), "3010 Bern");
		assertEquals(mensa.getTimestamp(), 123456789);
		assertNull(mensa.getMenuplan());
	}

	public void testSelfEquals() {
		assertNotNull(builder);
		assertFalse(mensa.equals(null));
		assertEquals(mensa, mensa);
	}

	public void testIdComparison() {
		changedMensa = builder.setId(2).build();
		assertNotEquals(changedMensa, mensa);
		assertNotEquals(mensa, changedMensa);
	}

	public void testIsFavoriteComparison() {
		changedMensa = builder.setIsFavorite(false).build();
		assertNotEquals(mensa, changedMensa);
	}

	private void assertNotEquals(Mensa mensa, Mensa changedMensa) {
		assertFalse(changedMensa.equals(mensa));
		assertFalse(mensa.equals(changedMensa));
	}

	public void testNameComparison() {
		changedMensa = builder.setName("some other name").build();
		assertNotEquals(mensa, changedMensa);
	}

	public void testStreetComparison() {
		changedMensa = builder.setStreet("some other address").build();
		assertNotEquals(mensa, changedMensa);
	}

	public void testZipComparison() {
		changedMensa = builder.setZip("3123").build();
		assertNotEquals(mensa, changedMensa);
	}

	public void testTimestampComparison() {
		changedMensa = builder.setTimestamp(98976213).build();
		assertNotEquals(mensa, changedMensa);
	}

	public void testLongitudeComparison() {
		changedMensa = builder.setLongitude(12.123).build();
		assertNotEquals(mensa, changedMensa);
	}

	public void testLatitudeComparison() {
		changedMensa = builder.setLatitude(62.123).build();
		assertNotEquals(mensa, changedMensa);
	}

	public void testWeeklyplanComparison() {
		changedMensa = builder.build();
		changedMensa.setMenuplan(new WeeklyMenuplan());
		assertNull(mensa.getMenuplan());
		assertNotNull(changedMensa.getMenuplan());
		assertNotEquals(mensa, changedMensa);

		mensa = changedMensa;
		changedMensa = builder.build();
		WeeklyMenuplan plan = new WeeklyMenuplan();
		Menu menu = new Menu.MenuBuilder().setDate(new Day(28, 10, 2013)).setTitle("menu title").build();
		plan.addMenu(menu);
		changedMensa.setMenuplan(plan);
		assertNotEquals(mensa, changedMensa);
		
		//tests if weeklyplans are compared recursivly
		WeeklyMenuplan plan2 = new WeeklyMenuplan();
		plan2.addMenu(menu);
		assertNotEquals(mensa, changedMensa);
	}
}