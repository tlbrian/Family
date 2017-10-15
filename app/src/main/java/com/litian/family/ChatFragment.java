/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family;

import java.io.ObjectOutputStream;
import java.net.Socket;

import com.litian.family.db.ChatContract.ChatEntry;
import com.litian.family.network.ReceiverDaemon;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ChatFragment extends Fragment {
	
	private TextView messages_textView;
	private ImageButton send_button;
	private EditText input_editText;
	
	private String chatIP;
	private String chatName;
	
	private String inputText;
	private String chatText;
	
	private static final int PROGRESS_DIALOG_STOP = 1;
	public  static final int TEXT_RECEIVED = 2;
	
	private static ProgressDialog mProgressDialog;
	
	// this handler is used to control the progress bar and receive messages
	public  Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case PROGRESS_DIALOG_STOP:
				mProgressDialog.dismiss();
				if (chatText==null)
					chatText = "Me: " + inputText;
				else 
					chatText = chatText + "\n" + "Me: " + inputText;
				messages_textView.setText(chatText);
				break;
			case TEXT_RECEIVED:
				if (chatText==null) {
					chatText = chatName + ": " + msg.obj;
				}
				else {
					chatText = chatText + "\n" + chatName + ": " + msg.obj;
				}
				messages_textView.setText(chatText);
				break;
			}
		}
	};
	
	
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
        return inflater.inflate(R.layout.chat_layout, container, false);
    }
    
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

    	//get IP of the other side
    	Cursor c = getActivity().getContentResolver().query(
    			ChatEntry.CONTENT_URI,
    			new String[]{ChatEntry.COLUMN_NAME, ChatEntry.COLUMN_IP}, ChatEntry._ID + " =?", new String[]{""+getShownIndex()}, null);
    	c.moveToFirst();
    	chatIP = c.getString(c.getColumnIndexOrThrow(ChatEntry.COLUMN_IP));
    	chatName = c.getString(c.getColumnIndexOrThrow(ChatEntry.COLUMN_NAME));

    	ReceiverDaemon.getInstance(mHandler);
    	
    	messages_textView = getActivity().findViewById(R.id.textView_messages);
    	
    	input_editText = getActivity().findViewById(R.id.editText_input);
		input_editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(input_editText.getWindowToken(), 0);
				}
			}
			
		});
		input_editText.setOnEditorActionListener(new OnEditorActionListener() {
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
    	
    	send_button = getActivity().findViewById(R.id.imageButton_send);
		// listen the action of button clicking
		send_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});

    }

    

    public int getShownIndex() {
        return getArguments().getInt("index");
    }
    
    
    private void sendMessage() {
		// ui dest_EID uri(start with secon:// and can include *)
		inputText = input_editText.getText().toString();

		if (inputText.equals("")) {
			CharSequence text = "Blank messages can't be sent!";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(getActivity(), text, duration).show();
			return;
		}				
		input_editText.setText("");
		
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage("Sending the message");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendText(inputText);
				Message msg = new Message();
				msg.what = PROGRESS_DIALOG_STOP;
				mHandler.sendMessage(msg);
			}
		}).start();
    }
    
    
    
	// send message to the person you are chatting with
	private boolean sendText(String text) {
		
		ObjectOutputStream oos = null;
		try {
			// open a socket connection
			Socket socket = new Socket(chatIP, 4000);

			// open I/O streams for objects
			oos = new ObjectOutputStream(socket.getOutputStream());
			
			oos.writeObject(text);
			//Log.d("send","successful");
			oos.flush();
			oos.close();
			socket.close();
			
		} catch (Exception exp) {
			exp.printStackTrace();
			System.err.println(exp);
		}
		return true;
	}
    
}


