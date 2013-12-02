package com.ese2013.mub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InvitationFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		View test = super.onCreateView(inflater, container, savedInstanceState);
		View test =inflater.inflate(R.layout.fragment_invitation, container, false);
		final EditText emailToInvite = (EditText) test.findViewById(R.id.email_invite);
		TextView text = (TextView) test.findViewById(R.id.invitation_text);
		text.setText("Email of the Friend you want to invite");
		
		final String invitationDate = getArguments().getString("invitationDate");
		final String invitationMensa = getArguments().getString("invitationMensa");
		final String invitationMenu = getArguments().getString("invitationMenu");
		
		Button button = (Button) test.findViewById(R.id.send_invitation);
		button.setText("Invite");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				Editable email = emailToInvite.getText();
				final String sendTo = email.toString();
				i.putExtra(Intent.EXTRA_EMAIL,
						new String[] { sendTo });
				i.putExtra(Intent.EXTRA_SUBJECT, "You have been invited to go for lunch on the " + invitationDate +" to "+ invitationMensa);
				i.putExtra(Intent.EXTRA_TEXT, "Hello!" + "\n" + "You have been invited to go for lunch on the:  " + "\n" 
						+ invitationDate +"to: "+ "\n" 
						+ invitationMensa  + "\n" 
						+ "On the menu is for example: "+ "\n"
						+ invitationMenu);
				try {
					startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(getActivity(),
							"There are no email clients installed.",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		return test;
	}
}
