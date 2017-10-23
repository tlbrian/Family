/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;
import com.litian.family.firestore.MyFirestore;
import com.litian.family.model.Message;
import com.litian.family.model.User;

public class ChatFragment extends Fragment {

	private RecyclerView messageRecyclerView;
	private ImageButton sendButton;
	private EditText inputEditText;

	private RecyclerView.LayoutManager layoutManager;
	MyAdapter mAdapter;

	ListenerRegistration registration;

	private String roomName;
	private User friend;
	private List<Message> messages = new ArrayList<>(50);
	
	
	public static ChatFragment newInstance(int index) {
		ChatFragment f = new ChatFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

	    Bundle bundle = getArguments();
	    String name = bundle.getString("name");
	    String uid = bundle.getString("uid");

	    roomName = name;
	    getActivity().setTitle(roomName);
    	
    	messageRecyclerView = getActivity().findViewById(R.id.view_message);
    	
    	inputEditText = getActivity().findViewById(R.id.editText_input);
		inputEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);
				}
			}
			
		});
		inputEditText.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		            sendMessage();
		            handled = true;
		        }
		        return handled;
		    }
		});
    	
    	sendButton = getActivity().findViewById(R.id.imageButton_send);
		// listen the action of button clicking
		sendButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});



	    MyFirestore.getInstance().searchUserByUid(uid, new MyFirestore.OnAccessDatabase<User>() {
		    @Override
		    public void onComplete(User data) {
			    friend = data;
		    }
	    });

	    messageRecyclerView.setHasFixedSize(true);

	    layoutManager = new LinearLayoutManager(getActivity());
	    messageRecyclerView.setLayoutManager(layoutManager);

	    mAdapter = new MyAdapter(messages);
	    messageRecyclerView.setAdapter(mAdapter);

	    registration = MyFirestore.getInstance().listenToMessageEvents(UserProfile.getInstance().getCurrentUser().getUid(), uid, new MyFirestore.OnAccessDatabase<MyFirestore.MessageWithType>() {
		    @Override
		    public void onComplete(MyFirestore.MessageWithType data) {
			    if (data.databaseEventType == MyFirestore.DatabaseEventType.add) {
				    mAdapter.add(mAdapter.getItemCount(), data.message);
			    }
		    }
	    });


	    ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
			    new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
				    @Override
				    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
						    target) {
					    return false;
				    }
				    @Override
				    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
					    messages.remove(viewHolder.getAdapterPosition());
					    mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
				    }
			    };
	    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
	    itemTouchHelper.attachToRecyclerView(messageRecyclerView);


    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (registration != null) registration.remove();
	}

	public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
		private List<Message> items;

		// Provide a reference to the views for each data item
		// Complex data items may need more than one view per item, and
		// you provide access to all the views for a data item in a view holder
		public class ViewHolder extends RecyclerView.ViewHolder {
			// each data item is just a string in this case
			public ImageView iconImage;
			public TextView selfMessage;
			public TextView friendMessage;
			public View layout;

			public ViewHolder(View v) {
				super(v);
				iconImage = v.findViewById(R.id.icon_message_friend);
				selfMessage = v.findViewById(R.id.text_message_self);
				friendMessage = v.findViewById(R.id.text_message_friend);
			}
		}


		// Provide a suitable constructor (depends on the kind of dataset)
		public MyAdapter(List<Message> myDataset) {
			items = myDataset;
		}

		// Create new views (invoked by the layout manager)
		@Override
		public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			// create a new view
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			View v = inflater.inflate(R.layout.item_message, parent, false);
			// set the view's size, margins, paddings and layout parameters
			ViewHolder vh = new ViewHolder(v);
			return vh;
		}

		// Replace the contents of a view (invoked by the layout manager)
		@Override
		public void onBindViewHolder(ViewHolder holder, final int position) {
			// - get element from your dataset at this position
			// - replace the contents of the view with that element
			final Message message = items.get(position);

			if (!message.getFrom_uid().equals(UserProfile.getInstance().getCurrentUser().getUid())) {
				// message from self
				holder.iconImage.setVisibility(View.GONE);
				holder.selfMessage.setText(message.getMessage());
				holder.friendMessage.setVisibility(View.GONE);
				holder.selfMessage.setVisibility(View.VISIBLE);
			}
			else {
				// message from others
				holder.iconImage.setVisibility(View.VISIBLE);
				holder.friendMessage.setVisibility(View.VISIBLE);
				holder.friendMessage.setText(message.getMessage());
				holder.selfMessage.setVisibility(View.GONE);
			}
		}

		// Return the size of your dataset (invoked by the layout manager)
		@Override
		public int getItemCount() {
			return items.size();
		}

		public void add(int position, Message item) {
			items.add(position, item);
			notifyItemInserted(position);
			layoutManager.scrollToPosition(position);
		}

		public void remove(int position) {
			items.remove(position);
			notifyItemRemoved(position);
		}
	}
    
    
    private void sendMessage() {
		// ui dest_EID uri(start with secon:// and can include *)
		String text = inputEditText.getText().toString();

	    if (!isMessageValid(text)) {
		    Toast.makeText(getActivity(), getString(R.string.empty_message), Toast.LENGTH_SHORT).show();
		    return;
	    }

		inputEditText.setText("");

	    final Message message = new Message(UserProfile.getInstance().getCurrentUser().getUid(), text);

	    MyFirestore.getInstance().addMessage(UserProfile.getInstance().getCurrentUser(), friend, message, new MyFirestore.OnAccessDatabase<Message>() {
		    @Override
		    public void onComplete(Message data) {
			    if (data != null) {
				    mAdapter.add(mAdapter.getItemCount(), message);
			    }
		    }
	    });
    }

    private boolean isMessageValid(String message) {
	    return !message.isEmpty();
    }
    
}


