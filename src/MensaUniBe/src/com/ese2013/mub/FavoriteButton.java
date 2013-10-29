package com.ese2013.mub;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FavoriteButton{
	
	ImageButton button;
	
	public FavoriteButton(Context context, View view, ViewGroup container){
		View.inflate(context, R.id.button_favorite, container);
		button = (ImageButton) view.findViewById(R.id.button_favorite);
	}
	public ImageButton getButton(){
		return button;
	}
}
