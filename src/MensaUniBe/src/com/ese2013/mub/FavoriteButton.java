package com.ese2013.mub;

import com.ese2013.mub.model.Mensa;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class FavoriteButton extends ImageButton{
	private Mensa mensa;
	public FavoriteButton(Context context, AttributeSet attrs, int defStyle ) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FavoriteButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FavoriteButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public void setMensa(Mensa mensa){
		this.mensa = mensa;
	}
}
