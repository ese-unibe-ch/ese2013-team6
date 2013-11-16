package com.ese2013.mub.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;

public class CriteriaMatcher {
	private HashMap<Menu, List<Mensa>> container;
	private List<Mensa> temp;

	public CriteriaMatcher() {
		container = new HashMap<Menu, List<Mensa>>();
		temp = new ArrayList<Mensa>();
	}

	/**
	 * 
	 * @param criteriaSet
	 *            String set of criterias you want to match with
	 * @param mensas
	 *            List of Mensas you want to match the criterias with
	 * @return Hashmap where the key entry is a Menu and the values are the
	 *         lists of Mensas where the Menu is served in
	 */
	public HashMap<Menu, List<Mensa>> match(Set<String> criteriaSet, List<Mensa> mensas) {
		for (Mensa mensa : mensas) {
			WeeklyMenuplan weekly = mensa.getMenuplan();
			DailyMenuplan daily = weekly.getDailymenuplan(Day.today());

			for (Menu menu : daily.getMenus()) {
				for (String criteria : criteriaSet) {
					if (menu.getDescription().contains(criteria)) {
						if (!container.containsKey(menu)) {
							temp.clear();
							temp.add(mensa);
							container.put(menu, temp);
						} else {
							temp.clear();
							temp = container.get(menu);
							temp.add(mensa);
						}
					}
				}
			}
		}
		return container;
	}
}
