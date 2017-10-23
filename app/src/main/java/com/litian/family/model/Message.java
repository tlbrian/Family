package com.litian.family.model;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by TianLi on 2017/10/23.
 */

public class Message {
	private String from_uid;
	private String message;
	private @ServerTimestamp Date timestamp;

	public Message() {}

	public Message(String uid, String message) {
		this.from_uid = uid;
		this.message = message;
		setTimestamp();
	}

	public String getFrom_uid() {
		return from_uid;
	}

	public void setFrom_uid(String from_uid) {
		this.from_uid = from_uid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public @ServerTimestamp Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp() {
		this.timestamp = Calendar.getInstance().getTime();
	}
}
