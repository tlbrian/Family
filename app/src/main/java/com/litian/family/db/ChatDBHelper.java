/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family.db;

import com.litian.family.db.ChatContract.ChatEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ChatDBHelper extends SQLiteOpenHelper{
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ChatContact.db";
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    
    private static final String SQL_CREATE_ENTRIES=
    		"CREATE TABLE "+ ChatEntry.TABLE_NAME + 
    		" ("+
    		ChatEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
    		ChatEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
    		ChatEntry.COLUMN_IP + TEXT_TYPE +
    		" )";
    
    public ChatDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

        //Initially add the first row
        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_NAME, "Me");
        values.put(ChatEntry.COLUMN_IP, "localhost");
        db.insert(ChatEntry.TABLE_NAME, null, values);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    }

    
}



