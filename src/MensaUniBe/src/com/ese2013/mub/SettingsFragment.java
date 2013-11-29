/**
 * Displays the settings with values from the shared preferences.
 */

package com.ese2013.mub;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingsFragment extends Fragment {
	
	private Context context;
	private Preferences prefs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		prefs = new Preferences();
		context = getActivity().getApplicationContext();

		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
		Switch languageSwitch = (Switch) view.findViewById(R.id.language_switch);
		Spinner languageSpinner = (Spinner) view.findViewById(R.id.language_spinner);
		Switch notificationSwitch = (Switch) view.findViewById(R.id.notification_switch);
		Spinner notificationSpinner = (Spinner) view.findViewById(R.id.notification_spinner);
		EditText notificationEditText = ((EditText) view.findViewById(R.id.edit_text_notification));
		
		languageSwitch.setChecked(prefs.getDoTranslation(context));
		languageSpinner.setSelection(prefs.getLanguage(context));
		notificationSwitch.setChecked(prefs.getDoNotification(context));
		notificationSpinner.setSelection(prefs.getNotificationMensas(context));
		notificationEditText.setText(prefs.getNotificationFood(context));
				
		return view;
	}
	
	@Override
	public void onPause() {
		super.onPause();

		prefs.setDoTranslation(context, ( (Switch)this.getView().findViewById(R.id.language_switch) ).isChecked());
		prefs.setLanguage(context, ( (Spinner)this.getView().findViewById(R.id.language_spinner) ).getSelectedItemPosition());
		prefs.setDoNotification(context, ( (Switch)this.getView().findViewById(R.id.notification_switch) ).isChecked());
		prefs.setNotificationMensas(context, ( (Spinner)this.getView().findViewById(R.id.notification_spinner) ).getSelectedItemPosition());
		prefs.setNotificationFood(context, ( (EditText)this.getView().findViewById(R.id.edit_text_notification) ).getText().toString());
	}
}