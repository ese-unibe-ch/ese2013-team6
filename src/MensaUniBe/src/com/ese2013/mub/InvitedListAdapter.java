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
import com.ese2013.mub.model.Model;
import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.SocialManager;

/**
 * Adapter for the invitedList ListView. shows unanswered requests and accepted
 * requests
 */
public class InvitedListAdapter extends InvitationsBaseAdapter {
	private LayoutInflater inflater;
	private List<Invitation> invitations = new ArrayList<Invitation>();

	public InvitedListAdapter() {
		onNotifyChanges();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null)
			inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		SimpleDateFormat notToday = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
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
		invitations = SocialManager.getInstance().getReceivedInvitations();
		notifyDataSetChanged();
	}
}