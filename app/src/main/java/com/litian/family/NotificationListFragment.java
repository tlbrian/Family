/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.litian.family.model.Notification;

import java.util.ArrayList;

public class NotificationListFragment extends ListFragment {

	public static final String PREFS_NAME = "UserPrefs";

//	int mCurCheckPosition = 0;
	MyAdapter mAdapter;
	
	String mCurFilter;


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.activity_main, container, false);
//    }


	public class MyAdapter extends ArrayAdapter<Notification> {
		public MyAdapter(Context context, ArrayList<Notification> notifications) {
			super(context, 0, notifications);
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			Notification notif = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notification, parent, false);
			}

			// Lookup view for data population
			TextView messageView = convertView.findViewById(R.id.notif_message);
			TextView from_user = convertView.findViewById(R.id.from_user);

			// Populate the data into the template view using the data object
			messageView.setText(notif.getMessage());
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
        setEmptyText("No Chat");

		setHasOptionsMenu(true);
		
		// Create an empty adapter we will use to display the loaded data.
	    // TODO: replace with real data
	    ArrayList<Notification> test = new ArrayList<>();
	    test.add(new Notification("message 1"));
	    test.add(new Notification("message 2"));
        mAdapter = new MyAdapter(getActivity(), test);
        setListAdapter(mAdapter);
		
        // Start out with a progress indicator.
        setListShown(true);
		
		//ReceiverDaemon.getInstance();
	}



    //Menu
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		menu.clear();
//		inflater.inflate(R.menu.main, menu);
//		SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
//		sv.setOnQueryTextListener(this);
//		//SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//		//SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//		//searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//	}


//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		super.onOptionsItemSelected(item);
//		return true;
//	}


}
