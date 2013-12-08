package com.ese2013.mub.util;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.util.database.MensaDataSource;
import com.ese2013.mub.util.parseDatabase.MensaDBHandler;

public class MensaFromWebFactory extends AbstractMensaFactory {
	private MensaDataSource dataSource;
	private MenuManager menuManager;

	public MensaFromWebFactory(MensaDataSource dataSource, MenuManager menuManager) {
		this.menuManager = menuManager;
		this.dataSource = dataSource;
	}

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