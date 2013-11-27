package com.ese2013.mub;

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;

public class MenuView extends LinearLayout {
	private String menuTitle;
	private String menuDesc;
	//private float averageMenuRating; // needs value
	
	public MenuView(Context context, final Menu menu) {
		super(context);
		setOrientation(VERTICAL);
		setPadding(0, 0, 0, dimToPixels(R.dimen.menu_view_bottom_margin));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.menu_view, this);
		menuTitle = menu.getTitle();
		menuDesc = menu.getDescription();
		
		menuTitle = menuTitle.toUpperCase(Locale.getDefault());
		setTitle(menuTitle);
		setDescription(menuDesc, view);
		
		RatingBar ratingBar = (RatingBar)view.findViewById(R.id.menu_rating_bar);
		ratingBar.setRating(menu.getAvarageRating());
		ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){ 
		       @Override
		       public void onRatingChanged(RatingBar ratingBar, float rating,
		         boolean fromUser) {
		        // TODO Do stuff
		    	// you can make ratingBar to not listen anymore with setIsIndicator(true);
		    	   
		    	   MenuManager.updateMenuRating(menu, (int) rating);
		       }}); 
		    
	}

	public MenuView(Context context) {
		super(context);
	}

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void setTitle(String menuTitle) {
		TextView menuTitleText = (TextView) getChildAt(0);
		menuTitleText.setText(menuTitle);
		menuTitleText.setBackgroundColor(getTitleColor(menuTitle));
	}
	
	private void setDescription(String menuDesc, View view) {
		TextView menuDescView = (TextView) view.findViewById(R.id.menu_description_text);
		menuDescView.setText(menuDesc);
	}

	// TODO there should be a cleaner way to map titles to colors
	private int getTitleColor(String title) {
		if (title.contains("VEGI") || title.contains("VEGETARISCH"))
			return getResources().getColor(R.color.green);

		if (title.contains("EINFACH GUT") || title.contains("TAGESGERICHT") || title.contains("WARMES SCH�SSELGERICHT"))
			return getResources().getColor(R.color.yellow);

		return getResources().getColor(R.color.blue);
	}

	private int dimToPixels(int dim) {
		return (int) getResources().getDimension(dim);
	}
}
