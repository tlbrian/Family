package com.litian.family;

import android.os.Bundle;
import android.util.Log;

import com.litian.family.model.Friend;
import com.litian.family.model.Notification;
import com.litian.family.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by TianLi on 2017/10/14.
 */

public class UserProfile {

	private static final String TAG = "UserProfile";

	private static UserProfile instance;

	private User currentUser;
	private Bundle data;
	private List<Friend> friends;
	private List<Notification> notifications;

	private UserProfile(){
		friends = new ArrayList<>();
		notifications = new ArrayList<>();
	}

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

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}

	public void addFriend(Friend friend) {
		Log.d("litian", "litian is called " + friend.getFriendOf());
		friends.add(friend);
		Collections.sort(friends, new Comparator<Friend>() {
			@Override
			public int compare(Friend f1, Friend f2) {
				return f1.getName().compareTo(f2.getName());
			}
		});
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public void addNotification(Notification notification) {
		notifications.add(0, notification);
	}
}
