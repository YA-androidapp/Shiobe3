package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.ya.shiobeforandroid3.util.TlViewLayoutUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import twitter4j.Status;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.ListView;

public final class StatusTl extends Activity {
	private ListAdapter adapter;

	private String userName = "";
	private long statusId = -1l;

	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT, MP = LinearLayout.LayoutParams.MATCH_PARENT;

	private String crpKey = "";
	private Handler handler = new Handler();

	private SharedPreferences pref_app;

	private ListView listView;

	private final List<Status> statuses = new ArrayList<Status>(200);

	@Override
	public final void onCreate(final Bundle bundle) {
		super.onCreate(bundle);
		final Intent intent1 = getIntent();

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
		} catch (NameNotFoundException e) {
		}

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

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

		// リストビューの生成
		final DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		final float dpi = metrics.density; // DPIの取得
		final boolean pref_enable_singleline = pref_app.getBoolean("pref_enable_singleline", false);
		final int pref_header_height = ListAdapter.getPrefInt(this, "pref_header_height", "-1");
		float pref_tl_fontsize = Float.parseFloat(pref_app.getString("pref_tl_fontsize", "14"));
		final int pref_tl_iconsize1 = (int) ( pref_tl_fontsize * dpi * ( ( pref_enable_singleline ) ? 2.0f : ( 4.0f * Float.parseFloat(pref_app.getString("pref_tl_iconsize", "1")) ) ) ); // pref_enable_singleline pref_tl_fontsize

		final String pref_header_bgcolor = pref_app.getString("pref_header_bgcolor", "#000000");
		final TlViewLayoutUtil tlViewLayoutUtil = new TlViewLayoutUtil(pref_enable_singleline, pref_header_height, pref_tl_fontsize, pref_tl_iconsize1, pref_header_bgcolor, "", "");
		listView = new ListView(this);
		listView.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
		final boolean hide_statustl_header = pref_app.getBoolean("hide_statustl_header", false);
		if (hide_statustl_header == false) {
			final LinearLayout linearLayout1 = tlViewLayoutUtil.getTlViewLayout1(this, false);
			listView.addHeaderView(linearLayout1, null, false);
		}
		final String pref_listview_bgcolor = pref_app.getString("pref_listview_bgcolor", "#000000");
		if (pref_listview_bgcolor.equals("") == false) {
			try {
				listView.setBackgroundColor(Color.parseColor(pref_listview_bgcolor));
			} catch (final IllegalArgumentException e) {
			}
		}
		setContentView(listView);
		// リストビューの生成

		adapter = new ListAdapter(this, crpKey, listView, null);
		listView.setAdapter(adapter);

		final boolean pref_showIcon = pref_app.getBoolean("pref_showIcon", false);
		final boolean pref_showIconWear = pref_app.getBoolean("pref_showIconWear", false);
		if (pref_showIcon == true) {
			adapter.notificationShowIcon(pref_showIconWear);
		}

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		// ステータスの更新
		String intent_userName = "";
		String intent_statusId = "";
		try {
			intent_userName = intent1.getStringExtra("userName");
			intent_statusId = intent1.getStringExtra("statusId");
			if (intent_statusId.equals("") == false) {
				userName = intent_userName;
				try {
					statusId = Long.parseLong(intent_statusId);
				} catch (final Exception e) {
				}
				if (hide_statustl_header == false) {
					adapter.setUserInfo(userName);
				}
				updateStatuses(userName, statusId);
				return;
			}
		} catch (final Exception e) {
		}
		try {
			final Uri uri = intent1.getData();
			final String uri2 = uri.toString();
			if (uri != null) {
				if (( uri2.startsWith("http://twitter.com/") ) || ( uri2.startsWith("https://twitter.com/") )) {
					userName = ( ( ( uri2.replace("http://twitter.com/", "") ).replace("https://twitter.com/", "") ).split("/") )[0];
					final String[] statusIdArray = uri2.split("/");
					try {
						statusId = Long.parseLong(statusIdArray[statusIdArray.length - 1]);
					} catch (final Exception e) {
					}
					if (hide_statustl_header == false) {
						adapter.setUserInfo(userName);
					}
					updateStatuses(userName, statusId);
					return;
				}
			}
		} catch (final Exception e) {
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	protected final Dialog onCreateDialog(final int id) {
		final Dialog dialog = adapter.createDialog(id);

		if (dialog != null) {
			return dialog;
		} else {
			return super.onCreateDialog(id);
		}
	}

	@Override
	protected final void onPause() {
		try {
			dismissDialog(R.string.loading);
		} catch (final IllegalArgumentException e) {
		}
		super.onPause();
	}

	@Override
	protected final void onResume() {
		super.onResume();
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
	}

	// ステータスの更新
	private final void updateStatuses(final String userName, final long statusId) {
		if (!isFinishing()) {
			try {
				dismissDialog(R.string.loading);
			} catch (final Exception e) {
			}
			showDialog(R.string.loading);
		}

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		final int index = adapter.checkIndexFromScreenname(userName);

		WriteLog.write(this, "userName: " + userName + " statusId: " + Long.toString(statusId) + " index: " + Integer.toString(index));

		// ステータスの更新
		new Thread(new Runnable() {
			@Override
			public final void run() {
				try {
					// ステータスの読み込み
					Status status = adapter.getTwitter(index, false).showStatus(statusId);

					while (status != null) {
						statuses.add(status);
						if (( status.getInReplyToStatusId() ) > 0) {
							try {
								status = adapter.getTwitter(index, false).showStatus(status.getInReplyToStatusId());
							} catch (final TwitterException e) {
								status = null;
								WriteLog.write(StatusTl.this, e);
								adapter.toast(getString(R.string.cannot_access_twitter));
							} catch (final Exception e) {
								status = null;
								WriteLog.write(StatusTl.this, e);
								adapter.toast(getString(R.string.exception));
							}
						} else {
							break;
						}
					}

					if (statuses.isEmpty() == false) {
						adapter.setTweets(statuses, 0);
						adapter.preloadMaps(statuses);
					}
					handler.post(new Runnable() {
						@Override
						public final void run() {
							try {
								adapter.notifyDataSetChanged();
							} catch (final Exception e) {
								WriteLog.write(StatusTl.this, e);
							}
						}
					});
				} catch (final TwitterException e) {
					WriteLog.write(StatusTl.this, e);
				}
				handler.post(new Runnable() {
					@Override
					public final void run() {
						try {
							dismissDialog(R.string.loading);
						} catch (final IllegalArgumentException e) {
						}
					}
				});
			}
		}).start();
	}
}
