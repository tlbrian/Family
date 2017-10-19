package com.litian.family.model;

import java.util.List;

/**
 * Created by TianLi on 2017/10/13.
 */

public class Notification {
	String from_uid;
	String to_uid;
	String toFCM_Token;
	String message;
	boolean isDone;
	private String from_FCMToken;

	public Notification() {}

	public Notification(String message) {
		this.message = message;
	}

	public Notification(String uid, String message) {
		this.from_uid = uid;
		this.message = message;
	}

	public String getFrom_uid() {
		return from_uid;
	}

	public String getMessage() {
		return message;
	}

	public String getFrom_FCMToken() {
		return from_FCMToken;
	}

	public String getTo_uid() {
		return to_uid;
	}

	public String getToFCM_Token() {
		return toFCM_Token;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setFrom_FCMToken(String from_FCMToken) {
		this.from_FCMToken = from_FCMToken;
	}
}
