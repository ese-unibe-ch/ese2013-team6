package com.ese2013.mub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.ese2013.mub.social.FriendRequest;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.User;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class PositiveButtonListener implements OnClickListener {
	
	private EditText edit;
	private OnlineDBHandler onlineDBHandler;
	private Context context;
	
	public PositiveButtonListener(EditText edit, Context context) {
		this.edit = edit;
		this.context = context.getApplicationContext();
		onlineDBHandler = new OnlineDBHandler();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		String email = edit.getText().toString();
		User friend = new User(email);
		try {
			onlineDBHandler.getUser(friend);
		} catch (ParseException e) {
			Toast.makeText(context, "Friend not Found", Toast.LENGTH_SHORT).show();
		}
		try {
			onlineDBHandler.sendFriendRequest(new FriendRequest(LoginService.getLoggedInUser(), friend));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO implement to SocialManager.getInstance().
	}

}
