package com.ese2013.mub;

import com.ese2013.mub.model.Mensa;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class FavoriteButtonListener implements OnClickListener {
	private Mensa mensa;
	
	public FavoriteButtonListener(Mensa mensa) {
		this.mensa = mensa;
	}
	@Override
	public void onClick(View viewIn) {
		mensa.setIsFavorite(!mensa.isFavorite());
		//Toast.makeText(, mensa.getName() + "is favorite now is: " + mensa.isFavorite(), Toast.LENGTH_SHORT).show();
	}
}