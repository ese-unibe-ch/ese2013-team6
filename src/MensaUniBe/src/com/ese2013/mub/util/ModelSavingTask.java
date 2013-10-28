package com.ese2013.mub.util;

import java.util.List;

import android.os.AsyncTask;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;

/**
 * This class loads asynchronously all data from the mensa web service and
 * stores it locally on the phone using the DataManager class.
 */
public class ModelSavingTask extends AsyncTask<Void, Void, Void> {
	private DataManager dataManager = DataManager.getSingleton();
	
	/**
	 * Downloads all menus and mensas from the web service. Does not provide a
	 * meaningful return value (always returns zero).
	 */
	@Override
	protected Void doInBackground(Void... params) {
		List<Mensa> mensas = Model.getInstance().getMensas();
		dataManager.storeMensaList(mensas);
		dataManager.deleteLocalMenus();
		for (Mensa m : mensas) {
			dataManager.storeWeeklyMenuplan(m);
		}
		return null;
	}
}
