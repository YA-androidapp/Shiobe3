package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.gr.java_conf.ya.shiobeforandroid3.ListAdapter;
import jp.gr.java_conf.ya.shiobeforandroid3.R;
import jp.gr.java_conf.ya.shiobeforandroid3.StatusTl;
import jp.gr.java_conf.ya.shiobeforandroid3.WearTweet;
import twitter4j.Paging;
import twitter4j.Relationship;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

public final class MentionTlWidgetMethods {
	private static boolean isMobile = false;
	private static int index_pre = -1;
	private static List<Status> statuses = new ArrayList<Status>(200);
	private static MediaPlayer mediaPlayer = null;
	private static SharedPreferences pref_app, pref_twtr;
	private static String pref_mute_screenname = "", pref_mute_source = "", pref_mute_text = "";
	private static String pref_develop_mentiontlwidget_androidwear_choice = "";
	private static Twitter twitter;
	private static UrlUtil urlUtil;
	private static final String NL = System.getProperty("line.separator");
	private static final String ACTION_BTNCLICK = "jp.gr.java_conf.ya.shiobeforandroid3.mentiontlwidget41.ACTION_BTNCLICK";

	private static int rc = 0;

	private static final int checkIndexFromPrefTwtr(final Context context) {
		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		pref_twtr = context.getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0
		final int pref_user_index_offset = ListAdapter.getPrefInt(context, "pref_user_index_offset", "0");
		final int user_index_size = ListAdapter.getPrefInt(context, "pref_user_index_size", Integer.toString(ListAdapter.default_user_index_size));

		try {
			return Integer.parseInt(pref_twtr.getString("index", "0"));
		} catch (final NumberFormatException e) {
			WriteLog.write(context, e);
		} catch (final Exception e) {
			WriteLog.write(context, e);
		}

		for (int i = pref_user_index_offset; i < user_index_size; i++) {
			if (isConnected(pref_twtr.getString("status_" + i, ""))) {
				return i;
			}
		}
		return 0;
	}

	private static final boolean checkMute(final Status status) {
		if (( checkMuteSource(status) ) || ( checkMuteScreenname(status) ) || ( checkMuteText(status) )) {
			return true;
		} else {
			return false;
		}
	}

	private static final boolean checkMuteScreenname(final Status status) {
		if (pref_mute_screenname.indexOf("," + ( status.getUser().getScreenName() ) + ",") > -1) {
			return true;
		}

		if (status.getRetweetedStatus() != null) {
			try {
				if (pref_mute_screenname.indexOf("," + ( status.getRetweetedStatus().getUser().getScreenName() ) + ",") > -1) {
					return true;
				}
			} catch (final Exception e) {
			}
		}

		return false;
	}

	private static final boolean checkMuteSource(final Status status) {
		if (pref_mute_source.indexOf("," + ( status.getSource() ).replaceAll("<[^>]+?>", "") + ",") > -1) {
			return true;
		}

		return false;
	}

	private static final boolean checkMuteText(final Status status) {
		if (pref_mute_text.equals(",,") == false) {
			for (final String pref_mute_text_part : pref_mute_text.split(",")) {
				if (( pref_mute_text_part.equals("") == false ) && ( status.getText().indexOf(pref_mute_text_part) > -1 )) {
					return true;
				}
			}
		}

		return false;
	}

