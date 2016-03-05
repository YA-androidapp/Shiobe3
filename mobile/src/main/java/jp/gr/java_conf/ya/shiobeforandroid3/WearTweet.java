package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import jp.gr.java_conf.ya.shiobeforandroid3.util.CheckNetworkUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.Kana;
import jp.gr.java_conf.ya.shiobeforandroid3.util.MyCrypt;
import jp.gr.java_conf.ya.shiobeforandroid3.util.StringUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

public final class WearTweet extends AppCompatActivity {
	private static int index_pre = -1;
	// private static final int NOTIFY_RUNNING = 0;
	private static final int NOTIFY_DONE_TWEET = 2;
	private static final int NOTIFY_TWITTER_EXCEPTION = 4;
	private static final int NOTIFY_DONE_ACTION = 8;

	private SharedPreferences pref_app, pref_twtr;

	private Twitter twitter = null;

	private final boolean currentThreadIsUiThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	private final void d(final Twitter twitter, final String screenName) {
		Status deljustbeforeTweet = null;
		try {
			deljustbeforeTweet = twitter.getUserTimeline(screenName, new Paging(1, 1)).get(0);
		} catch (final TwitterException e) {
			WriteLog.write(WearTweet.this, e);
		} catch (final Exception e) {
			WriteLog.write(WearTweet.this, e);
		}
		if (deljustbeforeTweet == null) {
			try {
				String[] deljustbeforeScreennames = { screenName };
				User deljustbeforeUser = twitter.lookupUsers(deljustbeforeScreennames).get(0);
				deljustbeforeTweet = deljustbeforeUser.getStatus();
			} catch (final TwitterException e) {
				WriteLog.write(WearTweet.this, e);
			} catch (final Exception e) {
				WriteLog.write(WearTweet.this, e);
			}
		}
		if (deljustbeforeTweet == null) {
			WriteLog.write(WearTweet.this, "null");
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "Exception", "NULL", "[@" + screenName + "]");
		} else {
			try {
				final String text = deljustbeforeTweet.getText();
				twitter.destroyStatus(deljustbeforeTweet.getId());
				notification(NOTIFY_DONE_ACTION, R.drawable.ic_launcher, getString(R.string.done_del), text, "[@" + screenName + "]");
			} catch (final TwitterException e) {
				WriteLog.write(WearTweet.this, e);
				notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "TwitterException", e.getMessage(), "[@" + screenName + "]");
			} catch (final Exception e) {
				WriteLog.write(WearTweet.this, e);
				notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "Exception", e.getMessage(), "[@" + screenName + "]");
			}
		}
	}

	private final void f(final Twitter twitter, final String screenName, final String str2, final long inReplyToStatusId) {
		try {
			twitter.createFavorite(inReplyToStatusId);
			notification(NOTIFY_DONE_ACTION, R.drawable.ic_launcher, getString(R.string.done_fav_create), str2, "[@" + screenName + "]");
		} catch (final TwitterException e) {
			WriteLog.write(WearTweet.this, e);
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "TwitterException", e.getMessage(), "[@" + screenName + "]");
		} catch (final Exception e) {
			WriteLog.write(WearTweet.this, e);
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "Exception", e.getMessage(), "[@" + screenName + "]");
		}
	}

	private final Configuration getConf(int index) {
		final SharedPreferences pref_twtr = getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0
		final int pref_timeout_t4j_connection = setTimeout(true);
		final int pref_timeout_t4j_read = setTimeout(false);

		String crpKey = getString(R.string.app_name);
		final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		crpKey += telephonyManager.getDeviceId();
		crpKey += telephonyManager.getSimSerialNumber();
		try {
			final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
			crpKey += Long.toString(packageInfo.firstInstallTime);
		} catch (final NameNotFoundException e) {
		}

		String consumerKey = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("consumer_key_" + index, ""));
		String consumerSecret = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("consumer_secret_" + index, ""));
		if (consumerKey.equals("") || consumerSecret.equals("")) {
			consumerKey = getString(R.string.default_consumerKey);
			consumerSecret = getString(R.string.default_consumerSecret);
		}
		final String oauthToken = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("oauth_token_" + index, ""));
		WriteLog.write(this, "oauthToken: " + oauthToken);
		final String oauthTokenSecret = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("oauth_token_secret_" + index, ""));
		WriteLog.write(this, "oauthTokenSecret: " + oauthTokenSecret);
		final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		confbuilder.setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read).setHttpRetryCount(3).setHttpRetryIntervalSeconds(10);// .setUseSSL(true);
		return confbuilder.build();
	}

	private final Twitter getTwitter(final int index, final boolean saveIndex) {
		final SharedPreferences pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
		if (isConnected(pref_twtr.getString("status_" + index, ""))) {
			if (( twitter == null ) || ( index != index_pre )) {
				if (saveIndex) {
					final SharedPreferences.Editor editor = pref_twtr.edit();
					editor.putString("index", Integer.toString(index));
					editor.commit();
				}
				final Configuration conf = getConf(index);
				twitter = new TwitterFactory(conf).getInstance();
				index_pre = index;
			}
			return twitter;
		}
		//		toast(getString(R.string.exception));
		return null;
	}

	private final boolean isConnected(final String shiobeStatus) {
		if (( shiobeStatus != null ) && shiobeStatus.equals("available")) {
			return true;
		} else {
			return false;
		}
	}

	private final void notification(final int notificationId, final int icon, final String title, final String message, final String summary) {
		if (!( (Activity) this ).isFinishing()) {
			if (currentThreadIsUiThread()) {
				notificationPart(notificationId, icon, title, message, summary);
			} else {
				( (Activity) this ).runOnUiThread(new Runnable() {
					@Override
					public final void run() {
						notificationPart(notificationId, icon, title, message, summary);
					}
				});
			}
		}
	}

	private final void notificationPart(final int notificationId, final int icon, final String title, final String message, final String summary) {
		final Intent intent = new Intent(WearTweet.this, UpdateTweet.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(WearTweet.this, 0, intent, 0);

		final NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(WearTweet.this).setAutoCancel(true).setSmallIcon(icon).setContentTitle(title).setContentText(message).setContentIntent(pendingIntent).setStyle(new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title).setSummaryText(summary));

		final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(WearTweet.this);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}

	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}

		( new CheckNetworkUtil(this) ).autoConnect(true);

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);

		new Thread(new Runnable() {
			@Override
			public final void run() {
				try {
					final long pref_autotweet_wait = ListAdapter.getPrefInt(WearTweet.this, "pref_autotweet_wait", "0");
					WriteLog.write(WearTweet.this, "pref_autotweet_wait: " + Long.toString(pref_autotweet_wait));
					if (pref_autotweet_wait > 0) {
						try {
							Thread.sleep(pref_autotweet_wait);
						} catch (final InterruptedException e) {
							WriteLog.write(WearTweet.this, e);
						}
					}
				} catch (final NumberFormatException e) {
					WriteLog.write(WearTweet.this, e);
				}

				final Intent intent = getIntent();
				final Bundle bundle = intent.getExtras();

				CharSequence message = "";
				try {
					final Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
					message = remoteInput.getCharSequence(ListAdapter.EXTRA_VOICE_REPLY);
				} catch (final Exception e) {
				}

				String index = "0";
				try {
					if (bundle.getString("index") != null) {
						if (( index.equals("-1") == false ) && ( isConnected(pref_twtr.getString("status_" + index, "")) )) {
							index = bundle.getString("index");
						}
					}
				} catch (final Exception e) {
				}
				WriteLog.write(WearTweet.this, "index: " + index);
				final String screenName = pref_twtr.getString("screen_name_" + index, "");
				WriteLog.write(WearTweet.this, "screenName: " + screenName);

				String str1 = "";
				String str2 = "";
				String str3 = "";
				String str4 = "";
				String str5 = "";
				String mode = "";
				try {
					str1 = ( bundle.getString("str1") == null ) ? "" : bundle.getString("str1");
				} catch (final Exception e1) {
				}
				try {
					str2 = ( message.toString() ) + ( ( bundle.getString("str2") == null ) ? "" : bundle.getString("str2") );
				} catch (final Exception e1) {
					str2 = message.toString();
				}
				try {
					str3 = ( bundle.getString("str3") == null ) ? "" : bundle.getString("str3");
				} catch (final Exception e1) {
				}
				try {
					str4 = ( bundle.getString("str4") == null ) ? "" : bundle.getString("str4");
				} catch (final Exception e1) {
				}
				try {
					str5 = ( bundle.getString("str5") == null ) ? "" : bundle.getString("str5");
				} catch (final Exception e1) {
				}
				try {
					mode = ( bundle.getString("mode") == null ) ? "" : bundle.getString("mode");
				} catch (final Exception e1) {
				}
				WriteLog.write(WearTweet.this, "str1: " + str1);
				WriteLog.write(WearTweet.this, "str2: " + str2);
				WriteLog.write(WearTweet.this, "str3: " + str3);
				WriteLog.write(WearTweet.this, "str4: " + str4);
				WriteLog.write(WearTweet.this, "str5: " + str5);
				WriteLog.write(WearTweet.this, "mode: " + mode);

				long inReplyToStatusId = 0L;
				try {
					inReplyToStatusId = Long.parseLong(bundle.getString("inReplyToStatusId"));
				} catch (final Exception e1) {
				}
				WriteLog.write(WearTweet.this, "inReplyToStatusId: " + inReplyToStatusId);

				try {
					final Twitter twitter = getTwitter(Integer.parseInt(index), false);

					if (str2.equals(getString(R.string.del))) {
						d(twitter, screenName);
					} else if (str2.equals(getString(R.string.fav_create))) {
						if (inReplyToStatusId != 0L) {
							f(twitter, screenName, str2, inReplyToStatusId);
						}
					} else if (str2.equals(getString(R.string.rt_create))) {
						if (inReplyToStatusId != 0L) {
							r(twitter, screenName, str2, inReplyToStatusId);
						}
					} else if (str2.equals(getString(R.string.pak_create))) {
						p(twitter, screenName, str2, inReplyToStatusId);
					} else {
						t(twitter, screenName, str1, str2, str3, str4, str5, inReplyToStatusId, mode);
					}
				} catch (final NumberFormatException e) {
					WriteLog.write(WearTweet.this, e);
				}

				finish();
				return;
			}
		}).start();
	}

	private final void p(final Twitter twitter, final String screenName, final String str2, final long inReplyToStatusId) {
		final StatusUpdate statusUpdate = new StatusUpdate(str2);
		if (inReplyToStatusId != 0L) {
			statusUpdate.setInReplyToStatusId(inReplyToStatusId);
		}

		try {
			twitter.updateStatus(statusUpdate);
			notification(NOTIFY_DONE_ACTION, R.drawable.ic_launcher, getString(R.string.done_pak), str2, "[@" + screenName + "]");
		} catch (final TwitterException e) {
			WriteLog.write(WearTweet.this, e);
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "TwitterException", e.getMessage(), "[@" + screenName + "]");
		} catch (final Exception e) {
			WriteLog.write(WearTweet.this, e);
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "Exception", e.getMessage(), "[@" + screenName + "]");
		}
	}

	private final void r(final Twitter twitter, final String screenName, final String str2, final long inReplyToStatusId) {
		try {
			twitter.retweetStatus(inReplyToStatusId);
			notification(NOTIFY_DONE_ACTION, R.drawable.ic_launcher, getString(R.string.done_rt), str2, "[@" + screenName + "]");
		} catch (final TwitterException e) {
			WriteLog.write(WearTweet.this, e);
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "TwitterException", e.getMessage(), "[@" + screenName + "]");
		} catch (final Exception e) {
			WriteLog.write(WearTweet.this, e);
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "Exception", e.getMessage(), "[@" + screenName + "]");
		}
	}

	private final String replaceWithUserDictionary(final String str) {
		String result = str;
		try {
			final String pref_wear_replace_dictionary = pref_app.getString("pref_wear_replace_dictionary", "");
			if (pref_wear_replace_dictionary.equals("") == false) {
				final String[] dictionarys = pref_wear_replace_dictionary.split(";");
				if (dictionarys.length > 0) {
					for (final String dictionary : dictionarys) {
						try {
							final String[] dic = dictionary.split(":");
							if (dic.length == 2) {
								if (( dic[0].equals("") == false ) && ( dic[1].equals("") == false )) {
									result = result.replaceAll(dic[0], dic[1]);
								}
							}
						} catch (final Exception e) {
						}
					}
				}
			}
			return result;
		} catch (final Exception e) {
			return str;
		}
	}

	private final int setTimeout(final boolean mode) {
		if (mode) {
			return ListAdapter.getPrefInt(this, "pref_timeout_t4j_connection", "20000");
		} else {
			return ListAdapter.getPrefInt(this, "pref_timeout_t4j_read", "120000");
		}
	}

	private final void t(final Twitter twitter, final String screenName, final String str1, String str2, final String str3, final String str4, final String str5, final long inReplyToStatusId,
			final String mode) {
		WriteLog.write(WearTweet.this, "mode: t");

		// 共通ヘッダ・フッタ
		final boolean pref_enable_common_header = pref_app.getBoolean("pref_enable_common_header", false);
		final boolean pref_enable_common_footer = pref_app.getBoolean("pref_enable_common_footer", false);
		String pref_common_header = "";
		String pref_common_footer = "";
		if (pref_enable_common_header) {
			pref_common_header = pref_app.getString("pref_common_header", "");
		}
		if (pref_enable_common_footer) {
			pref_common_footer = pref_app.getString("pref_common_footer", "");
		}
		final Boolean pref_enable_common_header_wear = pref_app.getBoolean("pref_enable_common_header_wear", false);
		final Boolean pref_enable_common_footer_wear = pref_app.getBoolean("pref_enable_common_footer_wear", false);
		String pref_common_header_wear = "";
		String pref_common_footer_wear = "";
		if (pref_enable_common_header_wear) {
			pref_common_header_wear = pref_app.getString("pref_common_header_wear", "");
		}
		if (pref_enable_common_footer_wear) {
			pref_common_footer_wear = pref_app.getString("pref_common_footer_wear", "");
		}

		String header = pref_common_header + pref_common_header_wear + str1;
		String footer = str3 + pref_common_footer_wear + pref_common_footer;

		final Boolean pref_enable_wear_replace_dictionary = pref_app.getBoolean("pref_enable_wear_replace_dictionary", false);
		if (pref_enable_wear_replace_dictionary) {
			str2 = replaceWithUserDictionary(str2);
		}

		if (mode.equals("k")) {
			header = Kana.Rome2Kana(header);
			str2 = Kana.Rome2Kana(str2);
			footer = Kana.Rome2Kana(footer);
		} else {
			Boolean pref_enable_wear_rome2kana = pref_app.getBoolean("pref_enable_wear_rome2kana", false);
			if (pref_enable_wear_rome2kana) {
				str2 = Kana.Rome2Kana(str2);
			}
		}

		final String tweetstr = StringUtil.getTweetString(header, str2, footer);

		if (tweetstr.equals("")) {
			return;
		}
		String toast_message = "";

		final StatusUpdate statusUpdate = new StatusUpdate(tweetstr);
		if (inReplyToStatusId != 0L) {
			statusUpdate.setInReplyToStatusId(inReplyToStatusId);
		}

		//		if (( str4.length() > 0 ) && ( str5.length() > 0 )) {
		//			// 位置情報
		//			try {
		//				if (( Math.abs(Double.parseDouble(str4)) <= 90.0 ) && ( Math.abs(Double.parseDouble(str5)) <= 180.0 )) {
		//					WriteLog.write(WearTweet.this, "((Math.abs(Double.parseDouble(str4)) <= 90.0) && (Math.abs(Double.parseDouble(str5)) <= 180.0))");
		//
		//					Boolean pref_enable_locationinfo_exception = pref_app.getBoolean("pref_enable_locationinfo_exception", false);
		//					Double pref_locationinfo_exception_lat = Double.parseDouble(pref_app.getString("pref_locationinfo_exception_lat", ListAdapter.default_locationinfo_exception_lat));
		//					Double pref_locationinfo_exception_lng = Double.parseDouble(pref_app.getString("pref_locationinfo_exception_lng", ListAdapter.default_locationinfo_exception_lng));
		//					Double pref_locationinfo_exception_radius = Double.parseDouble(pref_app.getString("pref_locationinfo_exception_radius", ListAdapter.default_locationinfo_exception_radius));
		//
		//					Double lat1 = pref_locationinfo_exception_lat;
		//					Double lng1 = pref_locationinfo_exception_lng;
		//					Double lat2 = Double.parseDouble(str4);
		//					Double lng2 = Double.parseDouble(str5);
		//
		//					if (( !pref_enable_locationinfo_exception ) || ( pref_enable_locationinfo_exception && ( CoordsUtil.calcDistHubeny(lat1, lng1, lat2, lng2) > pref_locationinfo_exception_radius ) )) {
		//						statusUpdate.location(new GeoLocation(Double.parseDouble(str4), Double.parseDouble(str5)));
		//						WriteLog.write(WearTweet.this, "tweet() statusUpdate.location()");
		//						String latlng = str4 + "," + str5;
		//						toast_message += System.getProperty("line.separator") + getString(R.string.placeinfo) + ": " + latlng;
		//					} else {
		//						toast_message += System.getProperty("line.separator") + getString(R.string.placeinfo) + ": " + getString(R.string.locationinfo_exception);
		//					}
		//				}
		//			} catch (final NumberFormatException e) {
		//				WriteLog.write(WearTweet.this, e);
		//			} catch (final Exception e) {
		//				WriteLog.write(WearTweet.this, e);
		//			}
		//		}
		//		Status updatedstatus;
		try {
			twitter.updateStatus(statusUpdate);
			WriteLog.write(WearTweet.this, "twitter.updateStatus()");

			notification(NOTIFY_DONE_TWEET, R.drawable.ic_launcher, getString(R.string.done_tweet), tweetstr, toast_message + " [@" + screenName + "]");
		} catch (final TwitterException e) {
			WriteLog.write(WearTweet.this, e);
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "TwitterException", e.getMessage(), "[@" + screenName + "]");
		} catch (final Exception e) {
			WriteLog.write(WearTweet.this, e);
			notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "Exception", e.getMessage(), "[@" + screenName + "]");
		}
	}
}
