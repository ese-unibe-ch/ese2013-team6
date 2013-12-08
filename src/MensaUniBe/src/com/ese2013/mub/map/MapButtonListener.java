package com.ese2013.mub.map;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.DrawerMenuActivity;
import com.ese2013.mub.model.Mensa;

public class MapButtonListener implements OnClickListener {
	private Mensa mensa;
	private Fragment target;

	public MapButtonListener(Mensa mensa, Fragment target) {
		this.mensa = mensa;
		this.target = target;
	}

	@Override
	public void onClick(View v) {
		((DrawerMenuActivity) target.getActivity()).displayMapAtMensa(mensa);
	}
}