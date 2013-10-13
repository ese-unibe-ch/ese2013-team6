package ch.xonix.mensa.unibe;

import ch.ese2013.mub.request.MenuplanRequest;
import ch.ese2013.mub.request.MenuplanWeeklyRequest;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuplanActivity extends Activity {

	private ListView menuListView;
	private MenuListAdapter menuListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuplan);
		String mensaId = getIntent().getStringExtra(Main.MENSA_ID_KEY);
		String mensaName = getIntent().getStringExtra(Main.MENSA_NAME_KEY);
		TextView textView = (TextView) this.findViewById(R.id.menuplan_title);
		textView.setText("Tagesmenüplan\n"+mensaName);
		
		menuListView = (ListView) this.findViewById(R.id.menu_list);
		menuListAdapter = new MenuListAdapter(this);
		menuListView.setAdapter(menuListAdapter);
		new MenuplanRequest(this,menuListAdapter,mensaId).execute();
		Toast.makeText(this, "lade...",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menuplan, menu);
		return true;
	}

}
