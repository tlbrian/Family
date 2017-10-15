/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.litian.family.auth.Auth;
import com.litian.family.model.User;

import java.util.ArrayList;

public class ChatListLoaderFragment extends ListFragment implements OnQueryTextListener {

	public static final String PREFS_NAME = "UserPrefs";

//	int mCurCheckPosition = 0;
	UsersAdapter mAdapter;
	
	String mCurFilter;


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.activity_main, container, false);
//    }

	public class UsersAdapter extends ArrayAdapter<User> {
		public UsersAdapter(Context context, ArrayList<User> users) {
			super(context, 0, users);
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			User user = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat, parent, false);
			}

			// Lookup view for data population
			TextView chatName = (TextView) convertView.findViewById(R.id.chat_name);
			TextView lastMessage = (TextView) convertView.findViewById(R.id.last_message);

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
        setEmptyText("No Chat");

		setHasOptionsMenu(true);
		
		// Create an empty adapter we will use to display the loaded data.
	    ArrayList<User> test = new ArrayList<>();
	    test.add(new User("test1@lt.com"));
	    test.add(new User("test2@lt.com"));
        mAdapter = new UsersAdapter(getActivity(), test);
        setListAdapter(mAdapter);
		
        // Start out with a progress indicator.
        setListShown(true);
		
		//ReceiverDaemon.getInstance();
	}

	private void signOutAndGoToSignInScreen() {
		Auth.getInstance().signOut();

		SharedPreferences sp = getActivity().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove("email");
		editor.remove("password");
		editor.commit();

		Intent intent = new Intent(getActivity(),
				LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
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
		if (item.getItemId() == R.id.add_person) {
			//add a new person here
			AddNewDialogFragment addNewDialog = new AddNewDialogFragment();
			addNewDialog.show(getFragmentManager(), "add_new_chat");
		}
		else if (item.getItemId() == R.id.action_logout) {
			Log.d("test", "lt >>>show log out dialog");
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setMessage(R.string.logout_message)
					.setTitle(R.string.logout)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User clicked OK button
								signOutAndGoToSignInScreen();
							}
					})
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
					});

			AlertDialog dialog = builder.create();
			dialog.show();
		}
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
