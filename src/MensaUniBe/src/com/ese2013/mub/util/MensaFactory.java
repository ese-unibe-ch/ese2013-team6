package com.ese2013.mub.util;

import java.util.List;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.WeeklyMenuplan;

public class MensaFactory {

	/**
	 * Creates all Mensas which are stored in the local data on the phone. Also
	 * retrieves all menus for the created mensas using the MenuFactory.
	 * 
	 * @return ArrayList of Mensas. Is null if the mensas couldn't be retrieved
	 *         (happens only if either the data retrieved from the web service
	 *         is garbage or the DataManager has a bug).
	 */
	public List<Mensa> createMensaList() {
		try {
			List<Mensa> mensas = DataManager.getSingleton().loadMensaList();
			for (Mensa m : mensas) {
				WeeklyMenuplan p = DataManager.getSingleton().loadWeeklyMenuplan(m.getId());
				m.setMenuplan(p);
			}
			return mensas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}