package com.litian.family;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.litian.family.firestore.MyFirestore;
import com.litian.family.model.User;

/**
 * Created by TianLi on 2017/10/14.
 */

public class CurrentUser {

	private static final String TAG = "Auth";
	private static User currentUser;

	private static Bundle data;

	private CurrentUser(){}

	public static User get() {
		return currentUser;
	}

	public static void set(User user) {
		currentUser = user;
	}

	public static void getCurrentUserInfoFromDB(String email) {
		MyFirestore.getInstance().searchUserByEmail(email, new MyFirestore.SearchUserCallback() {
			@Override
			public void onSearchUserResult(User user) {
				if (user != null) {
					Log.d(TAG, "find user in database: " + user.getEmail());
					CurrentUser.set(user);
				}
				else {
					Log.e(TAG, "Couldn't find user in database" + user.getEmail());
				}
			}
		});
	}

	public static Bundle getData() {
		return data;
	}

	public static void setData(Bundle b) {
		Log.d(TAG, "bundle payload:" + b);
		data = b;
	}
}
