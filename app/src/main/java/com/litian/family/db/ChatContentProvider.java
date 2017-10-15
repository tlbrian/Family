/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family.db;

import java.util.HashMap;

import com.litian.family.db.ChatContract.ChatEntry;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ChatContentProvider extends ContentProvider{
	
	private ChatDBHelper mDBHelper;
    
    public static final String AUTHORITY = "chn.phantom.providers";
	
    private static final int CHAT_TABLE = 1;
    
    private static final int CHAT_ID = 2;
    
	private static final UriMatcher sUriMatcher;
	
	private static HashMap<String, String> chatProjectionMap;
	
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, ChatEntry.TABLE_NAME, CHAT_TABLE);
        sUriMatcher.addURI(AUTHORITY, ChatEntry.TABLE_NAME + "/#", CHAT_ID);
 
        chatProjectionMap = new HashMap<String, String>();
        chatProjectionMap.put(ChatEntry._ID, ChatEntry._ID);
        chatProjectionMap.put(ChatEntry.COLUMN_NAME, ChatEntry.COLUMN_NAME);
        chatProjectionMap.put(ChatEntry.COLUMN_IP, ChatEntry.COLUMN_IP);      
    }
    
    @Override
    public boolean onCreate() {

        /*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        mDBHelper = new ChatDBHelper(getContext());

        return true;
    }
    
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CHAT_TABLE:
                return ChatEntry.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ChatEntry.TABLE_NAME);
        qb.setProjectionMap(chatProjectionMap);
 
        switch (sUriMatcher.match(uri)) {    
            case CHAT_TABLE:
                break;
            case CHAT_ID:
                selection = selection + " AND _id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    
    
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != CHAT_TABLE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
 
        if (values == null) {
        	throw new SQLException("Failed to insert row into " + uri);
        }
 
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long rowId = db.insert(ChatEntry.TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(ChatEntry.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        
        throw new SQLException("Failed to insert row into " + uri);
    }
    
    
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case CHAT_TABLE:
                count = db.update(ChatEntry.TABLE_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
 
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    
    
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CHAT_TABLE:
                break;
            case CHAT_ID:
                where = where + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
 
        int count = db.delete(ChatEntry.TABLE_NAME, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
