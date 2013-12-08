package com.ese2013.mub.util;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.util.database.MensaDataSource;
import com.ese2013.mub.util.parseDatabase.MensaDBHandler;

/**
 * This factory creates the list of Mensas by downloading all Mensas and Menus
 * from the Parse-Server.
 */
public class MensaFromWebFactory extends AbstractMensaFactory {
	private MensaDataSource dataSource;
	private MenuManager menuManager;

	/**
	 * Creates a MensaFromWebFactory.
	 * 
	 * @param dataSource
	 *            MensaDataSource which is needed to look up if a Mensa is a
	 *            favorite Mensa. Must not be null and properly initialized by
	 *            calling init().
	 * @param menuManager
	 *            MenuManager to store all Menu instances. Must not be null.
	 */
	public MensaFromWebFactory(MensaDataSource dataSource, MenuManager menuManager) {
		this.menuManager = menuManager;
		this.dataSource = dataSource;
	}

	/**
	 * Creates the list of Mensas from the Parse-Server (synchronous). Also sets
	 * the "isFavorite" flag of the Mensas by looking at the local database.
	 * 
	 * @throws MensaDownloadException
	 *             if the download failed.
	 * 
	 * @return the List of Mensas (sorted by id) with WeeklyMenuplans containing
	 *         the currently served Menus.
	 */
	@Override
	public List<Mensa> createMensaList() throws MensaDownloadException {
		try {
			List<Mensa> mensas = new MensaDBHandler().getMensasAndMenus(menuManager);
			dataSource.open();
			for (Mensa mensa : mensas)
				mensa.setIsFavorite(dataSource.isInFavorites(mensa.getId()));
			Collections.sort(mensas);
			return mensas;
		} catch (ParseException e) {
			throw new MensaDownloadException(e);
		} catch (com.parse.ParseException e) {
			throw new MensaDownloadException(e);
		} finally {
			dataSource.close();
		}
	}
}