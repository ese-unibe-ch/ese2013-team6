package com.ese2013.mub;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;

public class MensaListAdapter extends BaseAdapter{
	private Context context;
	private List<Mensa> menus = new ArrayList<Mensa>();
	private LayoutInflater inflater;

	public MensaListAdapter(Context context, int resource) {
		super();
		this.context = context;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) 
        	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        view = inflater.inflate(R.layout.mensa_row, null);
        TextView titleView = (TextView) view.findViewById(R.id.mensa_name_view);
        TextView adressView = (TextView) view.findViewById(R.id.mensa_adress_view);
        Mensa mensa = menus.get(position);
        titleView.setText(mensa.getName());
        adressView.setText(mensa.getName() + "\n" + mensa.getStreet() + "\n" + mensa.getZip());
        
		return view;
	}
	@Override
	public int getCount() {
		return menus.size();
	}
	@Override
	public Mensa getItem(int position) {
		return menus.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	public void fill(){
		menus = Model.getInstance().getMensas();
	}
}