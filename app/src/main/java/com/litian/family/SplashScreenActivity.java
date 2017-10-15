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
import android.view.Window;
import android.view.WindowManager;

import com.litian.family.auth.Auth;
import com.litian.family.firestore.MyFirestore;
import com.litian.family.messaging.MyFirebaseInstanceIDService;


public class SplashScreenActivity extends Activity
{
    public static final String PREFS_NAME = "UserPrefs";
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

        MyFirebaseInstanceIDService.getToken();

        //init components
        Auth.init();
	    MyFirestore.init();

        SharedPreferences profile = getSharedPreferences(PREFS_NAME, 0);
        String email = profile.getString("email", null);
        String password = profile.getString("password", null);

        if (email != null && password != null) {
            Auth.getInstance().signIn(email, password, new Auth.SignInListener() {
                @Override
                public void onSignInResult(boolean success) {
                    if (success) {
                        Intent intent = new Intent(SplashScreenActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
                    else {
	                    SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);

	                    SharedPreferences.Editor editor = sp.edit();
	                    editor.remove("email");
	                    editor.remove("password");
	                    editor.commit();

                        Intent intent = new Intent(SplashScreenActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    Intent intent = new Intent(SplashScreenActivity.this,
                            LoginActivity.class);
                    startActivity(intent);

                }

            }, SPLASH_MILLIS);
        }
    }
    
}
