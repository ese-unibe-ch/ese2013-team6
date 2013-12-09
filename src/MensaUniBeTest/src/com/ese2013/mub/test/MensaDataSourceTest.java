package com.ese2013.mub.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.test.AndroidTestCase;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.MensaDataSource;

public class MensaDataSourceTest extends AndroidTestCase {

	private MensaDataSource dataSource;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataSource = MensaDataSource.getInstance();
		dataSource.init(getContext());
		assertNotNull(dataSource);
		dataSource.open();
		dataSource.cleanUpAllTables();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dataSource.close();
	}

	public void testStoreAndLoadMensas() {
		List<Mensa> mensas = createMensaList();
		dataSource.storeMensaList(mensas);
		List<Mensa> loadedMensas = dataSource.loadMensaList();
		assertEquals("Loaded mensas should be the same as stored", loadedMensas, mensas);
	}

	public void testStoreAndLoadFavorites() {
		List<Mensa> mensas = createMensaList();

		mensas.get(0).setIsFavorite(true);
		assertTrue(mensas.get(0).isFavorite());
		assertFalse(mensas.get(1).isFavorite());
		assertFalse(mensas.get(2).isFavorite());
		dataSource.storeFavorites(mensas);
		for (Mensa m : mensas) {
			assertEquals(m.isFavorite(), dataSource.isInFavorites(m.getId()));
		}

		mensas.get(0).setIsFavorite(true);
		mensas.get(1).setIsFavorite(true);
		assertTrue(mensas.get(0).isFavorite());
		assertTrue(mensas.get(1).isFavorite());
		assertFalse(mensas.get(2).isFavorite());
		dataSource.storeFavorites(mensas);
		for (Mensa m : mensas) {
			assertEquals(m.isFavorite(), dataSource.isInFavorites(m.getId()));
		}

		mensas.get(0).setIsFavorite(false);
		mensas.get(1).setIsFavorite(false);
		assertFalse(mensas.get(0).isFavorite());
		assertFalse(mensas.get(1).isFavorite());
		assertFalse(mensas.get(2).isFavorite());
		dataSource.storeFavorites(mensas);
		for (Mensa m : mensas) {
			assertEquals(m.isFavorite(), dataSource.isInFavorites(m.getId()));
		}
	}

	public void testStoreAndLoadMenuplan() {
		List<Mensa> mensas = createMensaList();
		List<WeeklyMenuplan> plans = createWeeklyplans();
		Mensa m1 = mensas.get(0);
		m1.setMenuplan(plans.get(0));
		Mensa m2 = mensas.get(2);
		m2.setMenuplan(plans.get(1));
		dataSource.storeWeeklyMenuplan(m1);
		dataSource.storeWeeklyMenuplan(m2);
		MenuManager menuManager = new MenuManager();
		assertEquals(m1.getMenuplan(), dataSource.loadMenuplan(m1.getId(), menuManager));
		assertEquals(m2.getMenuplan(), dataSource.loadMenuplan(m2.getId(), menuManager));
	}

	public void testGetWeekOfStoredMenus() {
		List<Mensa> mensas = createMensaList();
		List<WeeklyMenuplan> plans = createWeeklyplans();
		Mensa m1 = mensas.get(0);
		m1.setMenuplan(plans.get(0));
		Mensa m2 = mensas.get(2);
		m2.setMenuplan(plans.get(1));
		dataSource.storeWeeklyMenuplan(m1);
		dataSource.storeWeeklyMenuplan(m2);

		assertEquals(dataSource.getWeekOfStoredMenus(), plans.get(0).getWeekNumber());
		assertEquals(dataSource.getWeekOfStoredMenus(), plans.get(1).getWeekNumber());
	}

	public void testGetWeekOfStoredMenusIfDatabaseIsEmpty() {
		dataSource.cleanUpAllTables();
		assertEquals(-1, dataSource.getWeekOfStoredMenus());
	}

	private static ArrayList<Mensa> createMensaList() {
		ArrayList<Mensa> mensas = new ArrayList<Mensa>();
		Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
		builder.setId(1).setName("Mensa Gesellschaftsstrasse").setStreet("Some street 123").setZip("3001 Bern")
				.setLongitude(45.123).setLatitude(12.412).setIsFavorite(false);
		mensas.add(builder.build());

		builder = new Mensa.MensaBuilder();
		builder.setId(2).setName("Mensa Unitobler").setStreet("Some other street 123").setZip("3005 Bern")
				.setLongitude(45.1).setLatitude(13.512).setIsFavorite(false);
		mensas.add(builder.build());

		builder = new Mensa.MensaBuilder();
		builder.setId(3).setName("Mensa Von Roll").setStreet("Some street 42").setZip("3012 Bern").setLongitude(43.123)
				.setLatitude(12.212).setIsFavorite(false);
		mensas.add(builder.build());

		assertNotNull(mensas);
		assertTrue(mensas.size() == 3);
		for (Mensa m : mensas)
			assertNotNull(m);
		return mensas;
	}

	private static ArrayList<WeeklyMenuplan> createWeeklyplans() {
		ArrayList<WeeklyMenuplan> plans = new ArrayList<WeeklyMenuplan>();
		ArrayList<Menu> menus = new ArrayList<Menu>();

		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(0);

		calendar.set(2013, 10, 20);
		Menu.MenuBuilder builder = new Menu.MenuBuilder();
		builder.setId("a").setTitle("Vegi").setDescription("Something \n Served with some other Stuff");
		menus.add(builder.build());
		builder.setId("b").setTitle("Nice Menu").setDescription("Something nice\n Served with nothing else");
		menus.add(builder.build());

		calendar.set(2013, 10, 21);
		builder.setId("c").setTitle("Vegi").setDescription("Something vegetarian");
		menus.add(builder.build());
		builder.setId("d").setTitle("Expensive Menu").setDescription("Very expensive food");
		menus.add(builder.build());

		calendar.set(2013, 10, 22);
		builder.setId("e").setTitle("Nice Vegi Menu").setDescription("Something nice vegetarian");
		menus.add(builder.build());
		builder.setId("f").setTitle("Special Menu").setDescription("Pizza");
		menus.add(builder.build());

		WeeklyMenuplan plan1 = new WeeklyMenuplan();
		Day day = new Day(23, 11, 2013);
		plan1.add(menus.get(0), day);
		plan1.add(menus.get(1), day);
		plan1.add(menus.get(2), day);
		plan1.add(menus.get(4), day);

		WeeklyMenuplan plan2 = new WeeklyMenuplan();
		plan2.add(menus.get(0), day);
		plan2.add(menus.get(1), day);
		plan2.add(menus.get(3), day);
		plan2.add(menus.get(5), day);

		plans.add(plan1);
		plans.add(plan2);
		return plans;
	}

}
