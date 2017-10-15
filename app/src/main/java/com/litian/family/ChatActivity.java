/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class ChatActivity extends Activity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            ChatFragment chat = new ChatFragment();
            chat.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, chat).commit();
        }
    }
}
