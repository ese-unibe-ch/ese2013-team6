package com.ese2013.mub;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.service.NotificationService;

/**
 * This class is the main activity for the mub app. Everything else to be
 * displayed with a drawer menu, needs to be created here.
 */
public class DrawerMenuActivity extends FragmentActivity {

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private Spinner spinner;
	private int selectedPosition = -1;
	private static final int HOME_INDEX = 0, MAP_INDEX = 2, NOTIFICATION_INDEX = 3;
	private static final String POSITION = "com.ese2013.mub.position";
	private Model model;
	private NotificationService service;
	private boolean isConnected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new Model(getApplicationContext());

		setContentView(R.layout.activity_drawer_menu);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the drawer menu list
		String[] menuItemNames = { "Home", "Mensa List", "Map", "Notifications" };
		drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuItemNames));

		// enable ActionBar app icon to behave as action to toggle nav drawer
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// Set up the action bar to show a dropdown list.
		createSpinner();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(spinner);
		actionBar.setDisplayShowCustomEnabled(false);

		// select home in drawer menu
		if (savedInstanceState != null)
			selectItem(savedInstanceState.getInt(POSITION, HOME_INDEX), true);
		else if(getIntent().getBooleanExtra(NotificationService.START_FROM_N, false))
			selectItem(NOTIFICATION_INDEX,true);
		else
			selectItem(HOME_INDEX, true);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);
		drawerToggle.syncState();
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	private void createSpinner() {
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_list,
				android.R.layout.simple_spinner_dropdown_item);
		spinner = new Spinner(this);
		spinner.setAdapter(spinnerAdapter);
		OnItemSelectedListener spinnerNavigationListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
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
		spinner.setOnItemSelectedListener(spinnerNavigationListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		model.saveFavorites();
	}

	/**
	 * ClickListener for the Drawer List. Handles selecting list items in the
	 * Drawer menu.
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position, true);
		}
	}

	private void selectItem(int position, boolean instantiateFragment) {
		// update the main content by replacing fragments
		if (instantiateFragment && selectedPosition != position) {
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
				frag = new NotificationFragment();
				setDisplayedFragment(frag);
				break;
			}
		}
		selectedPosition = position;
		drawerList.setItemChecked(selectedPosition, true);
		drawerLayout.closeDrawer(drawerList);
	}

	/**
	 * Sets the container "drawer_layout_frag_container" of the
	 * DrawerMenuActivity to the given Fragment.
	 * 
	 * @param frag
	 *            the Fragment to be displayed. Shouldn't be null.
	 */
	private void setDisplayedFragment(Fragment frag) {
		assert(frag != null);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.drawer_layout_frag_container, frag);
		transaction.commit();
	}

	/**
	 * Called whenever we call invalidateOptionsMenu() Hides all action par menu
	 * options and redisplays them as needed
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		if (isShowingHomeFragment())
			getActionBar().setDisplayShowCustomEnabled(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private boolean isShowingHomeFragment() {
		return selectedPosition == HOME_INDEX;
	}

	/**
	 * Called after creation of the activity.
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	/**
	 * Makes the home/up button opens/closes the drawer
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
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
		args.putInt(HomeFragment.POSITION, position);
		frag.setArguments(args);
		frag.setFavorites(false);
		frag.setShowAllByDay(true);
		spinner.setSelection(1);
		setDisplayedFragment(frag);
		selectItem(HOME_INDEX, false);
	}

	public void refreshHomeActivity() {
		HomeFragment frag = new HomeFragment();
		frag.setFavorites(true);
		frag.setShowAllByDay(false);
		setDisplayedFragment(frag);
		selectItem(HOME_INDEX, false);
	}

	public void displayMapAtMensa(Mensa mensa) {
		MapFragment mapFragment = new MapFragment();
		Bundle args = new Bundle();
		args.putInt(MapFragment.MENSA_ID_LOCATION, mensa.getId());
		mapFragment.setArguments(args);
		setDisplayedFragment(mapFragment);
		selectItem(MAP_INDEX, false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (selectedPosition != -1)
			outState.putInt(POSITION, selectedPosition);
		super.onSaveInstanceState(outState);
	}
}
	
