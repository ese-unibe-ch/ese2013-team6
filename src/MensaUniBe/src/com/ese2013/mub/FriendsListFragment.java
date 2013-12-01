package com.ese2013.mub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendsListFragment extends Fragment implements
		InvitationFragments {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container,false);
		
		return view;
	}
	@Override
	public Fragment getInstance(){
		return this;
	}
}
