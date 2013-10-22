package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.ese2013.mub.model.Mensa;

public class FavoriteButtonListener implements OnClickListener {
	private Mensa mensa;
	private ImageButton button;
	
	public FavoriteButtonListener(Mensa mensa, ImageButton button) {
		this.mensa = mensa;
		this.button = button;
	}
	@Override
	public void onClick(View viewIn) {
		mensa.setIsFavorite(!mensa.isFavorite());
		//Toast.makeText(, mensa.getName() + "is favorite now is: " + mensa.isFavorite(), Toast.LENGTH_SHORT).show();
		if(!mensa.isFavorite())
		 button.setImageResource(R.drawable.ic_fav_grey);
		else
		 button.setImageResource(R.drawable.ic_fav);
	}
}