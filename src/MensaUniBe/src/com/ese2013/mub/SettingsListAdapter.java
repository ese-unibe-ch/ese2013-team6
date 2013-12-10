package com.ese2013.mub;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * Adapter for the NotifiactionList in the SettingsFragment, responsible for deleting entries per button
 */
public class SettingsListAdapter extends BaseAdapter {

	private final Context context;
	private final ArrayList<String> itemsArrayList;

	public SettingsListAdapter(Context context, ArrayList<String> itemsArrayList) {
		this.context = context;
		this.itemsArrayList = itemsArrayList;
	}

	@Override
	public int getCount() {
		return itemsArrayList.size();
	}

	@Override
	public String getItem(int position) {
		return itemsArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.settings_list_item, parent, false);

		TextView text = (TextView) rowView.findViewById(android.R.id.text1);
		text.setText(itemsArrayList.get(position));
		ImageButton delete = (ImageButton) rowView.findViewById(R.id.minus_button);
		delete.setOnClickListener(new DeleteOnClickListener(this, itemsArrayList.get(position)));
		return rowView;
	}

	private boolean delete(String element) {
		return itemsArrayList.remove(element);
	}

	private class DeleteOnClickListener implements OnClickListener {
		private String element;
		private SettingsListAdapter adapter;

		public DeleteOnClickListener(SettingsListAdapter adapter, String element) {
			this.element = element;
			this.adapter = adapter;
		}

		@Override
		public void onClick(View v) {
			adapter.delete(element);
			adapter.notifyDataSetChanged();
		}
	}
}