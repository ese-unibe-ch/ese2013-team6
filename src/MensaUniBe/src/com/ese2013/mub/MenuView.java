package com.ese2013.mub;

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuView extends LinearLayout {

	public MenuView(Context context, String menuTitle, String menuDesc) {
		super(context);
		setOrientation(VERTICAL);
		setPadding(0, 0, 0, dimToPixels(R.dimen.menu_view_bottom_margin));

		menuTitle = menuTitle.toUpperCase(Locale.getDefault());

		addTitle(menuTitle);
		addDescription(menuDesc);
	}

	public MenuView(Context context) {
		super(context);
	}

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void addDescription(String menuDesc) {
		TextView menuDescView = new TextView(getContext());
		menuDescView.setText(menuDesc);
		menuDescView.setBackgroundColor(0xFFFFFFFF);
		menuDescView.setGravity(Gravity.CENTER);
		int sidePadding = dimToPixels(R.dimen.menu_desc_side_padding);
		int verticalPadding = dimToPixels(R.dimen.menu_desc_vertical_padding);
		menuDescView.setPadding(sidePadding, verticalPadding, sidePadding, verticalPadding);
		this.addView(menuDescView);
	}

	private void addTitle(String menuTitle) {
		TextView menuTitleText = new TextView(getContext());
		menuTitleText.setText(menuTitle);
		menuTitleText.setTextColor(0xFFFFFFFF);
		menuTitleText.setBackgroundColor(getTitleColor(menuTitle));
		int titlePadding = dimToPixels((R.dimen.menu_view_title_padding));
		menuTitleText.setPadding(titlePadding, titlePadding, titlePadding, titlePadding);
		this.addView(menuTitleText);
	}

	//TODO there should be a cleaner way to map titles to colors
	private int getTitleColor(String title) {
		if (title.equals("NATÜRLICH VEGI"))
			return getResources().getColor(R.color.menu_view_color_green);

		if (title.equals("EINFACH GUT"))
			return getResources().getColor(R.color.menu_view_color_yellow);

		return getResources().getColor(R.color.menu_view_color_blue);
	}

	private int dimToPixels(int dim) {
		return (int) getResources().getDimension(dim);
	}
}
