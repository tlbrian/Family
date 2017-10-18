package com.litian.family;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.litian.family.firestore.MyFirestore;
import com.litian.family.model.User;

/**
 * Created by TianLi on 2017/10/14.
 */
public class AddNewDialogFragment extends DialogFragment {
	private static final String TAG = "Login";
	View view;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.dialog_add_chat, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						EditText username_editText = view.findViewById(R.id.new_username);

						String username = username_editText.getText().toString();

						if (!isEmailValid(username)) {
							username_editText.setError(getString(R.string.error_invalid_email));
							return;
						}

						MyFirestore.getInstance().searchUserByEmail(username, new MyFirestore.SearchUserCallback() {
							@Override
							public void onSearchUserResult(User user) {
								if (user != null) {
									dismiss();

									//TODO: send a friend request
									MyFirestore.getInstance().sendFriendRequest(CurrentUser.get(), user, new MyFirestore.SendFriendReqCallback() {
										@Override
										public void onSendFriendReqResult(User user) {
											if (user != null) {
												Toast.makeText(getActivity(), "friend request sent", Toast.LENGTH_SHORT).show();
											}
											else {
												Log.e(TAG, "Couldn't add a friend request in database");
											}
										}
									});
								}
								else {
									Toast.makeText(getActivity(), "No such user", Toast.LENGTH_SHORT).show();
								}
							}
						});

					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						AddNewDialogFragment.this.getDialog().cancel();
					}
		});
		return builder.create();
	}

	private boolean isEmailValid(String email) {
		return email.contains("@");
	}
}
