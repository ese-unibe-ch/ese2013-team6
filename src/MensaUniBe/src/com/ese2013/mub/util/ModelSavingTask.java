package com.ese2013.mub.util;

import java.util.List;

import android.os.AsyncTask;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.util.database.MensaDataSource;

/**
 * This class saves asynchronously all data from the model using the
 * MensaDataSource class.
 */
public class ModelSavingTask extends AsyncTask<Void, Void, Void> {
	private MensaDataSource dataSource;
	private List<Mensa> mensas;

	/**
	 * Creates a ModelSavingTask.
	 * 
	 * @param dataSource
	 *            MensaDataSource to be used to save the model.
	 * @param mensas
	 *            List of mensas to be stored. Also the menus contained in their
	 *            WeeklyMenuplans are stored.
	 */
	public ModelSavingTask(List<Mensa> mensas, MensaDataSource dataSource) {
		this.dataSource = dataSource;
		this.mensas = mensas;
	}

	@Override
	protected Void doInBackground(Void... params) {
		dataSource.open();
		dataSource.storeMensaList(mensas);
		dataSource.deleteMenus();
		for (Mensa m : mensas)
			dataSource.storeWeeklyMenuplan(m);

		dataSource.close();
		return null;
	}
}
