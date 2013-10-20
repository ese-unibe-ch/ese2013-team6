package com.ese2013.mub;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.model.Mensa;

public class AddressTextListener implements OnClickListener{
private Mensa mensa;
private Context context;
private final String POSITION = "com.ese2013.mub.AdressTextListener";
	
	public AddressTextListener(Mensa mensa, Context context) {
		this.mensa = mensa;
		this.context = context;
	}
	@Override
	public void onClick(View viewIn) {
		Intent intent = new Intent(context, MenusByMensaViewFragment.class);
		intent.putExtra(POSITION, mensa.getId());
		//MenusByMensaViewFragment fragment = new ();
		//fragment.getActivity().setD;
		//Toast.makeText(, mensa.getName() + "is favorite now is: " + mensa.isFavorite(), Toast.LENGTH_SHORT).show();
	}
}
