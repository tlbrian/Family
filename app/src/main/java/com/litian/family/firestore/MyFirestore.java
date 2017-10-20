package com.litian.family.firestore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.litian.family.UserProfile;
import com.litian.family.auth.Auth;
import com.litian.family.model.Notification;
import com.litian.family.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TianLi on 2017/10/13.
 */

public class MyFirestore {
	private static final String TAG = "MyFireStore";

	private static final String userCollectionName = "users";
	private static final String friendReqCollectionName = "friend_requests";



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

	public void createUserAccount(@NonNull final User user, final OnAccessDatabase<User> listener) {
		// Add a new document with a generated ID
		db.collection(userCollectionName).document(user.getUid())
				.set(user)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid());
						if (listener != null) listener.onComplete(user);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error adding document", e);
						if (listener != null) listener.onComplete(null);
					}
				});
	}

	public void searchUserByUid(@NonNull String uid, final OnAccessDatabase<User> listener) {
		// Create a reference to the cities collection
		DocumentReference usersRef = db.collection(userCollectionName).document(uid);

		usersRef.get()
				.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
					@Override
					public void onSuccess(DocumentSnapshot documentSnapshot) {
						Log.d(TAG, "searchUserByUid data: " + documentSnapshot.getData());
						User user = documentSnapshot.toObject(User.class);
						listener.onComplete(user);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error reading document", e);
						if (listener != null) listener.onComplete(null);
					}
				});
	}

	public void searchUserByEmail(@NonNull String email, final OnAccessDatabase<User> listener) {
		// Create a reference to the cities collection
		CollectionReference usersRef = db.collection(userCollectionName);

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
						Log.d(TAG, "searchUserByEmail data: " + document.getData());
						listener.onComplete(user);
					} else {
						Log.d(TAG, "No such document");
						listener.onComplete(null);
					}
				} else {
					Log.d(TAG, "get failed with ", task.getException());
					listener.onComplete(null);
				}
			}
		});
	}





	public void sendFriendRequest(@NonNull final User fromUser, @NonNull final User toUser, final OnAccessDatabase<User> listener) {
		Map<String, Object> request = new HashMap<>();
		request.put("from_uid", fromUser.getUid());
		request.put("from_email", fromUser.getEmail());
		request.put("to_uid", toUser.getUid());
		request.put("to_FCMToken", toUser.getFCMToken());
		request.put("isDone", false);

		// Add a new document with a generated ID
		db.collection(friendReqCollectionName)
				.add(request)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {
						Log.d(TAG, "friend request sent");
						if (listener != null) listener.onComplete(toUser);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error adding document", e);
						if (listener != null) listener.onComplete(null);
					}
				});
	}

	/**
	 * Confirm that friend hss been made in database. Set the friend request isDone to true
	 * @param from_uid
	 * @param to_uid
	 * @param listener
	 */
	public void updateFriendRequest(@NonNull final String from_uid, @NonNull final String to_uid, final OnAccessDatabase<Notification> listener) {
		Query query = db.collection(friendReqCollectionName)
				.whereEqualTo("from_uid", from_uid)
				.whereEqualTo("to_uid", to_uid)
				.whereEqualTo("isDone", false);

		// Add a new document with a generated ID
		query.get()
				.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
					@Override
					public void onSuccess(QuerySnapshot querySnapshot) {
						Log.d(TAG, "Friend request retrieved");
						List<DocumentSnapshot> documents = querySnapshot.getDocuments();
						if (documents != null && documents.size() > 0) {
							final DocumentSnapshot documentSnapshot = documents.get(0);
							documentSnapshot.getReference().update("isDone", true)
									.addOnSuccessListener(new OnSuccessListener<Void>() {
										@Override
										public void onSuccess(Void aVoid) {
											Log.d(TAG, "DocumentSnapshot successfully updated!");
											Notification notification = documentSnapshot.toObject(Notification.class);
											if (listener != null) listener.onComplete(notification);
										}
									})
									.addOnFailureListener(new OnFailureListener() {
										@Override
										public void onFailure(@NonNull Exception e) {
											Log.w(TAG, "Error updating document", e);
										}
									});
						} else {
							Log.d(TAG, "No such document");
							if (listener != null) listener.onComplete(null);
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error adding document", e);
						if (listener != null) listener.onComplete(null);
					}
				});
	}


	public void listenToDatabaseEvents(final User currentUser) {
		// be notified when friend accept the request
		db.collection(friendReqCollectionName)
				.whereEqualTo("from_uid", currentUser.getUid())
				.addSnapshotListener(new EventListener<QuerySnapshot>() {
					@Override
					public void onEvent(@Nullable QuerySnapshot snapshots,
					                    @Nullable FirebaseFirestoreException e) {
						if (e != null) {
							Log.w(TAG, "listen:error", e);
							return;
						}

						if (snapshots != null) {
							for (DocumentChange dc : snapshots.getDocumentChanges()) {
								switch (dc.getType()) {
									case ADDED:
										Log.d(TAG, "New: " + dc.getDocument().getData());
										break;
									case MODIFIED:
										Log.d(TAG, "Modified: " + dc.getDocument().getData());
										DocumentSnapshot documentSnapshot = dc.getDocument();
										if (documentSnapshot.getBoolean("isDone")) {
											UserProfile.getInstance().addFriend(documentSnapshot.getString("to_uid"));
											instance.updateFriendList(currentUser, null);
										}
										break;
									case REMOVED:
										Log.d(TAG, "Removed: " + dc.getDocument().getData());
										break;
								}
							}
						}

					}
				});

		// be notified when receiving a friend request
		db.collection(friendReqCollectionName)
				.whereEqualTo("to_uid", currentUser.getUid())
				.addSnapshotListener(new EventListener<QuerySnapshot>() {
					@Override
					public void onEvent(@Nullable QuerySnapshot snapshots,
					                    @Nullable FirebaseFirestoreException e) {
						if (e != null) {
							Log.w(TAG, "listen:error", e);
							return;
						}

						for (DocumentChange dc : snapshots.getDocumentChanges()) {
							switch (dc.getType()) {
								case ADDED:
									Log.d(TAG, "New: " + dc.getDocument().getData());
									currentUser.addNotification(dc.getDocument().toObject(Notification.class));
									break;
								case MODIFIED:
									Log.d(TAG, "Modified: " + dc.getDocument().getData());
									break;
								case REMOVED:
									Log.d(TAG, "Removed: " + dc.getDocument().getData());
									break;
							}
						}

					}
				});
	}


	public void searchFriendRequestsToUser(@NonNull final User toUser, final OnAccessDatabase<List<Notification>> listener) {
		Query query = db.collection(friendReqCollectionName)
				.whereEqualTo("to_uid", toUser.getUid());

		// Add a new document with a generated ID
		query.get()
				.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
					@Override
					public void onSuccess(QuerySnapshot querySnapshot) {
						Log.d(TAG, "Friend request retrieved");
						List<DocumentSnapshot> documents = querySnapshot.getDocuments();
						List<Notification> notifications = new ArrayList<>(documents.size());
						for (DocumentSnapshot documentSnapshot : documents) {
							Notification notification = documentSnapshot.toObject(Notification.class);
							notifications.add(notification);
						}
						listener.onComplete(notifications);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error adding document", e);
						if (listener != null) listener.onComplete(null);
					}
				});
	}





	public void updateFriendList(@NonNull User currentUser, final OnAccessDatabase<Boolean> listener) {
		db.collection(userCollectionName).document(currentUser.getUid())
				.update("friendUids", currentUser.getFriendUids())
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Log.d(TAG, "Friend list successfully updated!");
						if (listener != null) listener.onComplete(true);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error updating document", e);
						if (listener != null) listener.onComplete(false);
					}
				});
	}




	public void updateFCMToken(@NonNull String token) {
		FirebaseUser user = Auth.getInstance().getCurrentUser();
		DocumentReference userRef = db.collection(userCollectionName).document(user.getUid());

		userRef .update("FCMToken", token)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Log.d(TAG, "FCM Token successfully updated!");
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error updating document", e);
					}
				});
	}




	public interface OnAccessDatabase<T> {
		void onComplete(T data);
	}
}
