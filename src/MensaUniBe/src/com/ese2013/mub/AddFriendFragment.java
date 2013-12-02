package com.ese2013.mub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.User;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class AddFriendFragment extends Fragment {
	private OnlineDBHandler onlineDBHandler = new OnlineDBHandler();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.add_friends_dialog, null);
		EditText editText = (EditText)view.findViewById(R.id.enter_name);
		ImageButton button = (ImageButton)view.findViewById(R.id.search_friend);
		button.setOnClickListener(new ButtonListener(editText));
		
		
		return view;
	}

	class ButtonListener implements OnClickListener{
		private EditText edit;
		public ButtonListener(EditText edit) {
			this.edit = edit;
		}
		@Override
		public void onClick(View v) {
			try {
				if(edit.getText().toString().length() == 0)
					throw new Exception();
				
				User user = new User(edit.getText().toString());
			
				user = onlineDBHandler.getUser(user);
			} catch (Exception e) {
				Toast.makeText(getActivity(), "Friend not Found", Toast.LENGTH_SHORT).show();
			}
			try {
				onlineDBHandler.addAsFriend(LoginService.getLoggedInUser(), edit.getText().toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			((DrawerMenuActivity) getActivity()).goBackToFriendsList();
		}
		
	}
}
