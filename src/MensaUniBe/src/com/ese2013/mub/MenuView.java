package com.ese2013.mub;

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuView extends LinearLayout {

	public MenuView(Context context, String menuTitle, String menuDesc) {
		super(context);
		setOrientation(VERTICAL);
		setPadding(0, 0, 0, dimToPixels(R.dimen.menu_view_bottom_margin));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.menu_view, this);

		menuTitle = menuTitle.toUpperCase(Locale.getDefault());
		setTitle(menuTitle);
		setDescription(menuDesc);
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
	
	private void setDescription(String menuDesc) {
		TextView menuDescView = (TextView) getChildAt(1);
		menuDescView.setText(menuDesc);
	}

	// TODO there should be a cleaner way to map titles to colors
	private int getTitleColor(String title) {
		if (title.equals("NATÜRLICH VEGI"))
			return getResources().getColor(R.color.green);

		if (title.equals("EINFACH GUT"))
			return getResources().getColor(R.color.yellow);

		return getResources().getColor(R.color.blue);
	}

	private int dimToPixels(int dim) {
		return (int) getResources().getDimension(dim);
	}
}
