package com.litian.family;

import android.os.Bundle;
import android.util.Log;

import com.litian.family.model.User;

/**
 * Created by TianLi on 2017/10/14.
 */

public class UserProfile {

	private static final String TAG = "UserProfile";

	private static UserProfile instance;

	private static User currentUser;
	private Bundle data;
	private ChatListFragment.MyAdapter chatListAdapter;
	private NotificationListFragment.MyAdapter notificationListAdapter;

	private UserProfile(){}

	public static void init() {
		instance = new UserProfile();
	}

	public static UserProfile getInstance() {
		if (instance == null) {
			instance = new UserProfile();
		}
		return instance;
	}

	public static User get() {
		return currentUser;
	}

	public static void set(User user) {
		currentUser = user;
	}

	public Bundle getData() {
		return data;
	}

	public void setData(Bundle b) {
		Log.d(TAG, "bundle payload:" + b);
		data = b;
	}
}
