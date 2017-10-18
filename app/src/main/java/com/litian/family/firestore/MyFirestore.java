package com.litian.family.firestore;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.litian.family.CurrentUser;
import com.litian.family.auth.Auth;
import com.litian.family.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TianLi on 2017/10/13.
 */

public class MyFirestore {
	private static final String TAG = "Auth";

	private static MyFirestore instance;

	FirebaseFirestore db;

	private MyFirestore() {}

	public static MyFirestore getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}

	public static void init() {
		instance = new MyFirestore();

		instance.db = FirebaseFirestore.getInstance();
	}

	public void createUserAccount(@NonNull final User user, final AddUserCallback listener) {
		// Add a new document with a generated ID
		db.collection("users").document(user.getUid())
				.set(user)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid());
						if (listener != null) listener.onAddUserResult(user);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error adding document", e);
						if (listener != null) listener.onAddUserResult(null);
					}
				});
	}

	public void searchUserByEmail(@NonNull String email, final SearchUserCallback listener) {
		// Create a reference to the cities collection
		CollectionReference usersRef = db.collection("users");

		// Create a query against the collection.
		Query query = usersRef.whereEqualTo("email", email);

		query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task) {
				if (task.isSuccessful()) {
					QuerySnapshot query = task.getResult();
					List<DocumentSnapshot> documents = query.getDocuments();
					if (documents != null && !documents.isEmpty()) {
						DocumentSnapshot document = documents.get(0);
						User user = document.toObject(User.class);
						Log.d(TAG, "DocumentSnapshot data: " + document.getData());
						listener.onSearchUserResult(user);
					} else {
						Log.d(TAG, "No such document");
						listener.onSearchUserResult(null);
					}
				} else {
					Log.d(TAG, "get failed with ", task.getException());
					listener.onSearchUserResult(null);
				}
			}
		});
	}


	public void sendFriendRequest(@NonNull final User fromUser, @NonNull final User toUser, final SendFriendReqCallback listener) {
		Map<String, Object> request = new HashMap<>();
		request.put("from_uid", fromUser.getUid());
		request.put("from_email", fromUser.getEmail());
		request.put("to_uid", toUser.getUid());
		request.put("to_FCMToken", toUser.getFCMToken());
		request.put("isDone", false);

		// Add a new document with a generated ID
		db.collection("friend_requests")
				.add(request)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {
						Log.d(TAG, "friend request sent");
						if (listener != null) listener.onSendFriendReqResult(toUser);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error adding document", e);
						if (listener != null) listener.onSendFriendReqResult(null);
					}
				});
	}



	public void updateFCMToken(@NonNull String token) {
		FirebaseUser user = Auth.getInstance().getCurrentUser();
		DocumentReference userRef = db.collection("users").document(user.getUid());

		userRef .update("FCMToken", token)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Log.d(TAG, "DocumentSnapshot successfully updated!");
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error updating document", e);
					}
				});
	}


	public interface AddUserCallback {
		void onAddUserResult(User user);
	}

	public interface SearchUserCallback {
		void onSearchUserResult(User user);
	}

	public interface SendFriendReqCallback {
		void onSendFriendReqResult(User user);
	}
}
