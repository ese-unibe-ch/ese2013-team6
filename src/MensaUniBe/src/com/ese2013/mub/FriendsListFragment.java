package com.ese2013.mub;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.User;

public class FriendsListFragment extends Fragment implements
		IFragmentsInvitation {
	
	private ListView friends;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container,false);
		friends = (ListView) view.findViewById(R.id.friends_list);
		friends.setAdapter(new FriendsListAdapter());
		setHasOptionsMenu(true);
		return view;
	}
	@Override
	public Fragment getInstance(){
		return this;
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.friend_list_menu, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.add_friend_button:
			//TODO addfriend;
			return true;
		default: 
			return super.onOptionsItemSelected(item);
		}	
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	private class FriendsListAdapter extends BaseAdapter{
		private List<User> friends = LoginService.getLoggedInUser().getFriends();
		private List<User> request;
		private LayoutInflater inflater;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
	        if (view == null) 
	        	inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        
	    	if(position < request.size()){
				view = inflater.inflate(R.layout.friend_request_layout, null);
				//request.get(position);
				TextView requestName = (TextView)view.findViewById(R.id.friend_name_request);
				//requestName.setText();
				ImageButton cancelRequestButton = (ImageButton)view.findViewById(R.id.cancel_request);
				ImageButton acceptRequestButton = (ImageButton)view.findViewById(R.id.accept_request);
	    	}
			else{
				view = inflater.inflate(R.layout.friend_entry_layout, null);
				User friend = friends.get(position - request.size() - 1);
				TextView friendName = (TextView)view.findViewById(R.id.friend_name);
				friendName.setText(friend.getNick());
				ImageButton deleteFriend = (ImageButton)view.findViewById(R.id.delete_friend);
				deleteFriend.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// Delete!
						
					}
				});
			}
	    	
			friends.get(position);
			return null;
		}

		@Override
		public int getCount() {
			return friends.size() + request.size();
		}

		@Override
		public Object getItem(int position) {
			if(position <= request.size())
				return request.get(position);
			else
				return friends.get(position - request.size() - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}
}
