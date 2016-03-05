package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import android.app.ActivityGroup;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

@SuppressWarnings("deprecation")
public final class TwinActivity extends ActivityGroup {
	private boolean isStartup = true;
	private ListAdapter adapter;
	private SharedPreferences pref_app;
	private String crpKey = "";

	protected final void initGroups() {
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		final int pref_twin_width = ListAdapter.getPrefInt(this, "pref_twin_width", "100");

		final LinearLayout layout = (LinearLayout) findViewById(R.id.activity_layout);
		final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pref_twin_width, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1.0f;

		final String[] urls = pref_app.getString("pref_twin_uri", "").split(",");
		int i = 0;
		for (final String url : urls) {
			final Intent intent = adapter.uriStringToIntent(url);

			try {
				final LocalActivityManager localActivityManager = getLocalActivityManager();
				final Window window = localActivityManager.startActivity(Integer.toString(i), intent);
				final View view = window.getDecorView();
				layout.addView(view, layoutParams);
			} catch (final Exception e) {
				WriteLog.write(this, e);
			}

			i++;
		}
	}

	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}

		crpKey = getString(R.string.app_name);
		final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		crpKey += telephonyManager.getDeviceId();
		crpKey += telephonyManager.getSimSerialNumber();
		try {
			final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
			crpKey += Long.toString(packageInfo.firstInstallTime);
		} catch (final NameNotFoundException e) {
		}
		adapter = new ListAdapter(this, crpKey, null, null);

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		final boolean pref_enable_ringtone_onstart = pref_app.getBoolean("pref_enable_ringtone_onstart", true);
		final String pref_ringtone_onstart_twinactivity = pref_app.getString("pref_ringtone_onstart_twinactivity", "");
		if (pref_enable_ringtone_onstart && ( pref_ringtone_onstart_twinactivity != null ) && ( pref_ringtone_onstart_twinactivity.equals("") == false )) {
			final MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(pref_ringtone_onstart_twinactivity));
			mediaPlayer.setLooping(false);
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
		}

		final int pre_pref_screen_orientation_timeline = ListAdapter.getPrefInt(this, "pref_screen_orientation_timeline", "0"); // 避難
		final int pref_screen_orientation_twinactivity = ListAdapter.getPrefInt(this, "pref_screen_orientation_twinactivity", "0");
		SharedPreferences.Editor editor = pref_app.edit();
		editor.putString("pref_screen_orientation_timeline", Integer.toString(pref_screen_orientation_twinactivity));
		editor.commit();

		setContentView(R.layout.twin);

		if (isStartup) {
			initGroups();
			isStartup = false;
		}

		editor = pref_app.edit();
		editor.putString("pref_screen_orientation_timeline", Integer.toString(pre_pref_screen_orientation_timeline));
		editor.commit();

		adapter.notifyDataSetChanged();
	}

	protected final Dialog onCreateDialog(final int id) {
		adapter = new ListAdapter(this, null, null, null);
		final Dialog dialog = adapter.createDialog(id);

		if (dialog != null) {
			return dialog;
		} else {
			return super.onCreateDialog(id);
		}

	}

	@Override
	public final void onDestroy() {
		isStartup = true;

		super.onDestroy();
	}

	@Override
	protected final void onResume() {
		super.onResume();

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
	}
}