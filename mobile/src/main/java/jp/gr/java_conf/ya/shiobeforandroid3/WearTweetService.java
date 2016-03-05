package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public final class WearTweetService extends WearableListenerService {
	static final int NOTIFY_RUNNING = 0;
	static final int NOTIFY_DONE_TWEET = 2;
	static final int NOTIFY_TWITTER_EXCEPTION = 4;
	static final int NOTIFY_DONE_ACTION = 8;

	private static int rc = 0;

	private final void notification(final int notificationId, final int icon, final String title, final String message, final String summary, final boolean autoCancel, final boolean onGoing,
			final int ledColor, final boolean vibrate, final PendingIntent pendingIntent, final boolean useRemoteInput) {

		final SharedPreferences pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		final String pref_develop_updatetweet_androidwear_choice = pref_app.getString("pref_develop_updatetweet_androidwear_choice", "");
		final String[] pref_develop_updatetweet_androidwear_choice_array = pref_develop_updatetweet_androidwear_choice.split(",");
		final Intent intent1 = new Intent(this, WearTweet.class).putExtra("mode", "t");
		final RemoteInput remoteInput = new RemoteInput.Builder(ListAdapter.EXTRA_VOICE_REPLY).setLabel(getString(R.string.develop_updatetweet_androidwear_choice_label)) //
		.setChoices(pref_develop_updatetweet_androidwear_choice_array) //
		.build();
		final PendingIntent pendingIntent1 = PendingIntent.getActivity(this, rc++, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		final Action action =
				new NotificationCompat.Action.Builder(R.drawable.bird_blue_48, this.getString(R.string.develop_updatetweet_androidwear_choice_label), pendingIntent1).addRemoteInput(remoteInput).build();

		final NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this).setAutoCancel(autoCancel).setOngoing(onGoing).setSmallIcon(icon).setContentTitle(title).setContentText(message).setContentIntent(pendingIntent).setStyle(new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title));

		if (ledColor != Color.TRANSPARENT) {
			notificationBuilder.setLights(ledColor, 500, 500);
		}
		if (vibrate) {
			notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
		}
		if (useRemoteInput) {
			notificationBuilder.extend(new NotificationCompat.WearableExtender().addAction(action));
		}

		final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}

	private final void notificationShowIcon() {
		final Intent intent = new Intent(this, UpdateTweet.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notification(NOTIFY_RUNNING, R.drawable.ic_launcher, getString(R.string.app_name_short), getString(R.string.app_name) + getString(R.string.pendingintent_sammary_open_updatetweet), "", false, false, Color.TRANSPARENT, false, pendingIntent, true);
	}

	@Override
	public final void onMessageReceived(final MessageEvent messageEvent) {
		final String path = messageEvent.getPath();
		final String data = new String(messageEvent.getData());

		WriteLog.write(this, "onMessageReceived");
		WriteLog.write(this, path);
		WriteLog.write(this, data);
		if (( path.equals("/updateStatus") ) && ( data.equals("") == false )) {
			wearTweet(data, "t");
		} else if (( path.equals("/updateStatusKana") ) && ( data.equals("") == false )) {
			wearTweet(data, "k");
			// } else if (path.equals("/notification")) {
			// 	notificationShowIcon();
		} else {
			notificationShowIcon();
		}
	}

	private final void wearTweet(final String str2, final String mode) {
		final Intent intent = new Intent(this, WearTweet.class);
		intent.putExtra("mode", mode); // t:tweet; k:kana-tweet
		intent.putExtra("str2", str2);
		intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}