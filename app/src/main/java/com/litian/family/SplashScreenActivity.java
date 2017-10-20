/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.litian.family.auth.Auth;
import com.litian.family.firestore.MyFirestore;
import com.litian.family.messaging.MyFirebaseInstanceIDService;
import com.litian.family.model.User;


public class SplashScreenActivity extends Activity
{
    public static final String PREFS_NAME = "UserPrefs";
	private static final String TAG = "Splash";
	private static long SPLASH_MILLIS = 1000;

    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
/*        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
            R.layout.splash_screen, null, false);
        
        addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));*/
        setContentView(R.layout.splash_screen);


        // init components
	    UserProfile.init();
        Auth.init();
	    MyFirestore.init();
	    MyFirebaseInstanceIDService.getToken();

	    // get data passed in from deep link or notification
	    UserProfile.getInstance().setData(getIntent().getExtras());

	    // try sign in or go to LoginActivity
        signInWithToken();
    }

    public void signInWithToken() {
        SharedPreferences profile = getSharedPreferences(PREFS_NAME, 0);
        final String email = profile.getString("email", null);
        final String password = profile.getString("password", null);

        if (email != null && password != null) {
            Auth.getInstance().signIn(email, password, new Auth.SignInListener() {
                @Override
                public void onSignInResult(boolean success) {
                    if (success) {
	                    MyFirestore.getInstance().searchUserByEmail(email, new MyFirestore.OnAccessDatabase<User>() {
		                    @Override
		                    public void onComplete(User data) {
			                    if (data != null) {
				                    Log.d(TAG, "Log in success");
				                    UserProfile.getInstance().setCurrentUser(data);
				                    gotoActivity(MainActivity.class);
			                    }
			                    else {
				                    Log.d(TAG, "Log in failed, no user in database");
				                    gotoActivity(LoginActivity.class);
			                    }
		                    }
	                    });
                    }
                    else {
                        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("email");
                        editor.remove("password");
                        editor.apply();

	                    gotoActivity(LoginActivity.class);
                    }
                }
            });
        }
        else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
					gotoActivity(LoginActivity.class);

                }

            }, SPLASH_MILLIS);
        }
    }

    public void gotoActivity(Class<? extends Activity> activity) {
	    Intent intent = new Intent(SplashScreenActivity.this,
			    activity);
	    startActivity(intent);
    }
    
}
