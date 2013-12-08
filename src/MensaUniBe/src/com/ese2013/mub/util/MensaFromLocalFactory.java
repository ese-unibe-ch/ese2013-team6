package com.ese2013.mub.util;

import java.util.Collections;
import java.util.List;

import android.database.sqlite.SQLiteException;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.MensaDataSource;

public class MensaFromLocalFactory extends AbstractMensaFactory {
	private MensaDataSource dataSource;
	private MenuManager menuManager;

	public MensaFromLocalFactory(MensaDataSource dataSource, MenuManager menuManager) {
		this.dataSource = dataSource;
		this.menuManager = menuManager;
	}

	@Override
	public List<Mensa> createMensaList() throws MensaLoadException {
		try {
			dataSource.open();
			List<Mensa> mensas = dataSource.loadMensaList();
			for (Mensa m : mensas) {
				WeeklyMenuplan p = dataSource.loadMenuplan(m.getId(), menuManager);
				m.setMenuplan(p);
			}
			Collections.sort(mensas);
			return mensas;
		} catch (SQLiteException e) {
			throw new MensaLoadException(e);
		} finally {
			dataSource.close();
		}
	}
}