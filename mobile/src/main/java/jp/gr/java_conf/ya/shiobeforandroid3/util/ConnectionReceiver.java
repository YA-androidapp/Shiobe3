package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class ConnectionReceiver extends BroadcastReceiver {
	private Observer mObserver;

	public ConnectionReceiver(final Observer observer) {
		mObserver = observer;
	}

	@Override
	public final void onReceive(final Context context, final Intent intent) {
		final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null) {
			mObserver.onDisconnect();
		} else {
			mObserver.onConnect();
		}
	}

	public interface Observer {
		void onConnect();

		void onDisconnect();
	}
}
