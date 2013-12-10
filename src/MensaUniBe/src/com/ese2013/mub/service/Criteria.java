package com.ese2013.mub.service;

import java.util.HashMap;
import java.util.List;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;

/**
 * 
 * Criteria object, contains the criteria itself as a String and a HashMap<Menu,
 * List<Mensa> containing all the Menus and the belonging Mensas. The HashMap is
 * easy accessible in order to allow the CriteriaMatcher algorithm to fill the
 * map continuously
 * 
 */
public class Criteria {

	private HashMap<Menu, List<Mensa>> map;
	private String criteria;

	public Criteria() {
		map = new HashMap<Menu, List<Mensa>>();
		criteria = null;
	}

	
	public String getCriteraName() {
		return criteria;
	}

	/**
	 * Gives access to the HashMap. This HashMap needs to be
	 * mutable in order for the CriteriaMatcher Algorithm to work properly.
	 * 
	 * @return Representation of the HashMap with all the <Menu, List<Mensa>> entries.
	 */
	public HashMap<Menu, List<Mensa>> getMap() {
		return map;
	}

	public void setCriteriaName(String name) {
		this.criteria = name;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Criteria) {
			Criteria other = (Criteria) object;
			if (object == this)
				return true;
			if (!other.getCriteraName().equals(criteria))
				return false;
			return true;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + criteria.hashCode();
		return result;
	}

}
