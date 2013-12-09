package com.ese2013.mub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.SocialManager;
import com.ese2013.mub.util.Observer;
/**
 * 
 * Page of the {@link InvitationBaseFragment}, shows where you are invited.
 *
 */
public class InvitedFragment extends Fragment {

	private ListView invitedList;
	private InvitedListAdapter adapter;
	private MenuItem menuItem;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		adapter = new InvitedListAdapter();
		View view = inflater.inflate(R.layout.fragment_invited, null);
		invitedList = (ListView) view.findViewById(R.id.invited_list);

		TextView showMessage = (TextView) view.findViewById(R.id.show_message);
		if (LoginService.isLoggedIn())
			showMessage.setText(R.string.no_invites);
		else
			showMessage.setText(R.string.not_loged_in);
		invitedList.setEmptyView(showMessage);

		invitedList.setAdapter(adapter);
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (LoginService.isLoggedIn())
			inflater.inflate(R.menu.invited_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
	private void loadingFinished() {
		if (menuItem != null) {
			menuItem.collapseActionView();
			menuItem.setActionView(null);
		}
	}
	/**
	 * Adapter for the invitedList ListView.
	 * shows unanswered requests and accepted requests 
	 * 
	 */
	private class InvitedListAdapter extends BaseAdapter implements Observer {
		private LayoutInflater inflater;
		private List<Invitation> invitations = new ArrayList<Invitation>();

		public InvitedListAdapter() {
			SocialManager.getInstance().addObserver(this);
			onNotifyChanges();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null)
				inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.invited_entry_layout, null);
			Invitation invite = invitations.get(position);

			setUpFromTextView(view, invite);
			setUpWhereTextView(view, invite);
			setUpWhenTextView(view, invite);
			setUpCancelButton(view, invite);
			setUpAcceptButton(view, invite);
			return view;
		}

		private void setUpWhenTextView(View view, Invitation invite) {
			TextView whenTextView = (TextView) view.findViewById(R.id.when_text_field);
			Day day = new Day(invite.getTime());
			
			SimpleDateFormat today = new SimpleDateFormat("HH:mm", Locale.getDefault());
			SimpleDateFormat notToday = new SimpleDateFormat("dd.MM.yyyy HH:mm",Locale.getDefault());
			if (day.equals(Day.today()))
				whenTextView.setText("today: " + today.format(invite.getTime()));
			else
				whenTextView.setText(notToday.format(invite.getTime()));
		}

		private void setUpFromTextView(View view, Invitation invite) {
			TextView fromWhomTextView = (TextView) view.findViewById(R.id.from_whom_text_field);
			fromWhomTextView.setText(invite.getFrom().getNick());

		}

		private void setUpWhereTextView(View view, Invitation invite) {
			TextView whereTextView = (TextView) view.findViewById(R.id.where_text_field);
			whereTextView.setText(Model.getInstance().getMensaById(invite.getMensa()).getName());
		}

		private void setUpCancelButton(View view, Invitation invite) {
			if (invite.getResponseOf(LoginService.getLoggedInUser()) == Invitation.Response.UNKNOWN) {
				ImageButton cancelRequestButton = (ImageButton) view.findViewById(R.id.cancel_invitation);
				cancelRequestButton.setImageResource(R.drawable.cancel);
				cancelRequestButton.setOnClickListener(new AnswerInviteListener(invite, false));
			}
		}

		private void setUpAcceptButton(View view, Invitation invite) {
			if (invite.getResponseOf(LoginService.getLoggedInUser()) == Invitation.Response.UNKNOWN) {
				ImageButton acceptRequestButton = (ImageButton) view.findViewById(R.id.accept_invitiation);
				acceptRequestButton.setImageResource(R.drawable.accept);
				acceptRequestButton.setOnClickListener(new AnswerInviteListener(invite, true));
			}
		}

		@Override
		public int getCount() {
			return invitations.size();
		}

		@Override
		public Object getItem(int position) {
			return invitations.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public void onNotifyChanges(Object... message) {
			this.invitations = SocialManager.getInstance().getReceivedInvitations();
			notifyDataSetChanged();
			loadingFinished();
		}
	}
}
