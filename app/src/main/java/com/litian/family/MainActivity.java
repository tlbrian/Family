/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.astuetz.PagerSlidingTabStrip;
import com.litian.family.auth.Auth;
import com.litian.family.firestore.MyFirestore;

public class MainActivity extends Activity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;


	public static final String PREFS_NAME = "UserPrefs";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		PagerSlidingTabStrip tabs = findViewById(R.id.tabs);
		tabs.setTextSize(17);
		tabs.setTabPaddingLeftRight(10);
		tabs.setShouldExpand(true);
		tabs.setViewPager(mViewPager);

		MyFirestore.getInstance().listenToFriendEvents(UserProfile.getInstance().getCurrentUser(), new MyFirestore.OnAccessDatabase<MyFirestore.DatabaseEventType>() {
				@Override
				public void onComplete(MyFirestore.DatabaseEventType data) {
					if (data == MyFirestore.DatabaseEventType.add) {
						Fragment f = mSectionsPagerAdapter.getFragment(mViewPager, 1);
						if (f != null) {
							((FriendListFragment)f).notifyDataSetChanged();
						}
					}
				}
			});

		MyFirestore.getInstance().listenToFriendRequestEvents(UserProfile.getInstance().getCurrentUser(), new MyFirestore.OnAccessDatabase<MyFirestore.DatabaseEventType>() {
				@Override
				public void onComplete(MyFirestore.DatabaseEventType data) {
					if (data == MyFirestore.DatabaseEventType.add) {
						Fragment f = mSectionsPagerAdapter.getFragment(mViewPager, 2);
						if (f != null) {
							((NotificationListFragment)f).notifyDataSetChanged();
						}
					}
				}
		});
    }


//	//Menu
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
//
//		//SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//		//SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//		//searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//
//		return true;
//	}
//
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		super.onOptionsItemSelected(item);
//		if (item.getItemId() == R.id.add_person) {
//			//add a new person here
//			AddNewDialogFragment addNewDialog = new AddNewDialogFragment();
//			addNewDialog.show(getFragmentManager(), "add_new_chat");
//		}
//		else if (item.getItemId() == R.id.action_logout) {
//			Log.d("test", "lt >>>show log out dialog");
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//			builder.setMessage(R.string.logout_message)
//					.setTitle(R.string.logout)
//					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//							// User clicked OK button
//							signOutAndGoToSignInScreen();
//						}
//					})
//					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//							// User cancelled the dialog
//						}
//					});
//
//			AlertDialog dialog = builder.create();
//			dialog.show();
//		}
//		return true;
//	}

	private void signOutAndGoToSignInScreen() {
		Auth.getInstance().signOut();

		SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove("email");
		editor.remove("password");
		editor.commit();

		Intent intent = new Intent(MainActivity.this,
				LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}



	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			switch (position) {
				case 0:
					return new Fragment();
				case 1:
					return new FriendListFragment();
				case 2:
					return new NotificationListFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return "Chats";
				case 1:
					return "Friends";
				case 2:
					return "Notifications";
			}
			return null;
		}

		/**
		 * @param containerViewId the ViewPager this adapter is being supplied to
		 * @param position pass in getItemId(position) as this is whats used internally in this class
		 * @return the tag used for this pages fragment
		 */
		public String makeFragmentTag(int containerViewId, int position) {
			return "android:switcher:" + containerViewId + ":" + position;
		}


		/**
		 * @return may return null if the fragment has not been instantiated yet for that position - this depends on if the fragment has been viewed
		 * yet OR is a sibling covered by {@link android.support.v4.view.ViewPager#setOffscreenPageLimit(int)}. Can use this to call methods on
		 * the current positions fragment.
		 */
		public @Nullable Fragment getFragment(ViewPager container, int position) {
			String tag = makeFragmentTag(container.getId(), position);
			return getFragmentManager().findFragmentByTag(tag);
		}
	}
}
