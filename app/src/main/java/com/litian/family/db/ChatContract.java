/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ChatContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ChatContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ChatEntry implements BaseColumns {
    	
        public static final Uri CONTENT_URI = Uri.parse(
        		"content://" + ChatContentProvider.AUTHORITY + "/"+ ChatEntry.TABLE_NAME);
        
    	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.chn.phantom.provider.table";
    	
    	//Columns
        public static final String TABLE_NAME = "ChatContact";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IP = "ip";
    }
}