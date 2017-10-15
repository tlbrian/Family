/*==============================================================================
 Copyright (c) 2013-2014 Li Tian
 All Rights Reserved.
 ==============================================================================*/

package com.litian.family.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.litian.family.ChatFragment;

import android.os.Handler;
import android.os.Message;

public class ReceiverDaemon extends Thread {
	private static ReceiverDaemon instance;
	
	private Handler mHandler;
	

	public static ReceiverDaemon getInstance(Handler handler) {
		if (instance == null) {
			instance = new ReceiverDaemon(handler);
		}
		else {
			instance.setParams(handler);
		}
		return instance;
	}
	
	public void setParams(Handler handler) {
		mHandler = handler;
	}

	private ServerSocket arrayServer;

	public ReceiverDaemon(Handler handler) {
		// JGroupsManager.getInstance().start();
		mHandler = handler;
		
		try {
			arrayServer = new ServerSocket(4000);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server listening on port 4000.");
		start();
	}

	@Override
	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		while (true) {
			try {
				Socket client = arrayServer.accept();
				//Log.d("receive","connected");
				new Connect(client, mHandler).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class Connect extends Thread {
	private Socket client = null;
	private ObjectInputStream ois = null;
	
	private Handler mHandler;
	
	public Connect(Socket clientSocket, Handler handler) {
		mHandler = handler;
		client = clientSocket;
		try {
			ois = new ObjectInputStream(client.getInputStream());
		} catch (Exception e1) {
			try {
				client.close();
			} catch (Exception e) {
				System.out.println(e);
				// Daemon.logger.error(e.getMessage());
			}
			return;
		}
	}

	@Override
	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		try {
			if (run0()) {
				ois.close();
				client.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean run0() {
		try {
			String recText = (String) ois.readObject();
			Message msg = new Message();
			msg.what = ChatFragment.TEXT_RECEIVED;
			msg.obj = recText;
			mHandler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
