package com.ese2013.mub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.ese2013.mub.social.FriendRequest;
import com.ese2013.mub.social.SocialManager;
import com.parse.ParseException;
/**
 * Listener for sending a {@link FriendRequest} in the FriendsList add friend Dialog
 *
 */
public class PositiveButtonListener implements OnClickListener {
	private EditText emailEdit;
	private Context context;

	public PositiveButtonListener(EditText edit, Context context) {
		this.emailEdit = edit;
		this.context = context.getApplicationContext();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		try {
			SocialManager.getInstance().sendFriendRequest(emailEdit.getText().toString());
			Toast.makeText(context, "Friend request sent", Toast.LENGTH_SHORT).show();
		} catch (ParseException e) {
			Toast.makeText(context, "Friend not Found", Toast.LENGTH_SHORT).show();
		}
	}
}