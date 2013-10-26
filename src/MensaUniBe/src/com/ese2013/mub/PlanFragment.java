package com.ese2013.mub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlanFragment extends Fragment {
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@SuppressWarnings("deprecation")//because the our min api is lower than 14 and setBackground(drawable) needs 16
	public TextView getTextView(String title) {
		TextView text = new TextView(getActivity());
		text.setText(title);
		text.setBackgroundDrawable(getResources().getDrawable(R.drawable.section_list_item_selector));
		text.setPadding(0, 6, 0, 6);
		text.setHeight(10);
		//text.setGravity(TextView.);
		return text;
	}
	
}
