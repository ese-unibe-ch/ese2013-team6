/**
 * Displays the settings with values from the shared preferences.
 */

package com.ese2013.mub;

import java.util.ArrayList;
import java.util.TreeSet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.ese2013.mub.model.Model;
import com.ese2013.mub.util.SharedPrefsHandler;
import com.ese2013.mub.util.TranslationTask;
import com.memetix.mst.language.Language;

public class SettingsFragment extends Fragment {

	private SharedPrefsHandler prefs;
	private ArrayList<String> notificationListItems;
	private SettingsListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		prefs = new SharedPrefsHandler(getActivity().getApplicationContext());

		View view = inflater.inflate(R.layout.fragment_settings, container,
				false);

		Switch languageSwitch = (Switch) view
				.findViewById(R.id.language_switch);
		// Spinner languageSpinner = (Spinner)
		// view.findViewById(R.id.language_spinner);
		Switch notificationSwitch = (Switch) view
				.findViewById(R.id.notification_switch);
		Spinner notificationSpinner = (Spinner) view
				.findViewById(R.id.notification_spinner);
		final EditText notificationEditText = ((EditText) view
				.findViewById(R.id.edit_text_notification));
		ListView notificationList = (ListView) view.findViewById(R.id.notification_list);
		
		addReregisterButton(view);

		languageSwitch.setChecked(prefs.getDoTranslation());
		setUpOnChangeListener(languageSwitch);
		
		// languageSpinner.setSelection(prefs.getLanguage());
		notificationSwitch.setChecked(prefs.getDoNotification());
		notificationSpinner.setSelection(prefs.getNotificationMensas());
		notificationEditText.setText(prefs.getNotificationFood());
		
		notificationListItems = new ArrayList<String>(prefs.getNotificationListItems());
		adapter = new SettingsListAdapter(getActivity().getApplicationContext(), notificationListItems);
		notificationList.setAdapter(adapter);
		
		Button plusButton = (Button) view.findViewById(R.id.plus_button);
		plusButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	if ( !notificationEditText.getText().toString().equals("") ){
		    		notificationListItems.add(notificationEditText.getText().toString());
			        adapter.notifyDataSetChanged();
			        notificationEditText.setText("");
		    	}
		    }
		});
				
		return view;
	}

	private void addReregisterButton(View view) {
		Button button = (Button) view.findViewById(R.id.register);
		
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DrawerMenuActivity a = (DrawerMenuActivity) getActivity();
				a.showRegistrationDialog();
//				new RegistrationDialog(getActivity());
			}
		});
	}

	private void setUpOnChangeListener(Switch languageSwitch) {
		languageSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Model.getInstance().getMenuManager()
									.setTranslationsEnabled(true);
							TranslationTask transTask = new TranslationTask(
									Model.getInstance().getMenuManager(),
									Language.ENGLISH, Model.getInstance());
							transTask.execute();
							Toast.makeText(getActivity(),
									"Translation started", Toast.LENGTH_SHORT)
									.show();
						} else {
							Model.getInstance().getMenuManager()
									.setTranslationsEnabled(false);
						}
					}
				});
	}

	@Override
	public void onPause() {
		super.onPause();

		prefs.setDoTranslation(((Switch) this.getView().findViewById(
				R.id.language_switch)).isChecked());
		// prefs.setLanguage(((Spinner)
		// this.getView().findViewById(R.id.language_spinner)).getSelectedItemPosition());
		prefs.setDoNotification(((Switch) this.getView().findViewById(
				R.id.notification_switch)).isChecked());
		prefs.setNotificationMensas(((Spinner) this.getView().findViewById(
				R.id.notification_spinner)).getSelectedItemPosition());
		//prefs.setNotificationFood(((EditText) this.getView().findViewById(
			//	R.id.edit_text_notification)).getText().toString());
		prefs.setNotificationListItems(new TreeSet<String>(notificationListItems));
	}

}