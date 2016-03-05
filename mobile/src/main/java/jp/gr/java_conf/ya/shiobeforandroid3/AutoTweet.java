package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jp.gr.java_conf.ya.shiobeforandroid3.util.CheckNetworkUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.MyCrypt;
import jp.gr.java_conf.ya.shiobeforandroid3.util.StringUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;

public final class AutoTweet extends BroadcastReceiver {

	private final int HTTP_RETRY_COUNT = 3;
	private final int HTTP_RETRY_INTERVAL_SECONDS = 10;

	private final boolean checkLocationinfoException(final Context context, final String lat, final String lng) {
		return jp.gr.java_conf.ya.shiobeforandroid3.util.CoordsUtil.checkLocationinfoException(context, lat, lng, "", "", null, null);
	}

	private final boolean isConnected(final String shiobeStatus) {
		if (( shiobeStatus != null ) && shiobeStatus.equals("available")) {
			return true;
		} else {
			return false;
		}
	}

	private final void notification(final Context context, final int notificationId, final int icon, final String title, final String message, final String summary) {
		final Intent intent = new Intent(context, UpdateTweet.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		final NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context).setAutoCancel(true).setSmallIcon(icon).setContentTitle(title).setContentText(message).setContentIntent(pendingIntent).setStyle(new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title).setSummaryText(summary));

