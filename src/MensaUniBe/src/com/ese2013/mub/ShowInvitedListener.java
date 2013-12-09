package com.ese2013.mub;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.User;
/**
 * 
 * Listens to the ShowInvided Button in the {@link InvitesFragment}
 * and shows them in a Dialog with the Responses
 *
 */
public class ShowInvitedListener implements OnClickListener {
	private Invitation invite;
	private Context context;

	public ShowInvitedListener(Context context, Invitation invite) {
		this.invite = invite;
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		new AlertDialog.Builder(context).setTitle("Responses").setAdapter(new ResponsesListAdapter(), null)
				.setPositiveButton(android.R.string.ok, null).show();
	}
	/**
	 * creates a row for the AlarmDialog in the {@link ShowInvitedListener}.
	 * It shows the name of the Invited and his/her response
	 *
	 */
	private class ResponsesListAdapter extends BaseAdapter {
		private List<User> invitedFriends = new ArrayList<User>();
		private LayoutInflater inflater;

		public ResponsesListAdapter() {
			super();
			this.invitedFriends = new ArrayList<User>(invite.getTo());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null)
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = inflater.inflate(R.layout.response_entry_layout, null);

			User friend = invitedFriends.get(position);

			TextView friendName = (TextView) view.findViewById(R.id.friend_name);
			friendName.setText(friend.getNick());

			ImageView statusImage = (ImageView) view.findViewById(R.id.response_icon);
			statusImage.setClickable(false);
			statusImage.setEnabled(false);

			switch (invite.getResponseOf(friend)) {
			case ACCEPTED:
				statusImage.setImageDrawable(context.getResources().getDrawable(R.drawable.accept));
				break;
			case DECLINED:
				statusImage.setImageDrawable(context.getResources().getDrawable(R.drawable.cancel));
				break;
			case UNKNOWN:
				statusImage.setImageDrawable(context.getResources().getDrawable(R.drawable.unknown));
				break;
			}

			return view;
		}

		@Override
		public int getCount() {
			return invitedFriends.size();
		}

		@Override
		public Object getItem(int position) {
			return invitedFriends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}
}
