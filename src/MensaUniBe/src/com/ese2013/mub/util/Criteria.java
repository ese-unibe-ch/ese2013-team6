package com.ese2013.mub.util;

import java.util.HashMap;
import java.util.List;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;

public class Criteria {

	private HashMap<Menu, List<Mensa>> map;
	private String criteria;

	public Criteria() {
		map = new HashMap<Menu, List<Mensa>>();
		criteria = null;
	}

	public String getName() {
		return criteria;
	}

	public HashMap<Menu, List<Mensa>> getMap() {
		return map;
	}

	public void setName(String name) {
		this.criteria = name;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Criteria) {
			Criteria other = (Criteria) object;
			if (object == this)
				return true;
			if (!other.getName().equals(criteria))
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
