package com.ese2013.mub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.Criteria;

public class CriteriaMatcher {
	private List<Criteria> container;
	private List<Mensa> temp;

	public CriteriaMatcher() {
		container = new ArrayList<Criteria>();
		temp = new ArrayList<Mensa>();
	}

	/**
	 * 
	 * @param criteriaSet
	 *            String set of criterias you want to match with.
	 * @param mensas
	 *            List of Mensas you want to match the criterias with.
	 * @return List of Criteria Object wich stores the matching menus and the mensa in which the menu is served in.
	 */
	public List<Criteria> match(Set<String> criteriaSet, List<Mensa> mensas) {
		for (Mensa mensa : mensas) {
			WeeklyMenuplan weekly = mensa.getMenuplan();
			DailyMenuplan daily = weekly.getDailymenuplan(Day.today());

			for (Menu menu : daily.getMenus()) {
				for (String criteria : criteriaSet) {
					Criteria crit = new Criteria();
					crit.setName(criteria);
					
					if (menu.getDescription().contains(criteria)) {
						if (!crit.getMap().containsKey(menu)) {
							temp.clear();
							temp.add(mensa);
							crit.getMap().put(menu, temp);
						} else {
							temp.clear();
							temp = crit.getMap().get(menu);
							temp.add(mensa);
						}
					}
					container.add(crit);
				}
			}
		}
		return container;
	}
}
