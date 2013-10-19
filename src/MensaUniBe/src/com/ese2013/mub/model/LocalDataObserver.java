package com.ese2013.mub.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class LocalDataObserver extends BroadcastReceiver {
	private ModelLoader loader;

	public LocalDataObserver(ModelLoader loader) {
		this.loader = loader;

		// Register for events related to application installs/removals/updates.
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		loader.getContext().registerReceiver(this, filter);

		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		loader.onContentChanged();
	}
}