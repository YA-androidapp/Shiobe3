package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public final class TabsActivity extends TabActivity {
	private SharedPreferences pref_app;
	private SharedPreferences pref_twtr;

	private final Intent getTl(final int index, final String uriString) {
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.Tl");
		intent.setData(Uri.parse(uriString));
		intent.putExtra("index", index);
		return intent;
	}

	private final Intent getTlFavorite(final int index) {
		return getTl(index, ListAdapter.TWITTER_BASE_URI + "favorites");
	}

	private final Intent getTlHome(final int index) {
		return getTl(index, ListAdapter.TWITTER_BASE_URI);
	}

	private final Intent getTlMention(final int index) {
		return getTl(index, ListAdapter.TWITTER_BASE_URI + "mentions");
	}

	protected final void initTabs() {
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);

		final Resources res = getResources();
		final TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		final int user_index_size = ListAdapter.getPrefInt(this, "pref_user_index_size", Integer.toString(ListAdapter.default_user_index_size));

		intent = new Intent().setClass(this, ShiobeForAndroidActivity.class);
		spec = tabHost.newTabSpec("Start").setIndicator("Start", res.getDrawable(R.drawable.ic_launcher)).setContent(intent);
		tabHost.addTab(spec);

		for (int i = 0; i < user_index_size; i++) {
			final String screenName = pref_twtr.getString("screen_name_" + i, "");
			if (screenName.equals("") == false) {
				spec = tabHost.newTabSpec("@" + screenName + "#home").setIndicator("@" + screenName + "#home", res.getDrawable(R.drawable.ic_launcher)).setContent(getTlHome(i));
				tabHost.addTab(spec);

				spec = tabHost.newTabSpec("@" + screenName + "#mention").setIndicator("@" + screenName + "#mention", res.getDrawable(R.drawable.ic_launcher)).setContent(getTlMention(i));
				tabHost.addTab(spec);

				spec = tabHost.newTabSpec("@" + screenName + "#Fav").setIndicator("@" + screenName + "#Fav", res.getDrawable(R.drawable.ic_launcher)).setContent(getTlFavorite(i));
				tabHost.addTab(spec);
			}
		}

		tabHost.setCurrentTab(0);
	}

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		final boolean pref_enable_ringtone_onstart = pref_app.getBoolean("pref_enable_ringtone_onstart", true);
		final String pref_ringtone_onstart_tabsactivity = pref_app.getString("pref_ringtone_onstart_tabsactivity", "");
		if (pref_enable_ringtone_onstart && ( pref_ringtone_onstart_tabsactivity != null ) && ( pref_ringtone_onstart_tabsactivity.equals("") == false )) {
			final MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(pref_ringtone_onstart_tabsactivity));
			mediaPlayer.setLooping(false);
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
		}

		final int pref_screen_orientation_timeline = ListAdapter.getPrefInt(this, "pref_screen_orientation_timeline", "0");
		switch (pref_screen_orientation_timeline) {
		default:
			break;
		case 0:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case 1:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case 2:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			break;
		case 3:

			switch (getResources().getConfiguration().orientation) {
			case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case android.content.res.Configuration.ORIENTATION_PORTRAIT:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			default:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}

			break;
		}

		setContentView(R.layout.tabs);
		initTabs();
	}

	@Override
	protected final void onResume() {
		super.onResume();

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
	}
}