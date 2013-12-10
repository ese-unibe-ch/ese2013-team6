package com.ese2013.mub;

import android.content.Context;
import android.widget.BaseAdapter;

import com.ese2013.mub.util.Observer;

/**
 * This class does only joins the BaseAdapter from Android and our Observer
 * interface. This class can also take a context which can then be used in the
 * view creation methods of the extending adapters.
 */
public abstract class InvitationsBaseAdapter extends BaseAdapter implements Observer {
	private Context context;

	public void setContext(Context context) {
		this.context = context;
	}

	protected Context getContext() {
		return context;
	}
}
