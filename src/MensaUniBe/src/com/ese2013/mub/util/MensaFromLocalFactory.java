package com.ese2013.mub.util;

import java.util.List;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.MensaDataSource;

public class MensaFromLocalFactory extends AbstractMensaFactory {
	private MensaDataSource dataSource = MensaDataSource.getInstance();

	@Override
	public List<Mensa> createMensaList() throws MensaLoadException {
		try {
			dataSource.open();
			List<Mensa> mensas = dataSource.loadMensaList();
			for (Mensa m : mensas) {
				WeeklyMenuplan p = dataSource.loadMenuplan(m.getId());
				m.setMenuplan(p);
			}
			return mensas;
		} catch (Exception e) {
			throw new MensaLoadException(e);
		} finally {
			dataSource.close();
		}
	}
}