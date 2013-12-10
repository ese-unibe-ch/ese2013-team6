package com.ese2013.mub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;

/**
 * 
 * This class works the CriteriaMatcher Algoritm and ensures 
 */
public class CriteriaMatcher {
	private List<Criteria> container;
	private List<Mensa> temp;

	public CriteriaMatcher() {
		container = new ArrayList<Criteria>();
		temp = new ArrayList<Mensa>();
	}

	/**
	 * Matches a Set of criteria to each {@link Menu} of the current day and returns a
	 * List of {@link Criteria} objects. The List contains a Criteria object for each
	 * matching criteria of the criteriaSet. The Algorithm also fills this
	 * Criteria object with all the Menus the String is matching and the mensas
	 * this Menu is served in. If criteriaSet or mensas are empty, an empty list is returned.
	 * Is either criteriaSet or mensas are null a {@link NullPointerException} may be thrown.
	 * 
	 * @param criteriaSet
	 *            String set of criterias you want to match with.
	 * @param mensas
	 *            List of {@link Mensa}s you want to match the criterias with.
	 * @return List of Criteria Objects which stores the matching menus and the
	 *         mensa in which the menu is served in.
	 */
	public List<Criteria> match(Set<String> criteriaSet, List<Mensa> mensas) {
		for (String criteria : criteriaSet) {
			Criteria crit = new Criteria();
			crit.setCriteriaName(criteria);
			for (Mensa mensa : mensas) {
				WeeklyMenuplan weekly = mensa.getMenuplan();
				DailyMenuplan daily = weekly.getDailymenuplan(Day.today());
				if (daily != null) {
					for (Menu menu : daily.getMenus()) {
						if ((menu.getDescription().toLowerCase(Locale.getDefault())).contains(criteria
								.toLowerCase(Locale.getDefault()))) {

							if (!container.contains(crit))
								container.add(crit);

							if (!crit.getMap().containsKey(menu)) {
								temp = new ArrayList<Mensa>();
								temp.add(mensa);
								crit.getMap().put(menu, temp);
							} else {
								temp = new ArrayList<Mensa>();
								temp = crit.getMap().get(menu);
								temp.add(mensa);

							}
						}
					}
				}
			}
		}
		return container;
	}
}
