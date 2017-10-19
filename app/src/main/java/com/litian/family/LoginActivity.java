package com.litian.family;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.litian.family.auth.Auth;
import com.litian.family.firestore.MyFirestore;
import com.litian.family.messaging.MyFirebaseInstanceIDService;
import com.litian.family.model.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
	private static final String TAG = "Login";
	private static final String PREFS_NAME = "UserPrefs";

	// UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
		findViewById(R.id.email_sign_up_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				signUp();
			}
		});


        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    private void signIn() {
	    // Store values at the time of the login attempt.
	    final String email = mEmailView.getText().toString();
	    final String password = mPasswordView.getText().toString();

	    if (!attemptLogin()) return;

		Auth.getInstance().signIn(email, password, new Auth.SignInListener() {
			@Override
			public void onSignInResult(boolean success) {
				if (success) {
					showProgress(false);

					MyFirestore.getInstance().searchUserByEmail(email, new MyFirestore.OnAccessDatabase<User>() {
						@Override
						public void onComplete(User user) {
							if (user != null) {
								Log.d(TAG, "Log in success");

								// Save the valid email and password to SharePreference
								saveLoginToSharePrefs(email, password);

								CurrentUser.set(user);

								gotoChatList();
							}
							else {
								Log.d(TAG, "Log in failed, no user in database");
								gotoChatList();
							}
						}
					});
				} else {
					Toast.makeText(LoginActivity.this, R.string.auth_failed,
							Toast.LENGTH_SHORT).show();
					showProgress(false);
				}
			}
		});
    }

    private void signUp() {
	    // Store values at the time of the login attempt.
	    String email = mEmailView.getText().toString();
	    String password = mPasswordView.getText().toString();

	    if (!attemptLogin()) return;

	    Auth.getInstance().signUp(email, password, new Auth.SignUpListener() {
		    @Override
		    public void onSignUpResult(boolean success) {
			    showProgress(false);
			    if (success) {
					//TODO: Send account to firestore
					FirebaseUser user = Auth.getInstance().getCurrentUser();

					User newUser = new User(user.getUid(), user.getEmail());
					newUser.setFCMToken(MyFirebaseInstanceIDService.getToken());
					MyFirestore.getInstance().createUserAccount(newUser, null);
				}
				else {
					Toast.makeText(LoginActivity.this, R.string.sign_up_failed,
							Toast.LENGTH_SHORT).show();
				}
		    }
	    });
    }

	/**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
        }

        return !cancel;
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
	    int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

	    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	    mLoginFormView.animate().setDuration(shortAnimTime).alpha(
	            show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
	        @Override
	        public void onAnimationEnd(Animator animation) {
	            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	        }
	    });

	    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
	    mProgressView.animate().setDuration(shortAnimTime).alpha(
	            show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
	        @Override
	        public void onAnimationEnd(Animator animation) {
	            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
	        }
	    });
    }


    private void saveLoginToSharePrefs(String email, String password) {
	    SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);

	    SharedPreferences.Editor editor = sp.edit();
	    editor.putString("email", email);
	    editor.putString("password", password);
	    editor.apply();
    }



    private void gotoChatList() {
	    Intent intent = new Intent(LoginActivity.this,
			    MainActivity.class);
	    startActivity(intent);
    }

}

