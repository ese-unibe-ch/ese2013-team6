package com.ese2013.mub;

import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.SocialManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This simple abstract class has the functionality which is common for both
 * Invited and Sent Invites Fragment.
 */
public abstract class AbstractInvitationsFragment extends Fragment {
	private ListView invitedList;
	private InvitationsBaseAdapter adapter;
	private MenuItem menuItem;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		adapter = createAdapter();
		View view = inflater.inflate(R.layout.fragment_invited, null);
		invitedList = (ListView) view.findViewById(R.id.invited_list);
		setUpEmptyView(view);
		invitedList.setAdapter(adapter);
		setHasOptionsMenu(true);
		return view;
	}

	protected abstract InvitationsBaseAdapter createAdapter();

	protected void setUpEmptyView(View view) {
		TextView showMessage = (TextView) view.findViewById(R.id.show_message);
		if (LoginService.isLoggedIn())
			showMessage.setText(R.string.no_invites);
		else
			showMessage.setText(R.string.not_loged_in);

		invitedList.setEmptyView(showMessage);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (LoginService.isLoggedIn())
			inflater.inflate(R.menu.invitations_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_invite_button:
			((DrawerMenuActivity) getActivity()).createInvitation();
			return true;
		case R.id.refresh:
			SocialManager.getInstance().loadInvites();
			menuItem = item;
			menuItem.setActionView(R.layout.progress_bar);
			menuItem.expandActionView();
			Toast.makeText(getActivity(), R.string.toast_refreshing_msg, Toast.LENGTH_SHORT).show();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SocialManager.getInstance().removeObserver(adapter);
	}

	/**
	 * changes the progressbar in the ActionBar back to not loading
	 */
	protected void loadingFinished() {
		if (menuItem != null) {
			menuItem.collapseActionView();
			menuItem.setActionView(null);
		}
	}
}
