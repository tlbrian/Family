package com.litian.family.messaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.litian.family.firestore.MyFirestore;

/**
 * Created by TianLi on 2017/10/13.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
	private static final String TAG = "FCM";

	@Override
	public void onTokenRefresh() {
		// Get updated InstanceID token.
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.d(TAG, "Refreshed token: " + refreshedToken);

		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		sendRegistrationToServer(refreshedToken);
	}

	public void sendRegistrationToServer(String token) {
		MyFirestore.getInstance().updateCMToken(token);
	}

	public static String getToken() {
		String token = FirebaseInstanceId.getInstance().getToken();
		Log.d(TAG, "getToken: " + token);
		return token;
	}
}
