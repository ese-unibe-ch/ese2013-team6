package com.ese2013.mub;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ese2013.mub.model.Mensa;

public class MensaListItem extends LinearLayout {
	String name;
	Mensa mensa;
	
	public MensaListItem(Context context, Mensa mensa) {
		super(context);
		setOrientation(VERTICAL);
		setPadding(0, 0, 0, dimToPixels(R.dimen.menu_view_bottom_margin));
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.mensa_row, this);
		this.mensa = mensa;
		addDescription(mensa.getStreet());
		addTitle(mensa.getName());
	}
	private void addTitle(String menuTitle) {
		TextView nameText = (TextView) findViewById(R.id.mensa_name_view);
		nameText.setText(mensa.getName());
	}
	private void addDescription(String menuDesc) {
		TextView adressText = (TextView) findViewById(R.id.mensa_adress_view);
		adressText.setText(mensa.getStreet());
	}
	private int dimToPixels(int dim) {
		return (int) getResources().getDimension(dim);
	}
}
