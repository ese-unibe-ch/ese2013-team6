package com.ese2013.mub.util;

import java.util.List;

import com.ese2013.mub.model.Mensa;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class ModelLoader extends AsyncTaskLoader<List<Mensa>> {

	private List<Mensa> mensaData;

	public ModelLoader(Context ctx) {
		super(ctx);
	}

	@Override
	public List<Mensa> loadInBackground() {
		MensaFactory fac = new MensaFactory();
		List<Mensa> mensas = fac.createMensaList();
		return mensas;
	}

	@Override
	public void deliverResult(List<Mensa> newData) {
		//if reset, "delete" data
		if (isReset()) {
			mensaData = null;
			return;
		}
		mensaData = newData;
		if (isStarted()) {
			super.deliverResult(newData);
		}
	}

	@Override
	protected void onStartLoading() {
		// deliver already loaded data
		if (mensaData != null) {
			deliverResult(mensaData);
		}

		// force loading
		if (takeContentChanged() || mensaData == null) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		onStopLoading();
		mensaData = null;
	}
}
