package com.ese2013.mub;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 
 * Listener which sets the viability of a view from visible to gone and vice versa
 *
 */
public class ToggleListener implements OnClickListener {
	private View toToggle;
	private Context ctx;
	
	public ToggleListener(View toToggle, Context ctx){
		this.toToggle = toToggle;
		this.ctx = ctx;
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