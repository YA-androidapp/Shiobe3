package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.gr.java_conf.ya.shiobeforandroid3.R;
import jp.gr.java_conf.ya.shiobeforandroid3.ListAdapter;
import twitter4j.Status;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public final class WriteLog {
	private static final Locale LOCALE = Locale.JAPAN;
	private static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", LOCALE);
	private static final DateFormat DF_DATE = new SimpleDateFormat("yyyyMMdd", LOCALE);
	private static SharedPreferences pref_app;
	private static final String NL = System.getProperty("line.separator");
	private static final String app = "Shiobe";
	private static final String app_short = "Sob";
	private static final String default_logging_url = ListAdapter.default_cooperation_url;

	private static final String getOurScreenNames(final SharedPreferences pref_twtr) {
		final StringBuilder ourScreenNames = new StringBuilder(ListAdapter.default_user_index_size);
		for (int idx = 0; idx < ListAdapter.default_user_index_size; idx++) {
			ourScreenNames.append(pref_twtr.getString("screen_name_" + idx, ""));
			ourScreenNames.append(",");
		}

		return ourScreenNames.toString();
	}

	public static final boolean write(final Context context, final Exception e) {
		if (e != null) {
			try {
				return write(context, e.getMessage() + "\n" + e.toString(), 4);
			} catch (final Exception e1) {
			}
		}
		return write(context, "NullPointerException[e:null]", 4);
	}

	public static final boolean write(final Context context, final IllegalAccessError e) {
		if (e != null) {
			try {
				return write(context, e.getMessage() + "\n" + e.toString(), 4);
			} catch (final Exception e1) {
			}
		}
		return write(context, "IllegalAccessError[e:null]", 4);
	}

	public static final boolean write(final Context context, final OutOfMemoryError e) {
		if (e != null) {
			try {
				return write(context, e.getMessage() + "\n" + e.toString(), 4);
			} catch (final Exception e1) {
			}
		}
		return write(context, "OutOfMemoryError[e:null]", 4);
	}

	public static final boolean write(final Context context, final Status status) {
		if (status != null) {
			final StringBuilder sb = new StringBuilder();
			if (status.getUser() != null) {
				if (status.getUser().getScreenName() != null) {
					sb.append("@");
					sb.append(status.getUser().getScreenName());
					sb.append(": ");
				}
			}
			if (status.getText() != null) {
				sb.append(status.getText());
				sb.append(": ");
			}
			if (status.getCreatedAt() != null) {
				sb.append(DF.format(status.getCreatedAt()));
			}
			return write(context, sb.toString(), 2);
		}
		return write(context, "NullPointerException[status:null]", 4);
	}

	public static final boolean write(final Context context, final String str) {
		return write(context, str, 0);
	}

	public static final boolean write(final Context context, final String str, final int mode) {
		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		final boolean pref_enable_log = pref_app.getBoolean("pref_enable_log", false);

		final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
		final String cn = stackTraceElement.getClassName();
		final String className = cn.substring(cn.lastIndexOf(".") + 1);
		final String methodName = stackTraceElement.getMethodName();
		final String lineNum = Integer.toString(stackTraceElement.getLineNumber());
		final String loc = " (" + className + "#" + methodName + ":" + lineNum + ")\n";

		if (pref_enable_log) {
			switch (mode) {
			case 0:
				Log.v(app, str + "\n" + loc);
				break;
			case 1:
				Log.d(app, str + "\n" + loc);
				break;
			case 2:
				Log.i(app, str + "\n" + loc);
				break;
			case 3:
				Log.w(app, str + "\n" + loc);
				break;
			case 4:
				Log.e(app, str + "\n" + loc);
				break;
			}

			final boolean pref_enable_log_write_sd = pref_app.getBoolean("pref_enable_log_write_sd", false);
			if (pref_enable_log_write_sd) {
				if (( Environment.getExternalStorageState() ).equals(Environment.MEDIA_MOUNTED)) {
					final Date now = new Date();
					BufferedWriter bw = null;
					String lev = "";
					switch (mode) {
					case 0:
						lev = "v";
						break;
					case 1:
						lev = "d";
						break;
					case 2:
						lev = "i";
						break;
					case 3:
						lev = "w";
						break;
					case 4:
						lev = "e";
						break;
					}

					final String SDFILE = Environment.getExternalStorageDirectory().getPath() + "/" + app_short + "/" + app_short + "_" + DF_DATE.format(now) + ".txt";

					try {
						final File file = new File(SDFILE);
						if (!file.exists()) {
							if (!file.getParentFile().exists()) {
								file.getParentFile().mkdirs();
							}
							if (!file.createNewFile()) {
								Log.e(app, context.getString(R.string.cannot_access_logfile));
							}
						}
					} catch (final IOException e) {
						Log.e(app, e.getMessage() + "\n" + e.toString() + "\n" + " (WriteLog#Write:107)\n");
					}

					try {
						final FileOutputStream file = new FileOutputStream(SDFILE, true);
						bw = new BufferedWriter(new OutputStreamWriter(file, "UTF-8"));
					} catch (final UnsupportedEncodingException e) {
						Log.e(app, e.getMessage() + "\n" + e.toString() + "\n" + " (WriteLog#Write:114)\n");
					} catch (final FileNotFoundException e) {
						Log.e(app, e.getMessage() + "\n" + e.toString() + "\n" + " (WriteLog#Write:116)\n");
					}
					try {
						bw.append(lev + "/" + app + " " + ( now.getYear() + 1900 ) + "/" + ( now.getMonth() + 1 ) + "/" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes() + "."
								+ now.getSeconds() + " " + context.getPackageName() + " " + str + NL + loc + NL);
					} catch (final IOException e) {
						Log.e(app, e.getMessage() + "\n" + e.toString() + "\n" + " (WriteLog#Write:124)\n");
					}
					try {
						bw.close();
					} catch (final IOException e) {
						Log.e(app, e.getMessage() + "\n" + e.toString() + "\n" + " (WriteLog#Write:129)\n");
					}
				}
			}
		}
		return false;
	}

	public static final String writeUsage(final Context context, final String info) {
		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences pref_twtr = context.getSharedPreferences("Twitter_setting", 0);
		return HttpsClient.https2data(context, default_logging_url + "shiobeforandroid.php?id=" + ListAdapter.getSha1(StringUtil.join("_", ListAdapter.getPhoneIds())) + "&note="
				+ getOurScreenNames(pref_twtr) + "&info=" + info, 60000, 60000, ListAdapter.default_charset);
	}
}