	private static final Configuration getConf(final Context context, final int index) {
		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		pref_twtr = context.getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0
		final int pref_timeout_t4j_connection = setTimeout(context, true);
		final int pref_timeout_t4j_read = setTimeout(context, false);

		String crpKey = context.getString(R.string.app_name);
		final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
		crpKey += telephonyManager.getDeviceId();
		crpKey += telephonyManager.getSimSerialNumber();
		try {
			final PackageInfo packageInfo = context.getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
			crpKey += Long.toString(packageInfo.firstInstallTime);
		} catch (NameNotFoundException e) {
			WriteLog.write(context, e);
		}

		String consumerKey = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("consumer_key_" + index, ""));
		String consumerSecret = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("consumer_secret_" + index, ""));
		if (consumerKey.equals("") || consumerSecret.equals("")) {
			consumerKey = context.getString(R.string.default_consumerKey);
			consumerSecret = context.getString(R.string.default_consumerSecret);
		}
		final String oauthToken = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("oauth_token_" + index, ""));
		//		WriteLog.write(context, "oauthToken: " + oauthToken);
		final String oauthTokenSecret = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("oauth_token_secret_" + index, ""));
		//		WriteLog.write(context, "oauthTokenSecret: " + oauthTokenSecret);
		final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		confbuilder.setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read).setHttpRetryCount(3).setHttpRetryIntervalSeconds(10);// .setUseSSL(true);
		return confbuilder.build();
	}

	private static final Twitter getTwitter(final Context context, final int index, final boolean saveIndex) {
		pref_twtr = context.getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0
		if (isConnected(pref_twtr.getString("status_" + index, ""))) {
			if (( twitter == null ) || ( index != index_pre )) {
				if (saveIndex) {
					final SharedPreferences.Editor editor = pref_twtr.edit();
					editor.putString("index", Integer.toString(index));
					editor.commit();
				}
				final Configuration conf = getConf(context, index);
				twitter = new TwitterFactory(conf).getInstance();
				index_pre = index;
			}
			return twitter;
		}
		//		toast(context.getString(R.string.exception));
		return null;
	}

	private static final boolean isConnected(final String shiobeStatus) {
		if (( shiobeStatus != null ) && shiobeStatus.equals("available")) {
			return true;
		} else {
			return false;
		}
	}

	private static final void notification(final Context context, final Status status, final int ledColor, final boolean vibrate) {
		if (pref_develop_mentiontlwidget_androidwear_choice.equals("")) {
			pref_app = PreferenceManager.getDefaultSharedPreferences(context);
			pref_develop_mentiontlwidget_androidwear_choice = pref_app.getString("pref_develop_mentiontlwidget_androidwear_choice", "");
		}
		final String[] pref_develop_mentiontlwidget_androidwear_choice_array = pref_develop_mentiontlwidget_androidwear_choice.split(",");

		//
		// List<NotificationCompat.Action> actions = new ArrayList<NotificationCompat.Action>();

		// StatusTl
		final Intent intent0 = new Intent(context, StatusTl.class);
		intent0.putExtra("statusId", Long.toString(status.getId()));
		intent0.putExtra("userName", status.getUser().getScreenName());
		final PendingIntent pendingIntent0 = PendingIntent.getActivity(context, rc++, intent0, 0);

		// 返信
		final Intent intent1 = new Intent(context, WearTweet.class);
		intent1.putExtra("inReplyToStatusId", Long.toString(status.getId()));
		intent1.putExtra("mode", "t");
		intent1.putExtra("str1", "@" + status.getUser().getScreenName());
		final RemoteInput remoteInput = new RemoteInput.Builder(ListAdapter.EXTRA_VOICE_REPLY).setLabel(context.getString(R.string.develop_mentiontlwidget_androidwear_choice_dialog)) //
		.setChoices(pref_develop_mentiontlwidget_androidwear_choice_array) //
		.build();
		final PendingIntent pendingIntent1 = PendingIntent.getActivity(context, rc++, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		final NotificationCompat.Action action1 =
				new NotificationCompat.Action.Builder(R.drawable.reply, context.getString(R.string.develop_mentiontlwidget_androidwear_choice_label), pendingIntent1).addRemoteInput(remoteInput).build();
		// actions.add(action1);

		//		// Fav
		//		final Intent intent2 = new Intent(context, WearTweet.class);
		//		intent2.putExtra("mode", "f");
		//		intent2.putExtra("str2", status.getText());
		//		intent2.putExtra("inReplyToStatusId", Long.toString(status.getId()));
		//		PendingIntent pendingIntent2 = PendingIntent.getActivity(context, rc++, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		//		NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(R.drawable.favorite, context.getString(R.string.fav_create), pendingIntent2).build();
		//		// actions.add(action2);

		//		if (status.getUser().isProtected()) {
		//			// RT
		//			final Intent intent3 = new Intent(context, WearTweet.class);
		//			intent3.putExtra("mode", "r");
		//			intent3.putExtra("str2", status.getText());
		//			intent3.putExtra("inReplyToStatusId", Long.toString(status.getId()));
		//			PendingIntent pendingIntent3 = PendingIntent.getActivity(context, rc++, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
		//			NotificationCompat.Action action3 = new NotificationCompat.Action.Builder(R.drawable.retweet, context.getString(R.string.rt_create), pendingIntent3).build();
		//			// actions.add(action3);
		//		}

		// Pak
		//		Intent intent4 = new Intent(context, WearTweet.class);
		//		intent4.putExtra("mode", "p");
		//		intent4.putExtra("str2", status.getText());
		//		intent4.putExtra("inReplyToStatusId", Long.toString(status.getId()));
		//		PendingIntent pendingIntent4 = PendingIntent.getActivity(context, rc++, intent4, PendingIntent.FLAG_UPDATE_CURRENT);
		//		NotificationCompat.Action action4 = new NotificationCompat.Action.Builder(R.drawable.retweet, context.getString(R.string.pak_create), pendingIntent4).build();
		// actions.add(action4);

		final String title = context.getString(R.string.app_name_short);
		final String message = "@" + status.getUser().getScreenName() + ": " + status.getText();
		final Style style = new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title).setSummaryText(ListAdapter.DF.format(status.getCreatedAt()));

		final NotificationCompat.Builder notificationBuilder1 =
				new NotificationCompat.Builder(context).setAutoCancel(false).setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(message) //
				.setContentIntent(pendingIntent0) // StatusTl
				//				.addAction(action1) // 返信
				//				.addAction(action2) // F
				//				.addAction(action3); // R
				.extend(new NotificationCompat.WearableExtender().addAction(action1)) // 返信
				//				.extend(new NotificationCompat.WearableExtender().addAction(action2)) // F
				//				.extend(new NotificationCompat.WearableExtender().addAction(action3)) // R
				//				.extend(new NotificationCompat.WearableExtender().addAction(action4)) // P
				.setStyle(style);
		if (ledColor != Color.TRANSPARENT) {
			notificationBuilder1.setLights(ledColor, 300, 200);
		}
		if (vibrate) {
			notificationBuilder1.setDefaults(Notification.DEFAULT_VIBRATE);
		}
		//		if (status.getUser().isProtected() == false) {
		//			// RT
		//			final Intent intent3 = new Intent(context, WearTweet.class);
		//			intent3.putExtra("mode", "r");
		//			intent3.putExtra("str2", status.getText());
		//			intent3.putExtra("inReplyToStatusId", Long.toString(status.getId()));
		//			PendingIntent pendingIntent3 = PendingIntent.getActivity(context, rc++, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
		//			NotificationCompat.Action action3 = new NotificationCompat.Action.Builder(R.drawable.retweet, context.getString(R.string.rt_create), pendingIntent3).build();
		//			// actions.add(action3);
		//
		//			notificationBuilder1.addAction(action3); // R
		//		}

		final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		notificationManager.notify((int) status.getId(), notificationBuilder1.build());
	}

	// 着信音の再生
	private static final void playSound(final Context context, final String soundUri, final long soundLength) {
		WriteLog.write(context, "playSound()");

		if (( soundUri != null ) && ( soundUri.equals("") == false )) {
			mediaPlayer = MediaPlayer.create(context, Uri.parse(soundUri));
			mediaPlayer.setLooping(false);
			mediaPlayer.seekTo(0);
			mediaPlayer.start();

			new Thread(new Runnable() {
				@Override
				public final void run() {
					try {
						Thread.sleep(soundLength);
					} catch (InterruptedException e) {
						WriteLog.write(context, e);
					}

					if (mediaPlayer != null) {
						try {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.stop();
								mediaPlayer.release();
								mediaPlayer = null;
								//								WriteLog.write(context, "playSound() thread mediaPlayer.stop()");
							}
						} catch (final IllegalStateException e) {
							WriteLog.write(context, e);
						} catch (final Exception e) {
							WriteLog.write(context, e);
						}
					}
				}
			}).start();
		}
	}

	private static final String repeatString(final String str, final int num) {
		return new String(new char[num]).replace("\0", str);
	}

	private static final String replaceAllUrl(final Context context, String text, final String fontcolor_statustext, final String fontcolor_statustext_uri, final URLEntity[] urlEntitys) {
		final boolean pref_enable_expand_uri_fullurl = pref_app.getBoolean("pref_enable_expand_uri_fullurl", false);
		final boolean pref_tl_fontsize_large_url = pref_app.getBoolean("pref_tl_fontsize_large_url", false);

		for (final URLEntity urlEntity : urlEntitys) {
			text =
					text.replace(urlEntity.getURL(), "</font>" + ( pref_tl_fontsize_large_url ? "<big>" : "" ) + "<font color=\"" + fontcolor_statustext_uri + "\"><a class=\"l\" href=\""
							+ urlUtil.expand_uri(urlEntity.getURL()) + "\">" + ( pref_enable_expand_uri_fullurl ? urlEntity.getExpandedURL() : urlEntity.getDisplayURL() ) + "</a></font>"
							+ ( pref_tl_fontsize_large_url ? "</big>" : "" ) + "<font color=\"" + fontcolor_statustext + "\">");
		}

		return text;
	}

	private static final int setTimeout(final Context context, final boolean mode) {
		if (mode) {
			return ListAdapter.getPrefInt(context, "pref_timeout_t4j_connection", "20000");
		} else {
			return ListAdapter.getPrefInt(context, "pref_timeout_t4j_read", "120000");
		}
	}

	public static final String updateStatuses(final Context context, final ComponentName componentName, final Class<?> className) {

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}

		final int networkType = ( new CheckNetworkUtil(context) ).autoConnect(true);
		if (networkType == -1) {
			return "";
		}
		isMobile = ( networkType == ( ConnectivityManager.TYPE_MOBILE ) ) ? true : false;

		urlUtil = new UrlUtil(context);

		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		pref_twtr = context.getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0

		pref_develop_mentiontlwidget_androidwear_choice = pref_app.getString("pref_develop_mentiontlwidget_androidwear_choice", "");
		pref_mute_screenname = "," + pref_app.getString("pref_mute_screenname", "") + "," + pref_twtr.getString("blocked_users", "") + ",";
		pref_mute_source = "," + pref_app.getString("pref_mute_source", "") + ",";
		pref_mute_text = "," + pref_app.getString("pref_mute_text", "") + ",";
		//		WriteLog.write(context, "pref_mute_screenname: " + pref_mute_screenname);
		//		WriteLog.write(context, "pref_mute_source: " + pref_mute_source);
		//		WriteLog.write(context, "pref_mute_text: " + pref_mute_text);

		final int pref_mentiontlwidget_interval = ListAdapter.getPrefInt(context, "pref_mentiontlwidget_interval", "60");

		final String lastTweetIdString = pref_twtr.getString("lastTweetIdString_" + Integer.toString(checkIndexFromPrefTwtr(context)), "0");

		final long lastUpdateTime = Long.parseLong(pref_twtr.getString("last_update_time", "0"));
		final long currentTime = System.currentTimeMillis();

		if (currentTime - lastUpdateTime > 1000) {
			if (mediaPlayer != null) {
				try {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayer = null;
						//						WriteLog.write(context, "mediaPlayer.stop()");
					}
				} catch (final IllegalStateException e) {
					WriteLog.write(context, e);
				} catch (final Exception e) {
					WriteLog.write(context, e);
				}
			}
		}

		//		WriteLog.write(context, "currentTime: " + currentTime + "(" + ListAdapter.DF.format(currentTime) + ")");
		//		WriteLog.write(context, "lastUpdateTime: " + lastUpdateTime + "(" + ListAdapter.DF.format(lastUpdateTime) + ")");
		//		WriteLog.write(context, "(currentTime - lastUpdateTime): " + ( currentTime - lastUpdateTime ) + "(" + ListAdapter.DF.format(( currentTime - lastUpdateTime )) + ")");
		//		WriteLog.write(context, "(1000 * pref_mentiontlwidget_interval): " + ( 1000 * pref_mentiontlwidget_interval ) + "(" + ListAdapter.DF.format(( 1000 * pref_mentiontlwidget_interval )) + ")");

		if (currentTime - lastUpdateTime > 1000 * pref_mentiontlwidget_interval) {
			//			WriteLog.write(context, "(currentTime - lastUpdateTime > 1000 * pref_mentiontlwidget_interval)");

			final int pref_mention_count_mentiontlwidget =
					isMobile ? ListAdapter.getPrefInt(context, "pref_mention_count_mentiontlwidget_mobile", "20") : ListAdapter.getPrefInt(context, "pref_mention_count_mentiontlwidget", "20");
			final int pref_mentiontlwidget_new = ListAdapter.getPrefInt(context, "pref_mentiontlwidget_new", "3600");

			final String pref_tl_fontcolor_screenname_mentiontlwidget = pref_app.getString("pref_tl_fontcolor_screenname_mentiontlwidget", "#000000");
			final String pref_tl_fontcolor_screenname_mentiontlwidget_ff = pref_app.getString("pref_tl_fontcolor_screenname_mentiontlwidget_ff", "#00CCFF");
			final String pref_tl_fontcolor_screenname_mentiontlwidget_ffnew = pref_app.getString("pref_tl_fontcolor_screenname_mentiontlwidget_ffnew", "#00CCFF");
			final String pref_tl_fontcolor_screenname_mentiontlwidget_new = pref_app.getString("pref_tl_fontcolor_screenname_mentiontlwidget_new", "#00CCFF");

			final String pref_tl_fontcolor_statustext_mentiontlwidget = pref_app.getString("pref_tl_fontcolor_statustext_mentiontlwidget", "#000000");
			final String pref_tl_fontcolor_statustext_mentiontlwidget_ff = pref_app.getString("pref_tl_fontcolor_statustext_mentiontlwidget_ff", "#FFFF00");
			final String pref_tl_fontcolor_statustext_mentiontlwidget_ffnew = pref_app.getString("pref_tl_fontcolor_statustext_mentiontlwidget_ffnew", "#FFFF00");
			final String pref_tl_fontcolor_statustext_mentiontlwidget_new = pref_app.getString("pref_tl_fontcolor_statustext_mentiontlwidget_new", "#FFFF00");

			final String pref_tl_fontcolor_statustext_uri_mentiontlwidget = pref_app.getString("pref_tl_fontcolor_statustext_uri_mentiontlwidget", "#000000");
			final String pref_tl_fontcolor_statustext_uri_mentiontlwidget_ff = pref_app.getString("pref_tl_fontcolor_statustext_uri_mentiontlwidget_ff", "#0000ff");
			final String pref_tl_fontcolor_statustext_uri_mentiontlwidget_ffnew = pref_app.getString("pref_tl_fontcolor_statustext_uri_mentiontlwidget_ffnew", "#0000ff");
			final String pref_tl_fontcolor_statustext_uri_mentiontlwidget_new = pref_app.getString("pref_tl_fontcolor_statustext_uri_mentiontlwidget_new", "#0000ff");

			final boolean pref_mentiontlwidget_enable_check_mutualfollow = pref_app.getBoolean("pref_mentiontlwidget_enable_check_mutualfollow", true);
			final boolean pref_mentiontlwidget_enable_notification_only_newest = pref_app.getBoolean("pref_mentiontlwidget_enable_notification_only_newest", true);
			final boolean pref_mentiontlwidget_enable_expand_uri = pref_app.getBoolean("pref_mentiontlwidget_enable_expand_uri", true);
			final boolean pref_mentiontlwidget_enable_notification = pref_app.getBoolean("pref_mentiontlwidget_enable_notification", true);

			final boolean pref_mentiontlwidget_enable_notification_only_mutualfollow = pref_app.getBoolean("pref_mentiontlwidget_enable_notification_only_mutualfollow", true);

			final boolean pref_mentiontlwidget_only_mutualfollow = pref_app.getBoolean("pref_mentiontlwidget_only_mutualfollow", false);

			// ステータスの更新
			int idx = checkIndexFromPrefTwtr(context);
			try {
				final Paging paging = new Paging();
				paging.setCount(pref_mention_count_mentiontlwidget);
				statuses = getTwitter(context, idx, false).getMentionsTimeline(paging);
			} catch (final TwitterException e) {
				WriteLog.write(context, e);
			} catch (final Exception e) {
				WriteLog.write(context, e);
			}

			if (statuses.size() > 0) {
				//				WriteLog.write(context, "(statuses.size() > 0)");

				MentionTlWidgetMethods.updateTextview(context, componentName, "loading: " + statuses.size());

				final SharedPreferences.Editor editor = pref_twtr.edit();

				if (pref_mentiontlwidget_enable_notification) {
					if (( Long.toString(statuses.get(0).getId()) ).equals(lastTweetIdString) == false) {

						boolean play_sound = false;

						int i = 0;
						for (final Status status : statuses) {
							MentionTlWidgetMethods.updateTextview(context, componentName, "loading: " + statuses.size() + NL + repeatString(".", i++));

							if (( status.getId() ) > ( Long.parseLong(lastTweetIdString) )) {

								if (!checkMute(status)) {
									if (pref_mentiontlwidget_enable_check_mutualfollow) {
										try {
											final Relationship relationship =
													getTwitter(context, idx, false).showFriendship(pref_twtr.getString("screen_name_" + idx, ""), status.getUser().getScreenName());
											if (( pref_mentiontlwidget_enable_notification_only_mutualfollow == false )
													| ( ( relationship.isSourceFollowedByTarget() ) && ( relationship.isSourceFollowingTarget() ) )) {
												//												WriteLog.write(context, "ff");
												play_sound = true;
												notification(context, status, Color.TRANSPARENT, false);
												if (pref_mentiontlwidget_enable_notification_only_newest) {
													break;
												}
											}
										} catch (final TwitterException e) {
											WriteLog.write(context, e);
										}
									} else {
										play_sound = true;
										notification(context, status, Color.TRANSPARENT, false);
										if (pref_mentiontlwidget_enable_notification_only_newest) {
											break;
										}
									}
								}
							} else {
								break;
							}
						}

						if (play_sound) {
							//							WriteLog.write(context, "(play_sound == true)");

							final boolean pref_enable_ringtone_onmention = pref_app.getBoolean("pref_enable_ringtone_onmention", true);
							if (pref_enable_ringtone_onmention) {
								final String pref_ringtone_onmention_mentiontlwidget = pref_app.getString("pref_ringtone_onmention_mentiontlwidget", "");
								final int pref_ringtone_onmention_mentiontlwidget_length = ListAdapter.getPrefInt(context, "pref_ringtone_onmention_mentiontlwidget_length", "10000");
								playSound(context, pref_ringtone_onmention_mentiontlwidget, pref_ringtone_onmention_mentiontlwidget_length);
							}
						}

						editor.putString("lastTweetIdString_" + Integer.toString(checkIndexFromPrefTwtr(context)), Long.toString(statuses.get(0).getId()));
						//						WriteLog.write(context, "editor.putString(\"lastTweetIdString\")");
					}
				}

				final int sbInitSize2 = 360; // 265 * 4/3
				final int sbInitSize = 130 + statuses.size() * sbInitSize2;
				// ( 94 + statuses * sbInitSize2 ) * 4/3
				final StringBuilder timelineItemBuilder = new StringBuilder(sbInitSize);
				timelineItemBuilder.append("<font color=\"");
				timelineItemBuilder.append(pref_tl_fontcolor_statustext_mentiontlwidget);
				timelineItemBuilder.append("\"><small>");
				timelineItemBuilder.append(ListAdapter.DF.format(currentTime));
				timelineItemBuilder.append(" ; ");
				timelineItemBuilder.append(ListAdapter.DF.format(lastUpdateTime));
				timelineItemBuilder.append("</small></font>");
				timelineItemBuilder.append(ListAdapter.BR);

				int i = 0;
				for (final Status status : statuses) {
					MentionTlWidgetMethods.updateTextview(context, componentName, "loading: " + statuses.size() + NL + repeatString(".", statuses.size()) + NL + repeatString(".", i++));

					final StringBuilder timelinestatusItemBuilder = new StringBuilder(sbInitSize2);

					if (!checkMute(status)) {
						final Date createdAt = status.getCreatedAt();
						final Date nowDate = new Date();

						timelinestatusItemBuilder.append("<font color=\"");

						final boolean isNew = ( nowDate.getTime() - createdAt.getTime() ) < ( 1000 * pref_mentiontlwidget_new );
						boolean isMutualFollow = false;
						if (pref_mentiontlwidget_enable_check_mutualfollow) {
							try {
								final Relationship relationship = getTwitter(context, idx, false).showFriendship(pref_twtr.getString("screen_name_" + idx, ""), status.getUser().getScreenName());
								if (( relationship.isSourceFollowedByTarget() ) && ( relationship.isSourceFollowingTarget() )) {
									// 相互フォロー
									isMutualFollow = true;
									if (isNew) {
										timelinestatusItemBuilder.append(pref_tl_fontcolor_screenname_mentiontlwidget_ffnew);
									} else {
										timelinestatusItemBuilder.append(pref_tl_fontcolor_screenname_mentiontlwidget_ff);
									}
								} else {
									// 非相互フォロー
									if (isNew) {
										timelinestatusItemBuilder.append(pref_tl_fontcolor_screenname_mentiontlwidget_new);
									} else {
										timelinestatusItemBuilder.append(pref_tl_fontcolor_screenname_mentiontlwidget);
									}
								}
							} catch (final TwitterException e) {
								WriteLog.write(context, e);
								if (isNew) {
									timelinestatusItemBuilder.append(pref_tl_fontcolor_screenname_mentiontlwidget_new);
								} else {
									timelinestatusItemBuilder.append(pref_tl_fontcolor_screenname_mentiontlwidget);
								}
							}
						} else {
							if (isNew) {
								timelinestatusItemBuilder.append(pref_tl_fontcolor_screenname_mentiontlwidget_new);
							} else {
								timelinestatusItemBuilder.append(pref_tl_fontcolor_screenname_mentiontlwidget);
							}
						}
						timelinestatusItemBuilder.append("\"><b><big><u>@");
						timelinestatusItemBuilder.append(status.getUser().getScreenName());
						timelinestatusItemBuilder.append("</u></big></b></font><font color=\"");

						if (isMutualFollow) {
							if (isNew) {
								timelinestatusItemBuilder.append(pref_tl_fontcolor_statustext_mentiontlwidget_ffnew);

								timelinestatusItemBuilder.append("\">: ");
								timelinestatusItemBuilder.append(pref_mentiontlwidget_enable_expand_uri
										? ( replaceAllUrl(context, status.getText(), pref_tl_fontcolor_statustext_mentiontlwidget_ffnew, pref_tl_fontcolor_statustext_uri_mentiontlwidget_ffnew, status.getURLEntities()) )
										: status.getText());
								timelinestatusItemBuilder.append("</font>" + ListAdapter.BR);
							} else {
								timelinestatusItemBuilder.append(pref_tl_fontcolor_statustext_mentiontlwidget_ff);

								timelinestatusItemBuilder.append("\">: ");
								timelinestatusItemBuilder.append(pref_mentiontlwidget_enable_expand_uri
										? ( replaceAllUrl(context, status.getText(), pref_tl_fontcolor_statustext_mentiontlwidget_ff, pref_tl_fontcolor_statustext_uri_mentiontlwidget_ff, status.getURLEntities()) )
										: status.getText());
								timelinestatusItemBuilder.append("</font>" + ListAdapter.BR);
							}
						} else {
							if (isNew) {
								timelinestatusItemBuilder.append(pref_tl_fontcolor_statustext_mentiontlwidget_new);

								timelinestatusItemBuilder.append("\">: ");
								timelinestatusItemBuilder.append(pref_mentiontlwidget_enable_expand_uri
										? ( replaceAllUrl(context, status.getText(), pref_tl_fontcolor_statustext_mentiontlwidget_new, pref_tl_fontcolor_statustext_uri_mentiontlwidget_new, status.getURLEntities()) )
										: status.getText());
								timelinestatusItemBuilder.append("</font>" + ListAdapter.BR);
							} else {
								timelinestatusItemBuilder.append(pref_tl_fontcolor_statustext_mentiontlwidget);

								timelinestatusItemBuilder.append("\">: ");
								timelinestatusItemBuilder.append(pref_mentiontlwidget_enable_expand_uri
										? ( replaceAllUrl(context, status.getText(), pref_tl_fontcolor_statustext_mentiontlwidget, pref_tl_fontcolor_statustext_uri_mentiontlwidget, status.getURLEntities()) )
										: status.getText());
								timelinestatusItemBuilder.append("</font>" + ListAdapter.BR);
							}
						}

						if (( isMutualFollow ) || ( !pref_mentiontlwidget_only_mutualfollow )) {
							timelineItemBuilder.append(timelinestatusItemBuilder.toString());
						}
					}
				}

				try {
					editor.putString("last_update_time", Long.toString(currentTime));
					editor.commit();
				} catch (final Exception e) {
					WriteLog.write(context, e);
				}

				//				WriteLog.write(context, "return timelineItemBuilder.toString()");
				return timelineItemBuilder.toString();
			} else {
				//				WriteLog.write(context, "!(statuses.size() > 0)");
			}
		} else {
			WriteLog.write(context, "frequency");
		}

		return "";
	}

	public static final void updateTextview(final Context context, final ComponentName componentName, final CharSequence string) {
		final int pref_tl_fontsize_mentiontlwidget = ListAdapter.getPrefInt(context, "pref_tl_fontsize_mentiontlwidget", "12");
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.mentiontlwidget);
		final Intent newintent = new Intent();
		newintent.setAction(ACTION_BTNCLICK);
		final PendingIntent pending = PendingIntent.getService(context, 0, newintent, 0);
		remoteViews.setOnClickPendingIntent(R.id.TextView, pending);
		remoteViews.setFloat(R.id.TextView, "setTextSize", pref_tl_fontsize_mentiontlwidget);
		remoteViews.setTextViewText(R.id.TextView, string);

		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		appWidgetManager.updateAppWidget(componentName, remoteViews);
	}
}
