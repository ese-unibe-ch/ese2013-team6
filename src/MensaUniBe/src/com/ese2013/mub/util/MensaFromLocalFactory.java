package com.ese2013.mub.util;

import java.util.Collections;
import java.util.List;

import android.database.sqlite.SQLiteException;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.MensaDataSource;

/**
 * This factory creates the list of Mensas from the local database.
 */
public class MensaFromLocalFactory extends AbstractMensaFactory {
	private MensaDataSource dataSource;
	private MenuManager menuManager;

	/**
	 * Creates a MensaFromLocalFactory.
	 * 
	 * @param dataSource
	 *            MensaDataSource which stores the local data. Must not be null
	 *            and properly initialized by calling init().
	 * @param menuManager
	 *            MenuManager to store all Menu instances. Must not be null.
	 */
	public MensaFromLocalFactory(MensaDataSource dataSource, MenuManager menuManager) {
		this.dataSource = dataSource;
		this.menuManager = menuManager;
	}

	/**
	 * Creates the list of Mensas from the MensaDataSource.
	 * 
	 * @throws MensaLoadException
	 *             if the database has severe errors.
	 * @return the List of Mensas (sorted by id) with WeeklyMenuplans containing
	 *         the currently served Menus.
	 */
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