package com.ese2013.mub;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class ToggleListener implements OnClickListener {
	private View toToggle;
	private Context ctx;
	//private View title;
	public ToggleListener(View toToggle, Context ctx){
		this.toToggle = toToggle;
		this.ctx = ctx;
		//this.title = title;
	}
	@Override
	public void onClick(View v) {
		
		if(toToggle.isShown()){
			 ToggleAnimation.slide_up(ctx, toToggle);
			 toToggle.setVisibility(View.GONE);
		}
		else{
			ToggleAnimation.slide_down(ctx, toToggle);
			 toToggle.setVisibility(View.VISIBLE);
		}
			
	}

}