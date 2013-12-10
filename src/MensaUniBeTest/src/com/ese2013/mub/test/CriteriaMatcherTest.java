package com.ese2013.mub.test;

import static com.ese2013.mub.test.Util.assertNotEquals;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.service.Criteria;
import com.ese2013.mub.service.CriteriaMatcher;

public class CriteriaMatcherTest extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testMatchCriteria() {

		Mensa mensa1 = new Mensa.MensaBuilder().setId(0).setIsFavorite(true).setName("Mensa no. 1").build();
		Mensa mensa2 = new Mensa.MensaBuilder().setId(1).setIsFavorite(true).setName("Mensa no. 2").build();
		Mensa mensa3 = new Mensa.MensaBuilder().setId(2).setIsFavorite(true).setName("Mensa no. 3").build();

		WeeklyMenuplan plan1 = new WeeklyMenuplan();
		WeeklyMenuplan plan2 = new WeeklyMenuplan();
		WeeklyMenuplan plan3 = new WeeklyMenuplan();

		Menu menu1 = new Menu.MenuBuilder().setDescription("Pommes und Schnitzel").setTitle("Menu").build();
		Menu menu2 = new Menu.MenuBuilder().setDescription("Teigwaren und Schnitzel").setTitle("Menu").build();
		Menu menu3 = new Menu.MenuBuilder().setDescription("Rösti und Geschnetzeltes").setTitle("Menu").build();

		Menu menu4 = new Menu.MenuBuilder().setDescription("Pommes und Schnitzel").setTitle("Menu").build();
		Menu menu5 = new Menu.MenuBuilder().setDescription("Bratkartoffeln und Geschnetzeltes").setTitle("Menu").build();
		Menu menu6 = new Menu.MenuBuilder().setDescription("Kartoffeln und Fleisch").setTitle("Menu").build();

		Menu menu7 = new Menu.MenuBuilder().setDescription("Bratkartoffeln und Geschnetzeltes").setTitle("Menu").build();
		Menu menu8 = new Menu.MenuBuilder().setDescription("Bratkartoffeln und Fleisch").setTitle("Menu").build();

		plan1.add(menu1, Day.today());
		plan1.add(menu2, Day.today());
		plan1.add(menu3, Day.today());
		plan2.add(menu4, Day.today());
		plan2.add(menu5, Day.today());
		plan2.add(menu6, Day.today());
		plan3.add(menu7, Day.today());
		plan3.add(menu8, Day.today());

		mensa1.setMenuplan(plan1);
		mensa2.setMenuplan(plan2);
		mensa3.setMenuplan(plan3);

		List<Mensa> mensas = new ArrayList<Mensa>();
		mensas.add(mensa1);
		mensas.add(mensa2);
		mensas.add(mensa3);

		Set<String> criteria = new LinkedHashSet<String>();
		criteria.add("Schnitzel");
		criteria.add("Fleisch");
		criteria.add("Pommes");
		criteria.add("kommt nicht vor");
		criteria.add("bogus");

		CriteriaMatcher matcher = new CriteriaMatcher();
		List<Criteria> matchedCriterias = matcher.match(criteria, mensas);

		assertEquals(3, matchedCriterias.size());

		// Tests for crit1
		Criteria crit1 = matchedCriterias.get(0);
		assertEquals("Schnitzel", crit1.getCriteraName());

		assertTrue(crit1.getMap().keySet().size() == 2);

		// assert containing menus for crit1
		assertTrue(crit1.getMap().containsKey(menu1));
		assertTrue(crit1.getMap().containsKey(menu2));
		assertFalse(crit1.getMap().containsKey(menu3));
		assertTrue(crit1.getMap().containsKey(menu4));
		assertFalse(crit1.getMap().containsKey(menu5));
		assertFalse(crit1.getMap().containsKey(menu6));
		assertFalse(crit1.getMap().containsKey(menu7));
		assertFalse(crit1.getMap().containsKey(menu8));

		List<Mensa> mensasCrit1 = crit1.getMap().get(menu1);
		assertEquals(2, mensasCrit1.size());

		assertTrue(mensasCrit1.contains(mensa1));
		assertTrue(mensasCrit1.contains(mensa2));
		assertFalse(mensasCrit1.contains(mensa3));

		// Tests for crit2
		Criteria crit2 = matchedCriterias.get(1);
		assertEquals("Fleisch", crit2.getCriteraName());

		// assert containing menus for crit2
		assertFalse(crit2.getMap().containsKey(menu1));
		assertFalse(crit2.getMap().containsKey(menu2));
		assertFalse(crit2.getMap().containsKey(menu3));
		assertFalse(crit2.getMap().containsKey(menu4));
		assertFalse(crit2.getMap().containsKey(menu5));
		assertTrue(crit2.getMap().containsKey(menu6));
		assertFalse(crit2.getMap().containsKey(menu7));
		assertTrue(crit2.getMap().containsKey(menu8));

		List<Mensa> mensasCrit2 = crit2.getMap().get(menu6);
		assertEquals(1, mensasCrit2.size());

		assertFalse(mensasCrit2.contains(mensa1));
		assertTrue(mensasCrit2.contains(mensa2));
		assertFalse(mensasCrit2.contains(mensa3));

		// Tests for crit3
		Criteria crit3 = matchedCriterias.get(2);
		assertEquals("Pommes", crit3.getCriteraName());

		// assert containing menus for crit1
		assertTrue(crit3.getMap().containsKey(menu1));
		assertFalse(crit3.getMap().containsKey(menu2));
		assertFalse(crit1.getMap().containsKey(menu3));
		assertTrue(crit3.getMap().containsKey(menu4));
		assertFalse(crit1.getMap().containsKey(menu5));
		assertFalse(crit1.getMap().containsKey(menu6));
		assertFalse(crit1.getMap().containsKey(menu7));
	}

	public void testCriteriaEquals() {
		Criteria crit1 = new Criteria();
		crit1.setCriteriaName("Fleisch");
		Criteria crit2 = new Criteria();
		crit2.setCriteriaName("Fleisch");
		Criteria crit3 = new Criteria();
		crit3.setCriteriaName("schielF");

		assertEquals(crit1, crit2);
		assertNotEquals(crit1, crit3);
		assertNotEquals(crit2, crit3);
	}
}
