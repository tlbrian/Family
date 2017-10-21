package com.litian.family.firestore;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/**
 * Created by TianLi on 2017/10/20.
 */

public class FirestoreWrapper {

	private static final String TAG = "Firestore";

	public void addDocument(String collectionName, Map<String, Object> data, final MyFirestore.OnAccessDatabase<Boolean> listener) {
		// Add a new document with a generated ID
		FirebaseFirestore.getInstance().collection(collectionName)
				.add(data)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {
						Log.d(TAG, "friend request sent");
						if (listener != null) listener.onComplete(true);
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
}
