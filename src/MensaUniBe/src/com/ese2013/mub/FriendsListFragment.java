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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

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
	class FriendsListAdapter extends BaseAdapter{
		List<User> friends;
		List<User> request;
		LayoutInflater inflater;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
	        if (view == null) 
	        	inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			friends.get(position);
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
