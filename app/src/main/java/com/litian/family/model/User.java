package com.litian.family.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TianLi on 2017/10/13.
 */

public class User {
	private String uid;
	private String email;
	private String name;
	private List<String> friendUids;
	private List<Notification> notifications;
	private String FCMToken;

	public User() {}

	public User(String email) {
		this.email = email;
		this.friendUids = new ArrayList<>(20);
		this.notifications = new ArrayList<>();
	}

	public User(String uid, String email) {
		this.uid = uid;
		this.email = email;
		this.friendUids = new ArrayList<>(20);
		this.notifications = new ArrayList<>();
	}

	public String getUid() {
		return uid;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getFCMToken() {
		return FCMToken;
	}

	public List<String> getFriendUids() {
		return friendUids;
	}

	public void addFriend(String uid) {
		friendUids.add(0, uid);
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void addNotification(Notification notification) {
		notifications.add(0, notification);
	}

	public void setFCMToken(String FCMToken) {
		this.FCMToken = FCMToken;
	}
}
