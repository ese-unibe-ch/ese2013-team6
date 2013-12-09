/**
 * Displays the settings with values from the shared preferences.
 */

package com.ese2013.mub;

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ese2013.mub.model.Model;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.util.SharedPrefsHandler;
import com.ese2013.mub.util.TranslationTask;
import com.memetix.mst.language.Language;

public class SettingsFragment extends Fragment {
	private SharedPrefsHandler prefs;
	private ArrayList<String> notificationListItems;
	private SettingsListAdapter adapter;
	private Button editCriteriaButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.settings);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		prefs = new SharedPrefsHandler(getActivity().getApplicationContext());
		View view = inflater.inflate(R.layout.fragment_settings, container, false);

		Switch languageSwitch = (Switch) view.findViewById(R.id.language_switch);
		languageSwitch.setChecked(prefs.getDoTranslation());

		Switch notificationSwitch = (Switch) view.findViewById(R.id.notification_switch);
		notificationSwitch.setChecked(prefs.getDoNotification());
		Spinner notificationSpinner = (Spinner) view.findViewById(R.id.notification_spinner);
		notificationSpinner.setSelection(prefs.getDoNotificationsForAllMensas() ? 0 : 1);

		addReregisterButton(view);
		setUpOnChangeListener(languageSwitch);

		notificationListItems = new ArrayList<String>(prefs.getNotificationListItems());
		adapter = new SettingsListAdapter(getActivity().getApplicationContext(), notificationListItems);
		editCriteriaButton = (Button) view.findViewById(R.id.edit_notification_criteria);
		updateEditCriteriaButton();
		editCriteriaButton.setOnClickListener(new EditCriteriaButtonListener());
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		for (int i = 0; i < menu.size(); i++)
			menu.getItem(i).setVisible(false);
	}

	private void updateEditCriteriaButton() {
		editCriteriaButton.setText(notificationListItems.isEmpty() ? R.string.settings_add_criteria
				: R.string.settings_edit_criteria);
	}

	private void addReregisterButton(View view) {
		TextView registerTitle = (TextView) view.findViewById(R.id.register_title);
		final Button button = (Button) view.findViewById(R.id.register);
		View line = view.findViewById(R.id.register_line);
		registerTitle.setVisibility(View.VISIBLE);
		button.setVisibility(View.VISIBLE);
		line.setVisibility(View.VISIBLE);
		if (LoginService.isLoggedIn()) {
			registerTitle.setVisibility(View.GONE);
			button.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		} else {
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DrawerMenuActivity a = (DrawerMenuActivity) getActivity();
					a.showRegistrationDialog();
					button.setEnabled(false);
				}
			});
		}

	}

	/**
	 * Starts translation if the @param languageSwitch is checked and if it is
	 * not already available.
	 * 
	 */
	private void setUpOnChangeListener(Switch languageSwitch) {
		languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Model.getInstance().getMenuManager().setTranslationsEnabled(true);
					if (!Model.getInstance().getMenuManager().translationsAvailable()) {
						TranslationTask transTask = new TranslationTask(Model.getInstance().getMenuManager(),
								Language.ENGLISH, Model.getInstance());
						transTask.execute();
						Toast.makeText(getActivity(), R.string.translation_started, Toast.LENGTH_SHORT).show();
					}
				} else {
					Model.getInstance().getMenuManager().setTranslationsEnabled(false);
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		prefs.setDoTranslation(((Switch) getView().findViewById(R.id.language_switch)).isChecked());
		prefs.setDoNotification(((Switch) getView().findViewById(R.id.notification_switch)).isChecked());
		boolean notificationsForAllMensas = ((Spinner) getView().findViewById(R.id.notification_spinner))
				.getSelectedItemPosition() == 0;
		prefs.setDoNotificationsForAllMensas(notificationsForAllMensas);
		prefs.setNotificationListItems(new TreeSet<String>(notificationListItems));
	}

	private class EditCriteriaButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			View view = getLayoutInflater(null).inflate(R.layout.dialog_edit_notification_criteria, null);
			adapter = new SettingsListAdapter(getActivity().getApplicationContext(), notificationListItems);
			ListView notificationList = (ListView) view.findViewById(R.id.notification_list);
			notificationList.setAdapter(adapter);
			ImageButton addCriteriaButton = (ImageButton) view.findViewById(R.id.plus_button);
			final EditText notificationEditText = ((EditText) view.findViewById(R.id.edit_text_notification));
			addCriteriaButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (!notificationEditText.getText().toString().isEmpty()) {
						notificationListItems.add(0, notificationEditText.getText().toString());
						adapter.notifyDataSetChanged();
						notificationEditText.setText("");
					}
				}
			});

			new AlertDialog.Builder(getActivity()).setTitle(R.string.settings_edit_notification_criteria_title)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							updateEditCriteriaButton();
						}
					}).setView(view).show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.settings);
	}
}