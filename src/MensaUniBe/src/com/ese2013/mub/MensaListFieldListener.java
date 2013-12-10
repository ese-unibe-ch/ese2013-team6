package com.ese2013.mub;

import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.model.Mensa;

/**
 * Listens to Clicks on the MesaList mensa fields and returns it back to the
 * adapter to handle it
 * 
 */
public class MensaListFieldListener implements OnClickListener {
	private Mensa mensa;
	private MensaFieldAdapter adapt;

	public MensaListFieldListener(Mensa mensa, MensaFieldAdapter adapt) {
		this.mensa = mensa;
		this.adapt = adapt;
	}

	@Override
	public void onClick(View viewIn) {

		adapt.sendListToMenusIntent(mensa);
	}
}
