package com.ese2013.mub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Model;
import com.ese2013.mub.social.Invitation;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.SocialManager;
import com.ese2013.mub.social.User;

/**
 * 
 * Allows a user to select time, date, friends and a text Message for invites.
 * It is responsible to check all fields are filled and call teh SocialManager
 * to send the complete invitation
 * 
 */
public class CreateInvitationFragment extends Fragment implements OnClickListener, OnDateSetListener, OnTimeSetListener {
	private static final String DATE_KEY = "date";
	public static final String MENSA_INDEX = "mensaIndex", DATE_FROM_VIEW = "DateFromView";

	private Date date = new Date();
	private List<User> recipients = new ArrayList<User>();
	private Button pickDateButton, pickTimeButton;
	private int mensaIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.invite);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_invitation, container, false);
		if (getArguments() != null) {
			mensaIndex = getArguments().getInt(MENSA_INDEX, 1);
			date = new Date(getArguments().getLong(DATE_FROM_VIEW, new Date().getTime()));
		}

		createPickDateButton(view);
		createPickTimeButton(view);
		createSelectRecipientsButton(view);
		createCreateButton(view);
		createCancelButton(view);
		createSpinner(view, mensaIndex);

		return view;
	}

	private void createCancelButton(View view) {
		Button cancelButton = (Button) view.findViewById(R.id.invitation_create_cancelButton);
		cancelButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});
	}

	private void createCreateButton(View view) {
		Button createButton = (Button) view.findViewById(R.id.invitation_create_createButton);
		createButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText messageText = (EditText) CreateInvitationFragment.this.getActivity().findViewById(
						R.id.invitation_create_editMessage);
				String message = messageText.getText().toString();

				Spinner spinner = (Spinner) CreateInvitationFragment.this.getActivity().findViewById(
						R.id.invitation_create_mensaSpinner);
				Mensa m = (Mensa) spinner.getSelectedItem();

				Invitation invite = new Invitation(LoginService.getLoggedInUser(), recipients, message, m.getId(), date);
				if (isComplete(invite)) {
					SocialManager.getInstance().sendInvitation(invite);
					Toast.makeText(getActivity(), "Sending invitation...", Toast.LENGTH_SHORT).show();
					getFragmentManager().popBackStack();
				}

			}
		});
	}

	private void createSelectRecipientsButton(View view) {
		Button selectRecipientsButton = (Button) view.findViewById(R.id.invitation_create_select_recipients);
		selectRecipientsButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View button) {
				DialogFragment newFragment = RecipientsPickerFragment.newInstance(CreateInvitationFragment.this,
						recipients);
				newFragment.show(getActivity().getSupportFragmentManager(), "recipientsPicker");
			}
		});
	}

	private void createPickTimeButton(View view) {
		pickTimeButton = (Button) view.findViewById(R.id.invitation_create_pick_time);
		pickTimeButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View button) {
				DialogFragment newFragment = TimePickerFragment.newInstance(CreateInvitationFragment.this, date);
				newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
			}
		});
		onTimeSet(null, 12, 0);
	}

	private void createPickDateButton(View view) {
		pickDateButton = (Button) view.findViewById(R.id.invitation_create_pick_date);
		pickDateButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View button) {
				DialogFragment newFragment = DatePickerFragment.newInstance(CreateInvitationFragment.this, date);
				newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
			}
		});
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		onDateSet(null, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	}

	private boolean isComplete(Invitation invitation) {
		String error = "";
		if (invitation.getFrom() == null)
			error += "You are not logged in" + "\n";
		if (invitation.getMessage().length() == 0)
			error = "Please enter a message" + "\n";
		if (invitation.getTo().isEmpty())
			error += "Please add at least one recipient" + "\n";

		if (error.length() != 0) {
			Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}

	private void createSpinner(View view, int startIndex) {
		Spinner spinner = (Spinner) view.findViewById(R.id.invitation_create_mensaSpinner);
		List<Mensa> list = new ArrayList<Mensa>();
		List<Mensa> mensas = Model.getInstance().getMensas();
		Mensa mensa = null;
		for (Mensa m : mensas) {
			list.add(m);
			if (startIndex == m.getId())
				mensa = m;
		}

		ArrayAdapter<Mensa> dataAdapter = new ArrayAdapter<Mensa>(this.getActivity(),
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		spinner.setSelection(mensas.indexOf(mensa));
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, monthOfYear);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		date = c.getTime();
		pickDateButton.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		date = c.getTime();
		pickTimeButton.setText(new SimpleDateFormat("h : mm", Locale.getDefault()).format(date));
	}

	@Override
	public void onClick(DialogInterface recipientsSelectionDialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			Button selectRecipientsButton = (Button) getView().findViewById(R.id.invitation_create_select_recipients);
			if (recipients.size() > 0) {
				String out = recipients.get(0).toString();
				for (int i = 1; i < recipients.size(); i++)
					out += ", " + recipients.get(i).toString();
				selectRecipientsButton.setText(out);
			} else {
				selectRecipientsButton.setText(R.string.invitation_create_select_recipients);
			}
			break;
		}
	}

	/**
	 * 
	 * Allows user to Select a Date for the Invites
	 * 
	 */
	public static class DatePickerFragment extends DialogFragment {
		private OnDateSetListener listener;

		public static DatePickerFragment newInstance(OnDateSetListener listener, Date initialDate) {
			DatePickerFragment dialogFragment = new DatePickerFragment();
			Bundle bundle = new Bundle();
			bundle.putLong(DATE_KEY, initialDate.getTime());
			dialogFragment.setListener(listener);
			dialogFragment.setArguments(bundle);
			return dialogFragment;
		}

		public void setListener(OnDateSetListener listener) {
			this.listener = listener;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			c.setTimeInMillis(getArguments().getLong(DATE_KEY));
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), listener, year, month, day);
			// minimum date to display in the date picker, can't be exactly now,
			// thus the " - 1000".
			datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
			return datePickerDialog;
		}
	}

	/**
	 * 
	 * Allows a User to select a Date for the Invite
	 * 
	 */
	public static class TimePickerFragment extends DialogFragment {
		private OnTimeSetListener listener;

		public static TimePickerFragment newInstance(OnTimeSetListener listener, Date initialDate) {
			TimePickerFragment dialogFragment = new TimePickerFragment();
			Bundle bundle = new Bundle();
			bundle.putLong(DATE_KEY, initialDate.getTime());
			dialogFragment.setListener(listener);
			dialogFragment.setArguments(bundle);
			return dialogFragment;
		}

		public void setListener(OnTimeSetListener listener) {
			this.listener = listener;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			c.setTimeInMillis(getArguments().getLong(DATE_KEY));
			int hours = c.get(Calendar.HOUR_OF_DAY);
			int minutes = c.get(Calendar.MINUTE);
			return new TimePickerDialog(getActivity(), listener, hours, minutes, true);
		}
	}

	/**
	 * 
	 * DialogeField with checkBoxes for adding one or more friends to the Invite
	 * 
	 */
	public static class RecipientsPickerFragment extends DialogFragment implements
			DialogInterface.OnMultiChoiceClickListener {
		private OnClickListener listener;
		private List<User> friends;
		private List<User> selectedFriends;

		public static RecipientsPickerFragment newInstance(OnClickListener listener, List<User> selectedFriends) {
			RecipientsPickerFragment dialogFragment = new RecipientsPickerFragment();
			dialogFragment.setSelectedFriends(selectedFriends);
			dialogFragment.setListener(listener);
			return dialogFragment;
		}

		private void setSelectedFriends(List<User> selectedFriends) {
			this.selectedFriends = selectedFriends;
		}

		public void setListener(OnClickListener listener) {
			this.listener = listener;
		}

		@Override
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			User selected = friends.get(which);
			if (isChecked) {
				if (!selectedFriends.contains(selected))
					selectedFriends.add(selected);
			} else {
				if (selectedFriends.contains(selected))
					selectedFriends.remove(selected);
			}
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			friends = LoginService.getLoggedInUser().getFriends();
			CharSequence[] options = new CharSequence[friends.size()];
			boolean[] selections = new boolean[options.length];
			for (int i = 0; i < options.length; i++) {
				options[i] = friends.get(i).getNick();
				if (selectedFriends.contains(friends.get(i)))
					selections[i] = true;
			}

			return new AlertDialog.Builder(getActivity()).setTitle("Recipients")
					.setMultiChoiceItems(options, selections, this).setPositiveButton("OK", listener).create();
		}
	}
}