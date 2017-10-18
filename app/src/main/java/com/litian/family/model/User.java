package com.litian.family.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TianLi on 2017/10/13.
 */

public class User {
	String uid;
	String email;
	String name;
	List<String> friendUids = new ArrayList<>();
	private String FCMToken;

	public User() {}

	public User(String email) {
		this.email = email;
	}

	public User(String uid, String email) {
		this.uid = uid;
		this.email = email;
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
		friendUids.add(uid);
	}

	public void setFCMToken(String FCMToken) {
		this.FCMToken = FCMToken;
	}
}
