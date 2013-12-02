package com.ese2013.mub;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.ese2013.mub.social.FriendRequest;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.User;
import com.ese2013.mub.util.parseDatabase.OnlineDBHandler;
import com.parse.ParseException;

public class FriendsListFragment extends Fragment implements
		IFragmentsInvitation {
	
	private ListView friends;
	private FriendsListAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container,false);
		friends = (ListView) view.findViewById(R.id.friends_list);
		adapter = new FriendsListAdapter();
		friends.setAdapter(adapter);
		
		View emptyView = inflater.inflate(R.layout.empty_friends_list_view, null);
		TextView showMessage = (TextView)emptyView.findViewById(R.id.no_friends_text_view);
		
		if(LoginService.isLoggedIn())
			showMessage.setText(R.string.no_friends);
		else
			showMessage.setText(R.string.not_loged_in);
		
		friends.setEmptyView(emptyView);
		
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
			
			
			//TODO
			((DrawerMenuActivity) getActivity()).goToAddFriendFragment();
			return true;
		default: 
			return super.onOptionsItemSelected(item);
		}	
	}
	@Override
	public void onPause() {
		onDestroyOptionsMenu();
		super.onPause();
	}
	@Override
	public void onDestroy() {
		onDestroyOptionsMenu();
		super.onDestroy();
	}
	private class FriendsListAdapter extends BaseAdapter{
		private List<User> friends = new ArrayList<User>();
		private List<FriendRequest> request = new ArrayList<FriendRequest>();
		private LayoutInflater inflater;
		private OnlineDBHandler onlineDBHandler = new OnlineDBHandler(); 
		public FriendsListAdapter(){
			super();
			if(LoginService.isLoggedIn()){
			try {
				request = onlineDBHandler.getFriendRequests(LoginService.getLoggedInUser());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			friends = LoginService.getLoggedInUser().getFriends();
			}
		}
		@Override
		public void notifyDataSetChanged() {
			if(LoginService.isLoggedIn()){
			try {
				
				request = onlineDBHandler.getFriendRequests(LoginService.getLoggedInUser());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			friends = LoginService.getLoggedInUser().getFriends();
		}
			super.notifyDataSetChanged();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
	        if (view == null)
	        	inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    	if(position < request.size()){
				view = inflater.inflate(R.layout.friend_request_layout, null);
				FriendRequest friendRequest = request.get(position);
				TextView requestName = (TextView)view.findViewById(R.id.friend_name_request);
				requestName.setText(friendRequest.getFrom().getNick());
				ImageButton cancelRequestButton = (ImageButton)view.findViewById(R.id.cancel_request);
				cancelRequestButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//onlineDBHandler.
						notifyDataSetChanged();
					}
				});
				ImageButton acceptRequestButton = (ImageButton)view.findViewById(R.id.accept_request);
				acceptRequestButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO acceptReqest;
						notifyDataSetChanged();
					}
				});
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
						notifyDataSetChanged();
						
					}
				});
			}
	    	
			friends.get(position);
			return view;
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
