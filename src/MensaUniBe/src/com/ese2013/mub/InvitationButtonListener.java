package com.ese2013.mub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

		// Create new fragment and transaction
		Fragment invitationFragment = new InvitationFragment();
		Bundle args = new Bundle();
		args.putString("invitationDate", day.toString());
		args.putString("invitationMensa", mensa.getName());
		args.putString("invitationMenu", mensa.getMenuplan().getDailymenuplan(day).getMenus().get(0).getDescription());
		// Put any other arguments
		invitationFragment.setArguments(args);
		FragmentTransaction transaction = target.getActivity().getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.drawer_layout_frag_container, invitationFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
}