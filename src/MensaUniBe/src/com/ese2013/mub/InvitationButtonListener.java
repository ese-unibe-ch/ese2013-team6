package com.ese2013.mub;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;

public class InvitationButtonListener implements OnClickListener {
	private Mensa mensa;
	private Day day;
	private Fragment target;

	public InvitationButtonListener(Mensa mensa, Day dayOfInvitation, Fragment fragment) {
		this.mensa = mensa;
		this.day = dayOfInvitation;
		this.target = fragment;
	}

	@Override
	public void onClick(View arg0) {
		((DrawerMenuActivity) target.getActivity()).createInvitation(mensa, day);
	}
}