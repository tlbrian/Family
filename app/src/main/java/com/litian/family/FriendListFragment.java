/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.litian.family.firestore.MyFirestore;
import com.litian.family.model.Friend;
import com.litian.family.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FriendListFragment extends ListFragment implements OnQueryTextListener {

	public static final String PREFS_NAME = "UserPrefs";
	private static final String TAG = "FriendList";

//	int mCurCheckPosition = 0;
	MyAdapter mAdapter;
	
	String mCurFilter;


	//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.activity_main, container, false);
//    }



	public class MyAdapter extends ArrayAdapter<Friend> {
		public MyAdapter(Context context, List<Friend> friends) {
			super(context, 0, friends);
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			Friend friend = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_friend, parent, false);
			}

			// Lookup view for data population
			TextView chatName = convertView.findViewById(R.id.friend_name);
			TextView lastMessage = convertView.findViewById(R.id.last_message);

			// Populate the data into the template view using the data object
			chatName.setText(friend.getName());
//			lastMessage.setText(user.hometown);

			// Return the completed view to render on screen
			return convertView;
		}
	}



    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

	    setEmptyText(getString(R.string.no_friends));

	    // get real data from db and attach to adapter
	    mAdapter = new MyAdapter(getActivity(), UserProfile.getInstance().getFriends());
	    setListAdapter(mAdapter);
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Friend friend = mAdapter.getItem(position);

		Intent intent = new Intent(getActivity(),
				ChatActivity.class);
		intent.putExtra("name", friend.getName());
		intent.putExtra("uid", friend.getUid());
		startActivity(intent);
	}


	void notifyDataSetChanged() {
		if (mAdapter != null) mAdapter.notifyDataSetChanged();
	}


	//Menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.clear();
		inflater.inflate(R.menu.main, menu);
		SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
		sv.setOnQueryTextListener(this);
		//SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		//SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		//searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		return true;
	}
	
	
	@Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
		// the search filter, and restart the loader to do a new query
		// with this filter.
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;

        return true;
    }
	

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }


}
