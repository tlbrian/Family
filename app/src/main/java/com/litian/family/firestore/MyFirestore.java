package com.litian.family.firestore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.litian.family.UserProfile;
import com.litian.family.auth.Auth;
import com.litian.family.model.Friend;
import com.litian.family.model.Message;
import com.litian.family.model.Notification;
import com.litian.family.model.User;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by TianLi on 2017/10/13.
 */

public class MyFirestore {
	private static final String TAG = "Firestore";

	private static final String userCollectionName = "users";
	private static final String friendReqCollectionName = "friend_requests";
	private static final String friendshipCollectionName = "friendships";
	private static final String chatCollectionName = "chats";



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



	/**
	 *
	 * @param token
	 */
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


	/**
	 *
	 * @param user
	 * @param listener
	 */
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


	/**
	 *
	 * @param uid
	 * @param listener
	 */
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


	/**
	 *
	 * @param email
	 * @param listener
	 */
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
					if (documents != null && documents.size() == 1) {
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


	/**
	 *
	 * @param fromUser
	 * @param toUser
	 * @param listener
	 */
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
	public void updateFriendRequestAsDone(@NonNull final String from_uid, @NonNull final String to_uid, final OnAccessDatabase<Notification> listener) {
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
						if (documents != null && documents.size() == 1) {
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


	/**
	 *
	 * @param toUser
	 * @param listener
	 */
	public void searchFriendRequests(@NonNull final User toUser, final OnAccessDatabase<List<Notification>> listener) {
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


	/**
	 *
	 * @param fromUser
	 * @param toUser
	 * @param listener
	 */
	public void createFriendship(@NonNull User fromUser, @NonNull final User toUser, final OnAccessDatabase<Friend> listener) {
		final Friend friend = new Friend(fromUser, toUser);

		db.collection(friendshipCollectionName)
				.add(friend)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {
						Log.d(TAG, "createFriendship success");
						if (listener != null) listener.onComplete(friend);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "Error adding document", e);
						if (listener != null) listener.onComplete(null);
					}
				});


		Friend friend_2 = new Friend(toUser, fromUser);
		db.collection(friendshipCollectionName).add(friend_2);
	}


	/**
	 *
	 * @param fromUser
	 * @param listener
	 */
	public void searchFriends(@NonNull final User fromUser, final OnAccessDatabase<List<Friend>> listener) {
		Query query = db.collection(friendshipCollectionName)
				.whereEqualTo("friendOf", fromUser.getUid());

		// Add a new document with a generated ID
		query.get()
				.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
					@Override
					public void onSuccess(QuerySnapshot querySnapshot) {
						Log.d(TAG, "Friend request retrieved");
						List<DocumentSnapshot> documents = querySnapshot.getDocuments();
						List<Friend> friends = new ArrayList<>(documents.size());
						for (DocumentSnapshot documentSnapshot : documents) {
							Friend friend = documentSnapshot.toObject(Friend.class);
							friends.add(friend);
						}
						listener.onComplete(friends);
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
	 *
	 * @param fromUser
	 * @param toUser
	 * @param message
	 * @param listener
	 */
	public void addMessage(@NonNull User fromUser, @NonNull User toUser, @NonNull final Message message, final OnAccessDatabase<Message> listener) {
		String chatName = fromUser.getUid() + "_" + toUser.getUid();
		db.collection(chatCollectionName)
				.document(chatName)
				.collection("messages")
				.add(message)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {
						Log.w(TAG, "message added");
						if (listener != null) listener.onComplete(message);
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
	 *
	 * @param currentUser
	 */
	public void listenToFriendEvents(final User currentUser, final OnAccessDatabase<DatabaseEventType> listener) {
		// be notified when friend accept the request
		db.collection(friendshipCollectionName)
				.whereEqualTo("friendOf", currentUser.getUid())
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
										UserProfile.getInstance().addFriend(dc.getDocument().toObject(Friend.class));
										if (listener != null) listener.onComplete(DatabaseEventType.add);
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

					}
				});
	}

	public void listenToFriendRequestEvents(final User currentUser, final OnAccessDatabase<DatabaseEventType> listener) {
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
									UserProfile.getInstance().addNotification(dc.getDocument().toObject(Notification.class));
									if (listener != null) listener.onComplete(DatabaseEventType.add);
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



	public ListenerRegistration listenToMessageEvents(final String fromUid, final String toUid, final OnAccessDatabase<MessageWithType> listener) {
		String chatName = fromUid + "_" + toUid;

		ListenerRegistration registration = db.collection(chatCollectionName)
				.document(chatName)
				.collection("messages")
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
									if (listener != null) listener.onComplete(new MessageWithType(DatabaseEventType.add, dc.getDocument().toObject(Message.class)));
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
		return registration;
	}

	public class MessageWithType {
		public DatabaseEventType databaseEventType;
		public Message message;

		public MessageWithType(DatabaseEventType type, Message m) {
			databaseEventType = type;
			message = m;
		}
	}






	public interface OnAccessDatabase<T> {
		void onComplete(T data);
	}

	public enum DatabaseEventType {
		add,
		modify,
		remove
	}
}
