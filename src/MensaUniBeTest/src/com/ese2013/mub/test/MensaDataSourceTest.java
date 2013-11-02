package com.ese2013.mub.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.test.AndroidTestCase;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.MensaDataSource;

public class MensaDataSourceTest extends AndroidTestCase {

	MensaDataSource dataSource = new MensaDataSource(getContext());

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataSource = new MensaDataSource(getContext());
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
		assertTrue("Loaded mensas should be the same as stored", mensaListEquals(loadedMensas, mensas));
	}

	private boolean mensaListEquals(List<Mensa> list1, List<Mensa> list2) {
		if (list1.size() != list2.size())
			return false;
		for (int i = 0; i < list1.size(); i++)
			if (!list1.get(i).equals(list2.get(i)))
				return true;
		return true;
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

	public void testLoadMensaTimestamp() {
		List<Mensa> mensas = createMensaList();
		dataSource.storeMensaList(mensas);

		for (Mensa m : mensas)
			assertEquals(m.getTimestamp(), dataSource.getMensaTimestamp(m.getId()));
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
		
//		Set<Date> daysLoaded = dataSource.loadMenuplan(m1.getId()).getDays();
//		for (Date d : daysLoaded) {
//			System.out.println(d.getTime());
//		}
//		System.out.println("ORIGINAL");
//		for (Date d : m1.getMenuplan().getDays()) {
//			System.out.println(d.getTime());
//		}

		assertEquals(m1.getMenuplan(), dataSource.loadMenuplan(m1.getId()));
		assertEquals(m2.getMenuplan(), dataSource.loadMenuplan(m2.getId()));
	}

	private ArrayList<Mensa> createMensaList() {
		ArrayList<Mensa> mensas = new ArrayList<Mensa>();
		Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
		builder.setId(1).setName("Mensa Gesellschaftsstrasse").setStreet("Some street 123").setZip("3001 Bern")
				.setLongitude(45.123).setLatitude(12.412).setIsFavorite(false).setTimestamp(123456789);
		mensas.add(builder.build());

		builder = new Mensa.MensaBuilder();
		builder.setId(2).setName("Mensa Unitobler").setStreet("Some other street 123").setZip("3005 Bern")
				.setLongitude(45.1).setLatitude(13.512).setIsFavorite(false).setTimestamp(123456789);
		mensas.add(builder.build());

		builder = new Mensa.MensaBuilder();
		builder.setId(3).setName("Mensa Von Roll").setStreet("Some street 42").setZip("3012 Bern").setLongitude(43.123)
				.setLatitude(12.212).setIsFavorite(false).setTimestamp(342312311);
		mensas.add(builder.build());

		assertNotNull(mensas);
		assertTrue(mensas.size() == 3);
		for (Mensa m : mensas)
			assertNotNull(m);
		return mensas;
	}

	private ArrayList<WeeklyMenuplan> createWeeklyplans() {
		ArrayList<WeeklyMenuplan> plans = new ArrayList<WeeklyMenuplan>();
		ArrayList<Menu> menus = new ArrayList<Menu>();

		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(0);

		calendar.set(2013, 10, 20);
		Menu.MenuBuilder builder = new Menu.MenuBuilder();
		builder.setTitle("Vegi").setDescription("Something \n Served with some other Stuff").setHash(6234435)
				.setDate(calendar.getTime());
		menus.add(builder.build());
		builder.setTitle("Nice Menu").setDescription("Something nice\n Served with nothing else").setHash(123125)
				.setDate(calendar.getTime());
		menus.add(builder.build());

		calendar.set(2013, 10, 21);
		builder.setTitle("Vegi").setDescription("Something vegetarian").setHash(4566341).setDate(calendar.getTime());
		menus.add(builder.build());
		builder.setTitle("Expensive Menu").setDescription("Very expensive food").setHash(124523).setDate(calendar.getTime());
		menus.add(builder.build());

		calendar.set(2013, 10, 22);
		builder.setTitle("Nice Vegi Menu").setDescription("Something nice vegetarian").setHash(3123412)
				.setDate(calendar.getTime());
		menus.add(builder.build());
		builder.setTitle("Special Menu").setDescription("Pizza").setHash(924523).setDate(calendar.getTime());
		menus.add(builder.build());

		WeeklyMenuplan plan1 = new WeeklyMenuplan();
		plan1.addMenu(menus.get(0));
		plan1.addMenu(menus.get(1));
		plan1.addMenu(menus.get(2));
		plan1.addMenu(menus.get(4));

		WeeklyMenuplan plan2 = new WeeklyMenuplan();
		plan2.addMenu(menus.get(0));
		plan2.addMenu(menus.get(1));
		plan2.addMenu(menus.get(3));
		plan2.addMenu(menus.get(5));

		plans.add(plan1);
		plans.add(plan2);
		return plans;
	}

}
