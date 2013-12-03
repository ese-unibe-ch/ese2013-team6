package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;

public class InvitationButtonListener implements OnClickListener {

	private Mensa mensa;
	private Day day;
	private DailyPlanFragment target;

	public InvitationButtonListener(Mensa mensa, Day dayOfInvitation, DailyPlanFragment dailyPlanFragment) {
		this.mensa = mensa;
		this.day = dayOfInvitation;
		this.target = dailyPlanFragment;
	}

	@Override
	public void onClick(View arg0) {

		((DrawerMenuActivity)target.getActivity()).createInvitation(mensa, day);
	}
}