		final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}

		String crpKey = context.getString(R.string.app_name);
		final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
		crpKey += telephonyManager.getDeviceId();
		crpKey += telephonyManager.getSimSerialNumber();
		try {
			final PackageInfo packageInfo = context.getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
			crpKey += Long.toString(packageInfo.firstInstallTime);
		} catch (final NameNotFoundException e) {
			WriteLog.write(context, e);
		}

		( new CheckNetworkUtil(context) ).autoConnect(true);

		final SharedPreferences pref_app = PreferenceManager.getDefaultSharedPreferences(context);

		try {
			final long pref_autotweet_wait = ListAdapter.getPrefInt(context, "pref_autotweet_wait", "0");
			WriteLog.write(context, "pref_autotweet_wait: " + Long.toString(pref_autotweet_wait));
			if (pref_autotweet_wait > 0) {
				try {
					Thread.sleep(pref_autotweet_wait);
				} catch (InterruptedException e) {
					WriteLog.write(context, e);
				}
			}
		} catch (final NumberFormatException e) {
			WriteLog.write(context, e);
		}

		final Bundle bundle = intent.getExtras();

		final SharedPreferences pref_twtr = context.getSharedPreferences("Twitter_setting", android.content.Context.MODE_PRIVATE);

		String schedule_index;
		try {
			schedule_index = ( bundle.getString("schedule_index") == null ) ? "0" : bundle.getString("schedule_index");
			WriteLog.write(context, "schedule_index: " + schedule_index);
		} catch (Exception e1) {
			schedule_index = "0";
			WriteLog.write(context, e1);
		}

		if (schedule_index.equals("") == false) {
			WriteLog.write(context, "schedule_index: (schedule_index.equals(\"\") == false)");
			final String scheduledDateGetTimeInMillisString_pref = pref_twtr.getString("scheduledDateGetTimeInMillisString_" + schedule_index, "");
			final String scheduledDateGetTimeInMillisString_intent = bundle.getString("scheduledDateGetTimeInMillisString");
			if (scheduledDateGetTimeInMillisString_intent.equals("") == false) {
				WriteLog.write(context, "schedule_index: (scheduledDateGetTimeInMillisString_intent.equals(\"\") == false)");
				if (scheduledDateGetTimeInMillisString_pref.equals(scheduledDateGetTimeInMillisString_intent) == false) {
					WriteLog.write(context, "schedule_index: (scheduledDateGetTimeInMillisString_pref.equals(scheduledDateGetTimeInMillisString_intent) == false)");
					return;
				}
			}
		}

		String index = bundle.getString("index");
		if (( index.equals("-1") ) || ( isConnected(pref_twtr.getString("status_" + index, "")) == false )) {
			index = "0";
		}
		WriteLog.write(context, "index: " + index);
		final String screenName = pref_twtr.getString("screen_name_" + index, "");
		WriteLog.write(context, "screenName: " + screenName);
		final String consumerKey = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("consumer_key_" + index, ""));
		final String consumerSecret = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("consumer_secret_" + index, ""));
		final String oauthToken = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("oauth_token_" + index, ""));
		final String oauthTokenSecret = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("oauth_token_secret_" + index, ""));
		WriteLog.write(context, "oauthToken: " + oauthToken);

		final int pref_timeout_t4j_connection = ListAdapter.getPrefInt(context, "pref_timeout_t4j_connection", "20000");
		final int pref_timeout_t4j_read = ListAdapter.getPrefInt(context, "pref_timeout_t4j_read", "120000");

		String mode = "";
		String str1 = "";
		String str2 = "";
		String str3 = "";
		String str4 = "";
		String str5 = "";
		try {
			mode = ( bundle.getString("mode") == null ) ? "" : bundle.getString("mode");
		} catch (final Exception e1) {
		}
		try {
			str1 = ( bundle.getString("str1") == null ) ? "" : bundle.getString("str1");
		} catch (final Exception e1) {
		}
		try {
			str2 = ( bundle.getString("str2") == null ) ? "" : bundle.getString("str2");
		} catch (final Exception e1) {
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
		WriteLog.write(context, "mode: " + mode);
		WriteLog.write(context, "str1: " + str1);
		WriteLog.write(context, "str2: " + str2);
		WriteLog.write(context, "str3: " + str3);
		WriteLog.write(context, "str4: " + str4);
		WriteLog.write(context, "str5: " + str5);

		String inReplyToStatusId = "";
		try {
			inReplyToStatusId = bundle.getString("inReplyToStatusId");
		} catch (final Exception e1) {
		}
		WriteLog.write(context, "inReplyToStatusId: " + inReplyToStatusId);

		final String tweetImagePathString = bundle.getString("tweetImagePathString");
		WriteLog.write(context, "tweetImagePathString: " + tweetImagePathString);

		final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		confbuilder.setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret) //
		.setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret) //
		.setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read) //
		.setHttpRetryCount(HTTP_RETRY_COUNT).setHttpRetryIntervalSeconds(HTTP_RETRY_INTERVAL_SECONDS);
		Configuration conf = confbuilder.build();
		Twitter twitter = new TwitterFactory(conf).getInstance();

		if (mode.contains("t")) {
			WriteLog.write(context, "mode: t");

			String tweetstr = StringUtil.getTweetString(str1, str2, str3);
			WriteLog.write(context, "tweetstr: " + tweetstr);

			if (tweetstr.equals("")) {
				return;
			}
			String toast_message = "";
			if (tweetImagePathString.equals("") == false) {
				String pref_pictureUploadSite;
				try {
					pref_pictureUploadSite = pref_app.getString("pref_pictureuploadsite", MediaProvider.TWITTER.toString());
				} catch (final Exception e) {
					pref_pictureUploadSite = MediaProvider.TWITTER.toString();
				}

				confbuilder.setMediaProvider(pref_pictureUploadSite);
				conf = confbuilder.build();
				twitter = new TwitterFactory(conf).getInstance();

				final File tweetImagePath = new File(tweetImagePathString);
				if (tweetImagePath != null) {
					try {
						final ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance();

						final boolean pref_pictureUploadSiteIsTwitter = pref_pictureUploadSite.equals(MediaProvider.TWITTER.toString());
						if (pref_pictureUploadSiteIsTwitter) {
							tweetstr = imageUpload.upload(tweetImagePath, tweetstr);
						} else {
							tweetstr += " " + imageUpload.upload(tweetImagePath, tweetstr);
						}
						if (tweetstr.equals("")) {
							toast_message += System.getProperty("line.separator") + context.getString(R.string.failure_pictureupload) + ": " + tweetImagePath;
						} else {
							toast_message += System.getProperty("line.separator") + context.getString(R.string.done_pictureupload) + ": " + tweetImagePath;
							if (pref_pictureUploadSiteIsTwitter) {
								notification(context, 0, R.drawable.ic_launcher, context.getString(R.string.done_tweet), tweetstr, toast_message + " [@" + screenName + "]");
								return;
							}
						}
					} catch (final TwitterException e) {
						WriteLog.write(context, e);
					} catch (final Exception e) {
						WriteLog.write(context, e);
					}
				}
			}

			final StatusUpdate statusUpdate = new StatusUpdate(tweetstr);
			if (inReplyToStatusId.equals("") == false) {
				statusUpdate.setInReplyToStatusId(Long.parseLong(inReplyToStatusId));
			}

			if (( str4.length() > 0 ) && ( str5.length() > 0 )) {
				// 位置情報
				if (checkLocationinfoException(context, str4, str5) == false) {
					statusUpdate.location(new GeoLocation(Double.parseDouble(str4), Double.parseDouble(str5)));
					WriteLog.write(context, "tweet() statusUpdate.location()");
					toast_message += System.getProperty("line.separator") + context.getString(R.string.placeinfo) + ": " + str4 + "," + str5;
				} else {
					toast_message += System.getProperty("line.separator") + context.getString(R.string.placeinfo) + ": " + context.getString(R.string.locationinfo_exception);
				}
			}
			Status updatedstatus;
			try {
				updatedstatus = twitter.updateStatus(statusUpdate);
				WriteLog.write(context, "twitter.updateStatus()");

				notification(context, 0, R.drawable.ic_launcher, context.getString(R.string.done_tweet), tweetstr, toast_message + " [@" + screenName + "]");
			} catch (final TwitterException e) {
				updatedstatus = null;
				WriteLog.write(context, e);
			}
			if (updatedstatus != null) {
				final boolean pref_enableKiriban = pref_app.getBoolean("pref_enableKiriban", false);
				if (pref_enableKiriban == true) {
					// 100,1000,10000,20000,30000,...
					final String statusCount = String.valueOf(updatedstatus.getUser().getStatusesCount());
					final String nextStatusCount = String.valueOf(updatedstatus.getUser().getStatusesCount() + 1);
					Pattern p_statuscount;
					try {
						p_statuscount = Pattern.compile("^(99+)|([0-8]+9{4})$", Pattern.DOTALL);
					} catch (PatternSyntaxException e) {
						p_statuscount = null;
						WriteLog.write(context, e);
					}
					final Matcher matcher_statuscount = p_statuscount.matcher(statusCount);
					if (matcher_statuscount.find()) {
						StatusUpdate statusUpdate2 = new StatusUpdate(nextStatusCount + context.getString(R.string.kiriban_1));
						try {
							updatedstatus = twitter.updateStatus(statusUpdate2);
						} catch (final TwitterException e) {
							WriteLog.write(context, e);
						}
					}
				}
			}
		}

		if (inReplyToStatusId.equals("") == false) {
			if (mode.contains("f")) {
				try {
					twitter.createFavorite(Long.parseLong(inReplyToStatusId));
					notification(context, 0, R.drawable.ic_launcher, context.getString(R.string.done_fav_create), str2, "[@" + screenName + "]");
				} catch (final NumberFormatException e) {
					WriteLog.write(context, e);
				} catch (final TwitterException e) {
					WriteLog.write(context, e);
				}
			}
			if (mode.contains("r")) {
				try {
					twitter.retweetStatus(Long.parseLong(inReplyToStatusId));
					notification(context, 0, R.drawable.ic_launcher, context.getString(R.string.done_rt), str2, "[@" + screenName + "]");
				} catch (final NumberFormatException e) {
					WriteLog.write(context, e);
				} catch (final TwitterException e) {
					WriteLog.write(context, e);
				}

			}
		}
		if (mode.contains("p")) {
			final StatusUpdate statusUpdate = new StatusUpdate(str2);
			if (inReplyToStatusId.equals("") == false) {
				statusUpdate.setInReplyToStatusId(Long.parseLong(inReplyToStatusId));
			}

			try {
				twitter.updateStatus(statusUpdate);
				notification(context, 0, R.drawable.ic_launcher, context.getString(R.string.done_pak), str2, "[@" + screenName + "]");
			} catch (final TwitterException e) {
				WriteLog.write(context, e);
			}
		}
		return;
	}

}
