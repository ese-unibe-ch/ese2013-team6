package com.ese2013.mub.model;

import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class ModelLoader extends AsyncTaskLoader<List<Mensa>> {

	private List<Mensa> mensaData;

	public ModelLoader(Context ctx) {
		super(ctx);
	}

	@Override
	public List<Mensa> loadInBackground() {
		Log.d("Model", "STARTING LOADER");
		MensaFactory fac = new MensaFactory();
		List<Mensa> mensas = fac.createMensaList();
		return mensas;
	}

	@Override
	public void deliverResult(List<Mensa> newData) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the
			// data.
			releaseResources(newData);
			return;
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// We must protect it until the new data has been delivered.
		List<Mensa> oldData = mensaData;
		mensaData = newData;

		if (isStarted()) {
			// If the Loader is in a started state, deliver the results to the
			// client. The superclass method does this for us.
			super.deliverResult(newData);
		}

		// Invalidate the old data as we don't need it any more.
		if (oldData != null && oldData != newData) {
			releaseResources(oldData);
		}
	}

	/*********************************************************/
	/** (3) Implement the Loader’s state-dependent behavior **/
	/*********************************************************/

	@Override
	protected void onStartLoading() {
		if (mensaData != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(mensaData);
		}

		if (takeContentChanged() || mensaData == null) {
			// When the observer detects a change, it should call
			// onContentChanged()
			// on the Loader, which will cause the next call to
			// takeContentChanged()
			// to return true. If this is ever the case (or if the current data
			// is
			// null), we force a new load.
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		// The Loader is in a stopped state, so we should attempt to cancel the
		// current load (if there is one).
		cancelLoad();

		// Note that we leave the observer as is. Loaders in a stopped state
		// should still monitor the data source for changes so that the Loader
		// will know to force a new load if it is ever started again.
	}

	@Override
	protected void onReset() {
		// Ensure the loader has been stopped.
		onStopLoading();

		// At this point we can release the resources associated with 'mData'.
		if (mensaData != null) {
			releaseResources(mensaData);
			mensaData = null;
		}
	}

	@Override
	public void onCanceled(List<Mensa> data) {
		// Attempt to cancel the current asynchronous load.
		super.onCanceled(data);

		// The load has been canceled, so we should release the resources
		// associated with 'data'.
		releaseResources(data);
	}

	private void releaseResources(List<Mensa> data) {
		//nothing to release
	}

	/*********************************************************************/
	/** (4) Observer which receives notifications when the data changes **/
	/*********************************************************************/

	// NOTE: Implementing an observer is outside the scope of this post (this
	// example
	// uses a made-up "SampleObserver" to illustrate when/where the observer
	// should
	// be initialized).

	// The observer could be anything so long as it is able to detect content
	// changes
	// and report them to the loader with a call to onContentChanged(). For
	// example,
	// if you were writing a Loader which loads a list of all installed
	// applications
	// on the device, the observer could be a BroadcastReceiver that listens for
	// the
	// ACTION_PACKAGE_ADDED intent, and calls onContentChanged() on the
	// particular
	// Loader whenever the receiver detects that a new application has been
	// installed.
	// Please don’t hesitate to leave a comment if you still find this
	// confusing! :)
	// private SampleObserver mObserver;
}
