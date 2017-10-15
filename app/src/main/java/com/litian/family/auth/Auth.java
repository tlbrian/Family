package com.litian.family.auth;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by TianLi on 2017/10/13.
 */

public class Auth {
	private static final String TAG = "Auth";

	private static Auth instance;

	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;


	private Auth() {}

	public static void init() {
		instance = new Auth();

		instance.mAuth = FirebaseAuth.getInstance();
		instance.mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					// User is signed in
					Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
				} else {
					// User is signed out
					Log.d(TAG, "onAuthStateChanged:signed_out");
				}
				// ...
			}
		};

	}

	public static Auth getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}


	public void signIn(final String email, final String password, final SignInListener listener) {
		mAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
						listener.onSignInResult(task.isSuccessful());
					}
				});
	}

	public void signUp(final String email, final String password, final SignUpListener listener) {
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						Log.d(TAG, "signUpWithEmail:onComplete:" + task.isSuccessful());
						listener.onSignUpResult(task.isSuccessful());
					}
				});
	}

	public void signOut()  {
		mAuth.signOut();
	}

	public FirebaseUser getCurrentUser() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			// Name, email address, and profile photo Url
			String name = user.getDisplayName();
			String email = user.getEmail();
			Uri photoUrl = user.getPhotoUrl();

			// The user's ID, unique to the Firebase project. Do NOT use this value to
			// authenticate with your backend server, if you have one. Use
			// FirebaseUser.getToken() instead.
			String uid = user.getUid();
		}

		return user;
	}


	public interface SignInListener {
		void onSignInResult(boolean success);
	}

	public interface SignUpListener {
		void onSignUpResult(boolean success);
	}
}
