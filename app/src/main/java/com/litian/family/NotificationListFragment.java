/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.litian.family.firestore.MyFirestore;
import com.litian.family.model.Notification;
import com.litian.family.model.User;

import java.util.ArrayList;
import java.util.List;

public class NotificationListFragment extends ListFragment {

	public static final String PREFS_NAME = "UserPrefs";
	private static final String TAG = "Main";

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
			final Notification notification = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notification, parent, false);
			}

			// Lookup view for data population
			TextView messageView = convertView.findViewById(R.id.notif_message);
			TextView from_user = convertView.findViewById(R.id.from_user);
			convertView.findViewById(R.id.button_accept_friend).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					acceptFriendRequest(notification);
				}
			});

			// Populate the data into the template view using the data object
			messageView.setText(notification.getMessage());
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
        setListShown(false);

	    MyFirestore.getInstance().searchFriendRequestsToUser(CurrentUser.get(), new MyFirestore.OnAccessDatabase<List<Notification>>() {
		    @Override
		    public void onComplete(List<Notification> data) {
			    mAdapter.addAll(data);
			    setListShown(true);
		    }
	    });

	}


	private void acceptFriendRequest(Notification notification) {
		MyFirestore.getInstance().searchUserByUid(notification.getFrom_uid(), new MyFirestore.OnAccessDatabase<User>() {
			@Override
			public void onComplete(User data) {
				if (data != null) {
					CurrentUser.get().addFriend(data.getUid());
					MyFirestore.getInstance().updateFriendList(CurrentUser.get(), data, new MyFirestore.OnAccessDatabase<User>() {
						@Override
						public void onComplete(User data) {
							Toast.makeText(getActivity(), "Friend added Start chat now!", Toast.LENGTH_SHORT).show();

							// TODO: update ChatList
						}
					});
				}
			}
		});

		MyFirestore.getInstance().updateFriendRequest(notification.getFrom_uid(), CurrentUser.get().getUid(), null);
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
