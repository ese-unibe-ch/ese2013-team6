package ch.xonix.mensa.unibe;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ch.ese2013.mub.model.Mensa;
import ch.ese2013.mub.model.Model;

public class Main extends Activity {

	private static final CharSequence MESSAGE_GET_DATA = "lade...";
	protected static final String MENSA_ID_KEY = "ch.xonix.mensa.unibe::mensa_ID";
	protected static final String MENSA_NAME_KEY = "ch.xonix.mensa.unibe::mensa_name";
	private static final String TAG = "Main";
	private ListView mensaListView;
	private ArrayAdapter<Mensa> mensaListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create a array adapter
		mensaListAdapter = new ArrayAdapter<Mensa>(this,
				android.R.layout.simple_list_item_1);
		mensaListView = (ListView) this.findViewById(R.id.mensa_list);
		mensaListView.setAdapter(mensaListAdapter);
		// make aysnc request to get a list of all canteens
		Toast.makeText(Main.this, MESSAGE_GET_DATA, Toast.LENGTH_SHORT).show();
//		new MensasRequest(mensaListAdapter).execute();
		Model model = new Model(this);
		
		ArrayList<Mensa> mensas = model.getMensas();
		for(Mensa mensa : mensas)
			this.mensaListAdapter.add(mensa);

		// ad OnItemClickListener
		mensaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long arg3) {
				Mensa mensa = (Mensa) parent.getItemAtPosition(pos);
				Intent intent = new Intent(Main.this, MenuplanActivity.class);
				intent.putExtra(MENSA_ID_KEY, mensa.getId());
				intent.putExtra(MENSA_NAME_KEY, mensa.getName());
				Main.this.startActivity(intent);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
