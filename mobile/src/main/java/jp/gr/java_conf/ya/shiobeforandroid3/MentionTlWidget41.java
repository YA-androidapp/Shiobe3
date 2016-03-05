package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import jp.gr.java_conf.ya.shiobeforandroid3.util.MentionTlWidgetMethods;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.RemoteViews;

public final class MentionTlWidget41 extends AppWidgetProvider {
	public static final class WidgetService extends Service {
		private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
			@Override
			public final void onReceive(final Context context, final Intent in) {
				final String ac = in.getAction();
				if (( ac.equals(Intent.ACTION_BATTERY_CHANGED) ) || ( ac.equals(Intent.ACTION_POWER_CONNECTED) ) || ( ac.equals(Intent.ACTION_POWER_DISCONNECTED) )
						|| ( ac.equals(WifiManager.WIFI_STATE_CHANGED_ACTION) ) || ( ac.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) )
						|| ( ac.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION) ) || ( ac.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) )) {
					btnClicked();
				}
			}
		};

		private final void btnClicked() {
			final Context context = this;

			if (updating) {
				//				WriteLog.write(context, "btnClicked() updating");
			} else {
				updating = true;

				//				WriteLog.write(context, "btnClicked() !updating");

				final ComponentName componentName = new ComponentName(getPackageName(), MentionTlWidget41.class.getName());

				new Thread(new Runnable() {
					public final void run() {
						//						WriteLog.write(context, "btnClicked() run()");

						final String result = MentionTlWidgetMethods.updateStatuses(context, componentName, MentionTlWidget41.class);
						if (result.equals("")) {
							final SharedPreferences pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
							MentionTlWidgetMethods.updateTextview(context, componentName, Html.fromHtml(pref_twtr.getString("pref_mentiontlwidget_preresult", "")));
						} else {
							MentionTlWidgetMethods.updateTextview(context, componentName, Html.fromHtml(result));

							final SharedPreferences pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
							final SharedPreferences.Editor editor = pref_twtr.edit();
							editor.putString("pref_mentiontlwidget_preresult", result);
							editor.commit();
						}

						updating = false;
					}
				}).start();
			}
		}

		@Override
		public final IBinder onBind(final Intent in) {
			return null;
		}

		@Override
		public final int onStartCommand(final Intent intent, final int flags, final int startId) {
			super.onStartCommand(intent, flags, startId);

			final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.mentiontlwidget);
			final Intent newintent = new Intent();
			newintent.setAction(ACTION_BTNCLICK);
			final PendingIntent pending = PendingIntent.getService(this, 0, newintent, 0);
			remoteViews.setOnClickPendingIntent(R.id.TextView, pending);

			if (startup) {
				btnClicked();
				startup = false;
			} else if (intent != null) {
				if (( ACTION_BTNCLICK.equals(intent.getAction()) ) || ( ( Intent.ACTION_BATTERY_CHANGED ).equals(intent.getAction()) )
						|| ( ( WifiManager.NETWORK_STATE_CHANGED_ACTION ).equals(intent.getAction()) ) || ( ( WifiManager.WIFI_STATE_CHANGED_ACTION ).equals(intent.getAction()) )) {
					btnClicked();
				}
			}

			final ComponentName componentName = new ComponentName(getPackageName(), MentionTlWidget41.class.getName());
			final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
			appWidgetManager.updateAppWidget(componentName, remoteViews);

			final SharedPreferences pref_app = PreferenceManager.getDefaultSharedPreferences(this);
			final boolean pref_mentiontlwidget_action_battery_changed = pref_app.getBoolean("pref_mentiontlwidget_action_battery_changed", false);
			final boolean pref_mentiontlwidget_action_wifistate_changed = pref_app.getBoolean("pref_mentiontlwidget_action_wifistate_changed", false);

			final IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);

			if (pref_mentiontlwidget_action_battery_changed) {
				filter.addAction(Intent.ACTION_BATTERY_CHANGED);
				//				filter.addAction(Intent.ACTION_POWER_CONNECTED);
				//				filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
			}

			if (pref_mentiontlwidget_action_wifistate_changed) {
				filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
				filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
				//				filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
				//				filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
			}

			registerReceiver(batteryReceiver, filter);

			return START_STICKY_COMPATIBILITY;
		}
	}

	private static final String ACTION_BTNCLICK = "jp.gr.java_conf.ya.shiobeforandroid3.mentiontlwidget41.ACTION_BTNCLICK";
	private static boolean startup = true;

	private static boolean updating = false;

	@Override
	public final void onUpdate(final Context context, final AppWidgetManager awm, final int[] awi) {
		final Intent in = new Intent(context, jp.gr.java_conf.ya.shiobeforandroid3.MentionTlWidget41.WidgetService.class);
		context.startService(in);
	}
}
