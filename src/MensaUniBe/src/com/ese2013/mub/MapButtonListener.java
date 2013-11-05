package com.ese2013.mub;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.ese2013.mub.model.Mensa;

public class MapButtonListener implements OnClickListener{
	private Mensa mensa;
	private Fragment target;

	public MapButtonListener(Mensa mensa, Fragment target){
		this.mensa = mensa;
		this.target = target;
	}
	@Override
	public void onClick(View v) {
		// Meiner meinung nach ist das austauschen der Fragmente die aufgabe der Activity!
		
		//FragmentManager fragmentManager = target.getFragmentManager();
    	//FragmentTransaction transaction = fragmentManager.beginTransaction();
    	//transaction.replace(R.id.drawer_layout_frag_container, mapFragment);
    	//transaction.addToBackStack(null);
    	//transaction.commit();
		
    	((DrawerMenuActivity) target.getActivity()).displayMapAtMensa(mensa);
        
		
	}
}
