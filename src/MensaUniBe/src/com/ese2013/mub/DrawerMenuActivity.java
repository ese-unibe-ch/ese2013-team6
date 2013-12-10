package com.ese2013.mub;

import java.util.Stack;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.ese2013.mub.map.MapFragment;
import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.service.NotificationService;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.SocialManager;
import com.ese2013.mub.social.util.LoginTask;
import com.ese2013.mub.social.util.LoginTaskCallback;
import com.ese2013.mub.util.SharedPrefsHandler;
import com.ese2013.mub.util.database.MensaDataSource;
import com.parse.PushService;

/**
 * This class is the main activity for the mub app. Everything else to be
 * displayed with a drawer menu, needs to be created here.
 */
public class DrawerMenuActivity extends FragmentActivity implements LoginTaskCallback {

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private Spinner spinner;
	private int selectedPosition = NOTHING_INDEX;
	private static final int HOME_INDEX = 0, MAP_INDEX = 2, INVITATIONS_INDEX = 3, NOTIFICATION_INDEX = 4,
			NOTHING_INDEX = -1;
	private static final String POSITION = "com.ese2013.mub.position";
	private Model model;
	private RegistrationDialog registrationDialog;
	private Stack<Integer> menuSelectionBackStack = new Stack<Integer>();

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		registrationDialog.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleLogin();

		MensaDataSource dataSource = MensaDataSource.getInstance();
		dataSource.init(getApplicationContext());
		model = Model.getInstance();
		model.init(dataSource, new SharedPrefsHandler(getApplicationContext()));

		createActionBar();
		createDrawerMenu(savedInstanceState);

