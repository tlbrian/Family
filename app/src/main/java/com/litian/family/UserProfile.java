package com.litian.family;

import android.os.Bundle;
import android.util.Log;

import com.litian.family.model.Notification;
import com.litian.family.model.User;

/**
 * Created by TianLi on 2017/10/14.
 */

public class UserProfile {

	private static final String TAG = "UserProfile";

	private static UserProfile instance;

	private User currentUser;
	private Bundle data;
	private boolean isFriendListDirty;

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

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User user) {
		this.currentUser = user;
	}

	public Bundle getData() {
		return data;
	}

	public void setData(Bundle b) {
		Log.d(TAG, "bundle payload:" + b);
		data = b;
	}

	public void addFriend(String uid) {
		isFriendListDirty = true;
		currentUser.addFriend(uid);
	}

	public boolean isFriendListDirty() {
		return isFriendListDirty;
	}

	public void setFriendListDirty(boolean friendListDirty) {
		isFriendListDirty = friendListDirty;
	}
}
