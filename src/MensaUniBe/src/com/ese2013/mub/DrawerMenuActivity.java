package com.ese2013.mub;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.ese2013.mub.model.Model;

/**
 * This class is the main activity for the mub app. Everything else to be
 * displayed with a drawer menu, needs to be created here.
 */
public class DrawerMenuActivity extends FragmentActivity {

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private Spinner spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Model(this);

		setContentView(R.layout.activity_drawer_menu);

		String[] menuItemNames = { "Home", "Mensa List", "Map", "Invites",
				"Settings" };
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, menuItemNames));

		// enable ActionBar app icon to behave as action to toggle nav drawer

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		/* spinner */

		// Set up the action bar to show a dropdown list.
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.spinner_list,
						android.R.layout.simple_spinner_dropdown_item);

		actionBar.setDisplayShowTitleEnabled(false);
		Spinner navigationSpinner = new Spinner(this);
		navigationSpinner.setAdapter(spinnerAdapter);
		actionBar.setCustomView(navigationSpinner);
		OnItemSelectedListener spinnerNavigationListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				HomeFragment frag = new HomeFragment();
				switch (position) {
				case 0:
					frag.setFavorites(true);
					frag.setShowAllByDay(false);
					setDisplayedFragment(frag);
					break;

				case 1:
					frag.setFavorites(false);
					frag.setShowAllByDay(false);
					setDisplayedFragment(frag);
					break;

				case 2:
					frag.setFavorites(true);
					frag.setShowAllByDay(true);
					setDisplayedFragment(frag);
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
		navigationSpinner.setOnItemSelectedListener(spinnerNavigationListener);
		actionBar.setDisplayShowCustomEnabled(false);
		spinner = navigationSpinner;
		/* end of spinner */

		selectItem(0);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();

		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	}

	@Override
	public void onPause() {
		super.onPause();
		Model.getInstance().saveFavorites();
	}

	/**
	 * ClickListener for the Drawer List. Handles selecting list items in the
	 * Drawer menu.
	 */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment frag;
		switch (position) {
		case 0:
			frag = new HomeFragment();
			setDisplayedFragment(frag);
			break;
		case 1:
			frag = new MensaListFragment();
			setDisplayedFragment(frag);
			break;
		case 2:
			frag = new MapFragment();
			setDisplayedFragment(frag);
			break;
		case 3:
			break;
		case 4:
			break;
		}

		// update selected item, then close drawer
		// TODO should also update the action bar if needed, maybe specific for
		// each fragment?
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	/**
	 * Sets the container "drawer_layout_frag_container" of the
	 * DrawerMenuActivity to the given Fragment.
	 * 
	 * @param frag
	 *            the Fragment to be displayed. Shouldn't be null.
	 */
	private void setDisplayedFragment(Fragment frag) {
		assert (frag != null);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.drawer_layout_frag_container, frag).commit();
	}

	/**
	 * Called whenever we call invalidateOptionsMenu() Hides all action par menu
	 * options and redisplays them as needed
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		//getActionBar().setDisplayShowCustomEnabled(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Called after creation of the activity.
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	/**
	 * Makes the home/up button opens/closes the drawer
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Initializes the action bar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public void launchByMensaAtGivenPage(int position) {
		HomeFragment frag = new HomeFragment();
		Bundle args = new Bundle();
		args.putInt("POSITION", position);
		frag.setArguments(args);
		frag.setFavorites(false);
		frag.setShowAllByDay(true);
		spinner.setSelection(1);
		setDisplayedFragment(frag);

	}
}