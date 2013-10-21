package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

public class toggleListener implements OnClickListener {
	private View toToggle;
	//private View title;
	public toggleListener(View toToggle, View title){
		this.toToggle = toToggle;
		//this.title = title;
	}
	@Override
	public void onClick(View v) {
		toToggle.setVisibility( toToggle.isShown()
				? View.GONE
				: View.VISIBLE );

	}

}
