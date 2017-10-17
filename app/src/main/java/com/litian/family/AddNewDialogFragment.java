package com.litian.family;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
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
							CharSequence text = "Email is not valid";
							int duration = Toast.LENGTH_SHORT;
							Toast.makeText(getActivity(), text, duration).show();
							return;
						}

						MyFirestore.getInstance().searchUserByEmail(username, new MyFirestore.SearchUserCallback() {
							@Override
							public void onSearchUserResult(User user) {
								if (user != null) {
									dismiss();
									Toast.makeText(getActivity(), "Added user successfully", Toast.LENGTH_SHORT).show();

									//TODO: send a friend request


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
