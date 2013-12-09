package com.ese2013.mub;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.ese2013.mub.social.CurrentUser;
import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.util.SharedPrefsHandler;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
/**
 * Start Dialog for Registering for the InvitationFeature with a Google Account
 *
 */
public class RegistrationDialog {
	private static final int PICK_ACCOUNT_REQUEST = 1;
	private Activity parentActivity;
	private SharedPrefsHandler prefs;

	public RegistrationDialog(Activity parentActivity) {
		this.parentActivity = parentActivity;
		this.prefs = new SharedPrefsHandler(parentActivity);
		showGoogleAccountPicker();
		prefs.setIsFirstTime(false);
	}

	private void showGoogleAccountPicker() {
		Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
				new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, true, null, null, null, null);
		parentActivity.startActivityForResult(googlePicker, PICK_ACCOUNT_REQUEST);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RegistrationDialog.PICK_ACCOUNT_REQUEST) {
			onAccountPickerDone(resultCode, data);
		}
	}

	private void onAccountPickerDone(final int resultCode, final Intent data) {
		switch (resultCode) {
		case Activity.RESULT_OK:
			final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			if (LoginService.loginSyncWithTimeout(accountName, 2)) {
				Toast.makeText(parentActivity, R.string.user_logged_in, Toast.LENGTH_SHORT).show();
				prefs.setUserEmail(accountName);
			} else {
				showNicknameDialog(accountName);
			}
			break;
		case Activity.RESULT_CANCELED:
			showRegistrationMessage();
			break;
		}

	}

	private void showNicknameDialog(final String accountName) {
		final EditText input = new EditText(parentActivity);
		new AlertDialog.Builder(parentActivity).setTitle(R.string.user_registration_enter_nickname)
				.setMessage(R.string.user_name).setView(input)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						prefs.setUserEmail(accountName);
						String nickname = input.getText().toString();
						if (!LoginService.registerAndLoginWithTimout(new CurrentUser(accountName, nickname), 2)) {
							Toast.makeText(parentActivity, R.string.user_registration_failed, Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						showRegistrationMessage();
					}
				}).show();
	}

	private void showRegistrationMessage() {
		new AlertDialog.Builder(parentActivity).setMessage(R.string.user_registration_declined_message)
				.setTitle(R.string.user_yes_registration).setCancelable(true)
				.setNegativeButton(R.string.user_no_registration, null)
				.setPositiveButton(R.string.user_register, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						showGoogleAccountPicker();
					}
				}).show();
	}
}
