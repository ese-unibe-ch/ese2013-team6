package ch.xonix.mensa.unibe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.ese2013.mub.model.Menu;

public class MenuListAdapter extends BaseAdapter {

	private List<Menu> menus = new ArrayList<Menu>();
	private LayoutInflater inflater;

	public MenuListAdapter(Activity activity) {
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return this.menus.size();
	}

	@Override
	public Object getItem(int pos) {
		return this.menus.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.list_row, null);

		TextView title = (TextView) vi.findViewById(R.id.menu_title); // title
		TextView content = (TextView) vi.findViewById(R.id.menu_content); // content
		Menu menu = this.menus.get(position);
		
		title.setText(menu.getTitle());
		content.setText(menu.getContent());	
		return vi;
	}

	public void clear() {
		this.menus.clear();
		
	}
	
	public void addAll(List<Menu> menus){
		this.menus.addAll(menus);
	}

}