		handleArguments();
	}

	private void handleLogin() {
		SharedPrefsHandler prefs = new SharedPrefsHandler(this);
		if (prefs.isFirstTime())
			showRegistrationDialog();
		else if (prefs.isUserRegistred())
			new LoginTask(this).execute(prefs.getUserEmail());

	}

	public void showRegistrationDialog() {
		registrationDialog = new RegistrationDialog(this);
	}

	private void handleArguments() {
		if (getIntent().getBooleanExtra(PushNotificationCallbackActivity.SHOW_INVITES, false))
			showReceivedInvitations();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleArguments();
	}

	@Override
	public void onTaskFinished(LoginTask task) {
		if (!task.hasSucceeded())
			Toast.makeText(this, R.string.user_login_failed, Toast.LENGTH_LONG).show();
		else {
			subscribeToPush();
			SocialManager.getInstance().load();
		}
	}

	private void subscribeToPush() {
		if (LoginService.isLoggedIn()) {
			String channelId = "user_" + LoginService.getLoggedInUser().getId();
			PushService.subscribe(getApplicationContext(), channelId, PushNotificationCallbackActivity.class);
		}
	}

	private void createActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		createActionBarSpinner();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(spinner);
		actionBar.setDisplayShowCustomEnabled(false);
	}

	private void createDrawerMenu(Bundle savedInstanceState) {
		setContentView(R.layout.activity_drawer_menu);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		String[] menuItemNames = getResources().getStringArray(R.array.drawer_menu_items);
		drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuItemNames));

		if (savedInstanceState != null)
			selectItem(savedInstanceState.getInt(POSITION, HOME_INDEX), true);
		else if (getIntent().getBooleanExtra(NotificationService.START_FROM_N, false))
			selectItem(NOTIFICATION_INDEX, true);
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

	private void createActionBarSpinner() {
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
					frag.setShowDailyPlans(true);
					frag.setShowOnlyFavorites(true);
					setDisplayedFragment(frag, false);
					break;

				case 1:
					frag.setShowDailyPlans(false);
					frag.setShowOnlyFavorites(true);
					setDisplayedFragment(frag, false);
					break;

				case 2:
					frag.setShowDailyPlans(true);
					frag.setShowOnlyFavorites(false);
					setDisplayedFragment(frag, false);
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
		if (instantiateFragment && selectedPosition != position) {
			Fragment frag;
			switch (position) {
			case 0:
				frag = new HomeFragment();
				setDisplayedFragment(frag, false);
				break;
			case 1:
				frag = new MensaListFragment();
				setDisplayedFragment(frag, true);
				break;
			case 2:
				frag = new MapFragment();
				setDisplayedFragment(frag, true);
				break;
			case 3:
				frag = new InvitationBaseFragment();
				setDisplayedFragment(frag, true);
				break;
			case 4:
				frag = new NotificationFragment();
				setDisplayedFragment(frag, true);
				break;
			}
		}

		drawerList.setItemChecked(selectedPosition, false);
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
	private void setDisplayedFragment(Fragment frag, boolean useBackStack) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.drawer_layout_frag_container, frag);
		if (useBackStack) {
			transaction.addToBackStack(null);
		}

		getActionBar().setDisplayShowCustomEnabled(isShowingHomeFragment());
		menuSelectionBackStack.push(selectedPosition);
		transaction.commit();

	}

	/**
	 * Called whenever we call invalidateOptionsMenu() Hides all action bar menu
	 * options and redisplays them as needed.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		for (int i = 0; i < menu.size(); i++)
			menu.getItem(i).setVisible(!drawerOpen);

		getActionBar().setDisplayShowCustomEnabled(isShowingHomeFragment() && !drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private boolean isShowingHomeFragment() {
		return selectedPosition == HOME_INDEX;
	}

	@Override
	public void onBackPressed() {
		if (isShowingHomeFragment())
			finish();

		if (!menuSelectionBackStack.isEmpty()) {
			int select = menuSelectionBackStack.pop();
			selectedPosition = select;
			if (select != NOTHING_INDEX)
				drawerList.setItemChecked(select, true);
		}
		super.onBackPressed();
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
		if (!drawerToggle.onOptionsItemSelected(item) && item.getItemId() == R.id.action_settings) {
			displaySettings();
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

	/**
	 * goes to weeklyplan view and sets the pager to the mensa on this position
	 * The position in the list of the mensa in the model must be consistent at
	 * runtime.
	 * 
	 * @param position
	 *            position of the fragment to go to
	 */
	public void launchByMensaAtGivenPage(int position) {
		HomeFragment frag = new HomeFragment();
		Bundle args = new Bundle();
		args.putInt(HomeFragment.POSITION, position);
		frag.setArguments(args);
		frag.setShowDailyPlans(false);
		frag.setShowOnlyFavorites(true);
		spinner.setSelection(1);

		selectItem(HOME_INDEX, false);
		setDisplayedFragment(frag, true);
	}

	/**
	 * Restart the HomeFragment on the FavoriteView.
	 * 
	 */
	public void refreshHomeActivity() {
		HomeFragment frag = new HomeFragment();
		frag.setShowDailyPlans(true);
		frag.setShowOnlyFavorites(false);

		selectItem(HOME_INDEX, false);
		setDisplayedFragment(frag, true);
	}

	/**
	 * Start the map and selects given mensa
	 * 
	 * @param mensa
	 *            mensa to be selected
	 */
	public void displayMapAtMensa(Mensa mensa) {
		MapFragment mapFragment = new MapFragment();
		Bundle args = new Bundle();
		args.putInt(MapFragment.MENSA_ID_LOCATION, mensa.getId());
		mapFragment.setArguments(args);

		selectItem(MAP_INDEX, false);
		setDisplayedFragment(mapFragment, true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (selectedPosition != -1)
			outState.putInt(POSITION, selectedPosition);
		super.onSaveInstanceState(outState);
	}

	/**
	 * creates an Invitation and gives them as arguments to the CreationFragment
	 * 
	 * @param mensa
	 *            argument for the CreateInvitationFragment
	 * @param day
	 *            argument for the CreateInvitationFragment
	 */
	public void createInvitation(Mensa mensa, Day day) {
		CreateInvitationFragment frag = new CreateInvitationFragment();
		Bundle args = new Bundle();
		args.putInt(CreateInvitationFragment.MENSA_INDEX, mensa.getId());
		args.putLong(CreateInvitationFragment.DATE_FROM_VIEW, day.getDate().getTime());
		frag.setArguments(args);

		selectItem(NOTHING_INDEX, false);
		setDisplayedFragment(frag, true);
	}

	/**
	 * creates an CreationInvitationFragment without giving arguments to it
	 */
	public void createInvitation() {
		CreateInvitationFragment frag = new CreateInvitationFragment();

		selectItem(NOTHING_INDEX, false);
		setDisplayedFragment(frag, true);
	}

	private void showReceivedInvitations() {
		selectItem(DrawerMenuActivity.INVITATIONS_INDEX, true);
	}

	private void displaySettings() {
		Fragment frag = new SettingsFragment();

		selectItem(NOTHING_INDEX, false);
		setDisplayedFragment(frag, true);
	}
}
