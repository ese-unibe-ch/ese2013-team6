package com.ese2013.mub.util;

import java.util.List;

import android.os.AsyncTask;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;

/**
 * This class saves asynchronously all data from the model using the DataManager
 * class.
 */
public class ModelSavingTask extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... params) {
		DataManager dataManager = DataManager.getInstance();
		List<Mensa> mensas = Model.getInstance().getMensas();
		dataManager.storeMensaList(mensas);
		dataManager.deleteLocalMenus();
		for (Mensa m : mensas) {
			dataManager.storeWeeklyMenuplan(m);
		}
		return null;
	}
}
