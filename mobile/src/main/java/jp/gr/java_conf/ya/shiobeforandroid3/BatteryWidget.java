package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.Calendar;

import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public final class BatteryWidget extends AppWidgetProvider {

	public static final class WidgetService extends Service {
		@Override
		public IBinder onBind(final Intent in) {
			return null;
		}

		@Override
		public final void onStart(final Intent in, final int si) {
			final IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_BATTERY_CHANGED);
			registerReceiver(batteryReceiver, filter);
		}
	}

	private static final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		final int[] IMAGE = { R.drawable.bat0, R.drawable.bat10, R.drawable.bat20, R.drawable.bat30, R.drawable.bat40, R.drawable.bat50, R.drawable.bat60, R.drawable.bat70, R.drawable.bat80,
				R.drawable.bat90, R.drawable.bat100 };
		int scale = 100;
		int level = 0;

		private SharedPreferences pref_app;
		private String preTweetStr = "";

		@Override
		public final void onReceive(final Context context, final Intent in) {
			final String ac = in.getAction();
			if (ac.equals(Intent.ACTION_BATTERY_CHANGED)) {

				final AppWidgetManager awm = AppWidgetManager.getInstance(context);
				final ComponentName cn = new ComponentName(context, BatteryWidget.class);
				final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.batterywidget);

				level = in.getIntExtra("level", 0);
				scale = in.getIntExtra("scale", 0);

				String pluggedStr = "";
				final int plugged = in.getIntExtra("plugged", 0);
				if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
					pluggedStr = "AC";
				} else if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
					pluggedStr = "DC";
				}

				rv.setImageViewResource(R.id.ImageView, IMAGE[(int) ( level * 100 / scale ) / 10]);

				if ((int) ( level * 100 / scale ) < 100) {
					rv.setTextViewText(R.id.TextView, " " + (int) ( level * 100 / scale ) + "% " + pluggedStr);
				} else {
					rv.setTextViewText(R.id.TextView, " " + (int) ( level * 100 / scale ) + "% " + pluggedStr);
				}

				pref_app = PreferenceManager.getDefaultSharedPreferences(context);
				final boolean pref_enable_batterywidget_tweet = pref_app.getBoolean("pref_enable_batterywidget_tweet", false);
				final String pref_batterywidget_tweet_index = pref_app.getString("pref_batterywidget_tweet_index", "0");

				if (pref_enable_batterywidget_tweet) {
					WriteLog.write(context, "pref_enable_batterywidget_tweet: " + Boolean.toString(pref_enable_batterywidget_tweet));

					int pref_batterywidget_tweet_interval = ListAdapter.getPrefInt(context, "pref_batterywidget_tweet_interval", "10");
					final int pref_batterywidget_tweet_upper = ListAdapter.getPrefInt(context, "pref_batterywidget_tweet_upper", "100");
					final int pref_batterywidget_tweet_lower = ListAdapter.getPrefInt(context, "pref_batterywidget_tweet_lower", "0");
					final String batterywidget_tweet_prefix = pref_app.getString("batterywidget_tweet_prefix", "");

					if (pref_batterywidget_tweet_interval == 0) {
						pref_batterywidget_tweet_interval = 1;
					}

					final int percent = level * 100 / scale;

					if (( pref_batterywidget_tweet_lower < percent ) && ( percent < pref_batterywidget_tweet_upper )) {

						if (percent % pref_batterywidget_tweet_interval == 0) {

							final String tweetStr = batterywidget_tweet_prefix + percent + "% " + pluggedStr;
							if (tweetStr.equals(preTweetStr) == false) {
								preTweetStr = tweetStr;

								final Calendar cal = Calendar.getInstance(ListAdapter.LOCALE);
								WriteLog.write(context, "cal: " + cal.toString());

								final Intent intent = new Intent(context, jp.gr.java_conf.ya.shiobeforandroid3.AutoTweet.class);
								intent.setAction(Intent.ACTION_MAIN);
								intent.setData(Uri.parse("http://shiobe/?" + String.valueOf(cal.getTimeInMillis())));
								intent.putExtra("mode", "t");
								intent.putExtra("str1", "");
								intent.putExtra("str2", tweetStr);
								intent.putExtra("str3", "");
								intent.putExtra("str4", "");
								intent.putExtra("str5", "");
								intent.putExtra("inReplyToStatusId", "");
								intent.putExtra("tweetImagePathString", "");
								intent.putExtra("index", pref_batterywidget_tweet_index);
								intent.putExtra("scheduledDateGetTimeInMillisString", "");

								context.sendBroadcast(intent);
							}
						}
					}
				}

				awm.updateAppWidget(cn, rv);
			}
		}
	};

	@Override
	public final void onUpdate(final Context context, final AppWidgetManager awm, final int[] awi) {
		final Intent in = new Intent(context, WidgetService.class);
		context.startService(in);
	}
}
