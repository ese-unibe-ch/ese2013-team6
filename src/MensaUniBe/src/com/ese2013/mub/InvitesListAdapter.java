package com.ese2013.mub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.SocialManager;

/**
 * Adapter for the {@link InvitesListAdapter} ListView. Creates rows in witch
 * you can check the state of your invite
 * 
 */
public class InvitesListAdapter extends InvitationsBaseAdapter {

	private LayoutInflater inflater;
	private List<Invitation> invitations = new ArrayList<Invitation>();

	public InvitesListAdapter() {
		onNotifyChanges();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null)
			inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.invites_entry_layout, null);
		Invitation invite = invitations.get(position);

		setUpWhereTextView(view, invite);
		setUpWhenTextView(view, invite);

		setUpShowInvitedButton(view, invite);

		return view;
	}

	private void setUpShowInvitedButton(View view, Invitation invite) {
		ImageButton showInvitedButton = (ImageButton) view.findViewById(R.id.show_invited);
		showInvitedButton.setOnClickListener(new ShowInvitedListener(getContext(), invite));
	}

	private void setUpWhenTextView(View view, Invitation invite) {
		TextView whenTextView = (TextView) view.findViewById(R.id.when_text_field);
		Day day = new Day(invite.getTime());

		SimpleDateFormat today = new SimpleDateFormat("HH:mm", Locale.getDefault());
		SimpleDateFormat notToday = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
		if (day.equals(Day.today()))
			whenTextView.setText("today: " + today.format(invite.getTime()));
		else
			whenTextView.setText(notToday.format(invite.getTime()));
	}

	private void setUpWhereTextView(View view, Invitation invite) {
		TextView whereTextView = (TextView) view.findViewById(R.id.where_text_field);
		Mensa mensa = null;
		for (Mensa m : Model.getInstance().getMensas()) {
			if (m.getId() == invite.getMensa())
				mensa = m;
		}
		whereTextView.setText(mensa.getName());
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
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		invitations = SocialManager.getInstance().getSentInvitations();
		super.notifyDataSetChanged();
	}
}