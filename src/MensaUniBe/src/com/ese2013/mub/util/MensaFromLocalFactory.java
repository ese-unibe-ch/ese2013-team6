package com.ese2013.mub.util;

import java.util.List;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.WeeklyMenuplan;

public class MensaFromLocalFactory extends AbstractMensaFactory {
	private DataManager dataManager = DataManager.getSingleton();

	@Override
	public List<Mensa> createMensaList() throws MensaLoadException {
		try {
			List<Mensa> mensas = dataManager.loadMensaList();
			for (Mensa m : mensas) {
				WeeklyMenuplan p = dataManager.loadWeeklyMenuplan(m.getId());
				m.setMenuplan(p);
			}
			return mensas;
		} catch (Exception e) {
			throw new MensaLoadException(e);
		} finally {
			dataManager.closeOpenResources();
		}
	}
}