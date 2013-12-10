package com.ese2013.mub;

import android.app.Fragment;

import com.ese2013.mub.model.Mensa;

/**
 * Interface for Creating a common Method for Adding an Interface to
 * MensaListFieldListener
 * 
 */
public interface MensaFieldAdapter {
	/**
	 * Delegate intent to the {@link MensaFieldAdapter}'s {@link Fragment}
	 * @param mensa
	 */
	public void sendListToMenusIntent(Mensa mensa);

}
