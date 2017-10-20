/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import android.app.ListFragment;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.litian.family.model.User;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends ListFragment implements OnQueryTextListener {

	public static final String PREFS_NAME = "UserPrefs";
	private static final String TAG = "FriendList";

//	int mCurCheckPosition = 0;
	MyAdapter mAdapter;
	
	String mCurFilter;

	public FriendListFragment() {
		super();
	}


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_friendlist, container, false);
//    }


	public class MyAdapter extends ArrayAdapter<User> {
		public MyAdapter(Context context, ArrayList<User> users) {
			super(context, 0, users);
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			User user = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_friend, parent, false);
			}

			// Lookup view for data population
			TextView chatName = convertView.findViewById(R.id.friend_name);
			TextView lastMessage = convertView.findViewById(R.id.last_message);

			// Populate the data into the template view using the data object
			chatName.setText(user.getEmail());
//			lastMessage.setText(user.hometown);

			// Return the completed view to render on screen
			return convertView;
		}
	}
	
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText(getString(R.string.no_friends));

		setHasOptionsMenu(true);
		
		// Create an empty adapter we will use to display the loaded data.
	    // TODO: replace with real data
	    ArrayList<User> test = new ArrayList<>();
	    test.add(new User("test1@lt.com"));
	    test.add(new User("test2@lt.com"));
        mAdapter = new MyAdapter(getActivity(), test);
	    List<String> uids = UserProfile.getInstance().getCurrentUser().getFriendUids();
	    if (uids != null) {
		    for (String uid : uids) {
			    test.add(new User(uid, null));
		    }
	    }

	    setListAdapter(mAdapter);

	}


	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume called");
		if (UserProfile.getInstance().isFriendListDirty()) {
			UserProfile.getInstance().setFriendListDirty(false);
			Log.d(TAG, "friend is dirty");
			mAdapter.clear();
			for (String uid : UserProfile.getInstance().getCurrentUser().getFriendUids()) {
				mAdapter.add(new User(uid, null));
			}
		}
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
