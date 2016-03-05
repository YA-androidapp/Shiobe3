package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gr.java_conf.ya.shiobeforandroid3.util.CheckNetworkUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.HttpsClient;
import jp.gr.java_conf.ya.shiobeforandroid3.util.MyCrypt;
import jp.gr.java_conf.ya.shiobeforandroid3.util.StringUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;

public final class UpdateTweetMultiple extends AppCompatActivity {
	public final class EdittextComparator implements Comparator<View> {
		@Override
		public final int compare(final View a, final View b) {
			return ( a.getId() - b.getId() ) / Math.abs(a.getId() - b.getId());
		}
	}

	final class FileCheck implements FilenameFilter {
		private String screenName = "";

		public FileCheck(final String screenName) {
			this.screenName = screenName;
		}

		@Override
		public final boolean accept(final File dir, final String strfilename) {
			if (strfilename.startsWith(this.screenName + "_multiplelist_") && strfilename.endsWith(".cgi")) {
				return true;
			} else {
				return false;
			}
		}
	}

	private ListAdapter adapter;

	final char[] c = { '\u3000' };
	final String wspace = new String(c);

	private int editText_index = 100;
	private int pre_key = 0;

	private static final Pattern convHTTPSURLLinkPtn = Pattern.compile("(https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+", Pattern.CASE_INSENSITIVE);
	private static final Pattern convHTTPURLLinkPtn = Pattern.compile("(http://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+", Pattern.CASE_INSENSITIVE);
	private static String consumerKey = "";
	private static String consumerSecret = "";

	private static int pref_timeout_t4j_connection = 20000;
	private static int pref_timeout_t4j_read = 120000;

	private String crpKey = "";

	private String screenName = "";
	private String multiple_filename = "";
	// View
	private AutoCompleteTextView editText1;
	private AutoCompleteTextView editText2;
	private Button button1;
	private EditText editText;
	private EditText editText3;
	private EditText last_edited_editText = editText;
	private SharedPreferences pref_app;

	private TableLayout tableLayout1;
	private TableRow tableRow1;
	// View
	// Twitter
	private Configuration conf;
	private ConfigurationBuilder confbuilder;
	private SharedPreferences pref_twtr;

	// Twitter

	private String oauthToken = "";

	private String oauthTokenSecret = "";

	boolean mLongPressed = false;

	private static final boolean currentThreadIsUiThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	private final void add_editText() {
		final TableRow.LayoutParams row_layout_params = new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		final TableRow.LayoutParams text_layout_params = new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		text_layout_params.weight = 1.0f;
		tableRow1 = new TableRow(UpdateTweetMultiple.this);
		tableRow1.setLayoutParams(row_layout_params);
		editText = new EditText(UpdateTweetMultiple.this);
		editText.requestFocusFromTouch();
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.setHint(Integer.toString(( editText_index - 100 ) + 2));
		editText.setLayoutParams(text_layout_params);
		editText.setId(editText_index);
		WriteLog.write(this, "Tag: " + editText_index);
		editText.setTextSize(14f);
		editText_index++;

		editText.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public final boolean onLongClick(final View v2) {
				final int index = adapter.checkIndexFromPrefTwtr();
				screenName = pref_twtr.getString("screen_name_" + index, " - ");
				try {
					last_edited_editText = (EditText) v2;
				} catch (final Exception e) {
				}
				try {
					init_user_oauth(index);
					adapter.toast(getString(R.string.doing_send) + ": " + last_edited_editText.getText().toString() + System.getProperty("line.separator") + System.getProperty("line.separator")
							+ "[@" + screenName + "]");
					tweet(Integer.toString(index), screenName, editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString());
				} catch (final TwitterException e) {
				}
				return true;
			}
		});
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public final void afterTextChanged(final Editable s) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}

			@Override
			public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}
		});
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public final void onFocusChange(final View arg0, final boolean arg1) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}
		});
		editText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public final boolean onKey(final View v2, final int keyCode, final KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (( pre_key == KeyEvent.KEYCODE_SHIFT_LEFT ) || ( pre_key == KeyEvent.KEYCODE_SHIFT_RIGHT ) || ( pre_key == KeyEvent.KEYCODE_CTRL_LEFT )
							|| ( pre_key == KeyEvent.KEYCODE_CTRL_RIGHT )) {
						pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
						final int index = adapter.checkIndexFromPrefTwtr();
						screenName = pref_twtr.getString("screen_name_" + index, " - ");
						try {
							last_edited_editText = (EditText) v2;
						} catch (final Exception e) {
						}
						try {
							init_user_oauth(index);
							adapter.toast(getString(R.string.doing_send) + ": " + last_edited_editText.getText().toString() + System.getProperty("line.separator")
									+ System.getProperty("line.separator") + "[@" + screenName + "]");
							tweet(Integer.toString(index), screenName, editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString());
						} catch (final TwitterException e) {
						}
						tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
						pre_key = 0;
						return true;
					}
				}
				if (( keyCode == KeyEvent.KEYCODE_SHIFT_LEFT ) || ( keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT ) || ( keyCode == KeyEvent.KEYCODE_CTRL_LEFT ) || ( keyCode == KeyEvent.KEYCODE_CTRL_RIGHT )) {
					pre_key = keyCode;
				} else {
					pre_key = 0;
				}
				return false;
			}
		});
		tableRow1.addView(editText);
		tableLayout1.addView(tableRow1);
	}

	private final void check() {
		( new CheckNetworkUtil(this) ).autoConnect(false);

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean pref_keepScreenOn = pref_app.getBoolean("pref_keepScreenOn", false);
		if (pref_keepScreenOn == true) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
		init_user(adapter.checkIndexFromPrefTwtr());
	}

	//普通に戻るボタンを押してもアプリを終了させない
	@Override
	public final boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				//trueを返して戻るのを無効化する
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	private final void init_user(int index) {
		String screenName = init_user_oauth(index);
		if (screenName.equals("")) {
			WriteLog.write(this, "screenName.equals(\"\")");
			pref_twtr = getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0
			index = adapter.checkIndexFromPrefTwtr();
			screenName = pref_twtr.getString("screen_name_" + index, " - ");
			// finish();
		} else {
			WriteLog.write(this, "!screenName.equals(\"\")");
			init_user_profimage(index);
			init_user_autocomplete(index);
		}
	}

	private final void init_user_autocomplete(int index) {
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean pref_enable_cooperation_url = pref_app.getBoolean("pref_enable_cooperation_url", false);
		if (pref_enable_cooperation_url) {
			final String pref_cooperation_url = pref_app.getString("pref_cooperation_url", ListAdapter.default_cooperation_url);
			int pref_timeout_connection = ListAdapter.getPrefInt(this, "pref_timeout_connection", ListAdapter.default_timeout_connection_string);
			int pref_timeout_so = ListAdapter.getPrefInt(this, "pref_timeout_so", ListAdapter.default_timeout_so_string);

			final String app_uri_setting = pref_cooperation_url + "autocomplete_shiobe.php?id=";
			final String url1 = app_uri_setting + screenName + "&mode=pre";
			final String url3 = app_uri_setting + screenName + "&mode=tag";

			if (oauthToken.equals("")) {
				pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
				index = adapter.checkIndexFromPrefTwtr();
				init_user(index);
			}

			String text1 = MyCrypt.decrypt(this, oauthToken, HttpsClient.https2data(this, url1, pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset));
			String text3 = MyCrypt.decrypt(this, oauthToken, HttpsClient.https2data(this, url3, pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset));

			if (text1.equals("") == false) {
				final String[] PREFIXS = text1.split(",");
				final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.list_item, PREFIXS);
				editText1.setAdapter(adapter1);
				editText1.setThreshold(1);
			}

			if (text3.equals("") == false) {
				final String[] HASHTAGS = text3.split(",");
				final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.list_item, HASHTAGS);
				editText2.setAdapter(adapter3);
				editText2.setThreshold(1);
			}
		}
	}

	private final String init_user_oauth(final int index) {
		oauthToken = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("oauth_token_" + index, ""));
		if (oauthToken.equals("")) {
			WriteLog.write(this, "oauthToken.equals(\"\")");
			final SharedPreferences.Editor editor = pref_twtr.edit();
			editor.putString("index", "0");
			editor.remove("consumer_key_" + index);
			editor.remove("consumer_secret_" + index);
			editor.remove("oauth_token_" + index);
			editor.remove("oauth_token_secret_" + index);
			editor.remove("profile_image_url_" + index);
			editor.remove("screen_name_" + index);
			editor.remove("status_" + index);
			editor.commit();
		}

		consumerKey = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("consumer_key_" + index, ""));
		consumerSecret = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("consumer_secret_" + index, ""));
		if (consumerKey.equals("") || consumerSecret.equals("")) {
			WriteLog.write(this, "(consumerKey.equals(\"\") || consumerSecret.equals(\"\"))");
			consumerKey = getString(R.string.default_consumerKey);
			consumerSecret = getString(R.string.default_consumerSecret);
		}

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		pref_timeout_t4j_connection = ListAdapter.getPrefInt(this, "pref_timeout_t4j_connection", "20000");
		pref_timeout_t4j_read = ListAdapter.getPrefInt(this, "pref_timeout_t4j_read", "120000");

		oauthTokenSecret = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("oauth_token_secret_" + index, ""));
		try {
			confbuilder = new ConfigurationBuilder();
		} catch (final IllegalStateException e) {
			WriteLog.write(this, e);
		} catch (final Exception e) {
			WriteLog.write(this, e);
		}
		confbuilder.setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read).setHttpRetryCount(3).setHttpRetryIntervalSeconds(10);// .setUseSSL(true);
		Twitter twtr;
		try {
			conf = confbuilder.build();
			twtr = new TwitterFactory(conf).getInstance();
		} catch (final Exception e) {
			twtr = null;
			WriteLog.write(this, e);
			adapter.toast(getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
		}
		WriteLog.write(this, "init_user_oauth(): conf: " + conf.toString());
		WriteLog.write(this, "init_user_oauth(): twtr: " + twtr.toString());

		if (twtr != null) {
			try {
				screenName = twtr.getScreenName();
				User user = twtr.showUser(screenName);
				final String profile_image_url = user.getProfileImageURL().toString();

				pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
				final SharedPreferences.Editor editor = pref_twtr.edit();
				editor.putString("screen_name_" + index, screenName);
				editor.putString("profile_image_url_" + index, profile_image_url);
				editor.commit();

				WriteLog.write(this, "screenName: " + screenName);
				WriteLog.write(this, "profile_image_url: " + profile_image_url);
			} catch (final TwitterException e) {
				WriteLog.write(this, e);
				adapter.toast(getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
			} catch (final Exception e) {
				WriteLog.write(this, e);
				adapter.toast(getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
			}
		}
		return screenName;
	}

	private final void init_user_profimage(final int index) {
		URL url;
		try {
			url = new URL(pref_twtr.getString("profile_image_url_" + index, ""));
		} catch (final MalformedURLException e) {
			url = null;
			WriteLog.write(this, e);
		} catch (final Exception e) {
			url = null;
			WriteLog.write(this, e);
		}
		if (url != null) {
			InputStream input;
			try {
				input = url.openStream();
			} catch (final IOException e) {
				input = null;
				WriteLog.write(this, e);
			}
			if (input != null) {
				Drawable drawable;
				try {
					final Bitmap bitmap = BitmapFactory.decodeStream(input);
					drawable = new BitmapDrawable(bitmap);
				} catch (OutOfMemoryError e) {
					WriteLog.write(UpdateTweetMultiple.this, e);
					System.gc();
					System.runFinalization();
					System.gc();
					toast(getString(R.string.too_large_picture));
					final Bitmap bitmap = BitmapFactory.decodeStream(input);
					drawable = new BitmapDrawable(bitmap);
				} catch (final Exception e) {
					drawable = null;
					WriteLog.write(this, e);
				}
				if (drawable != null) {
					final Drawable finalDrawable = drawable;
					runOnUiThread(new Runnable() {
						@Override
						public final void run() {
							button1.setBackgroundDrawable(finalDrawable);
						}
					});
				}
			}
		}
	}

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}

		simpleauth();

		crpKey = getString(R.string.app_name);
		final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		crpKey += telephonyManager.getDeviceId();
		crpKey += telephonyManager.getSimSerialNumber();
		try {
			final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
			crpKey += Long.toString(packageInfo.firstInstallTime);
		} catch (final NameNotFoundException e) {
			WriteLog.write(this, e);
		}

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		final boolean pref_enable_ringtone_onstart = pref_app.getBoolean("pref_enable_ringtone_onstart", true);
		final String pref_ringtone_onstart_updatetweetmultiple = pref_app.getString("pref_ringtone_onstart_updatetweetmultiple", "");
		if (pref_enable_ringtone_onstart && ( pref_ringtone_onstart_updatetweetmultiple != null ) && ( pref_ringtone_onstart_updatetweetmultiple.equals("") == false )) {
			final MediaPlayer mediaPlayer = MediaPlayer.create(UpdateTweetMultiple.this, Uri.parse(pref_ringtone_onstart_updatetweetmultiple));
			mediaPlayer.setLooping(false);
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
		}

		final int pref_screen_orientation_tweet = ListAdapter.getPrefInt(this, "pref_screen_orientation_tweet", "0");
		switch (pref_screen_orientation_tweet) {
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

		// View
		setContentView(R.layout.tweet_multiple);
		tableLayout1 = (TableLayout) findViewById(R.id.tableLayout1);
		button1 = (Button) findViewById(R.id.button_tweet);
		editText1 = (AutoCompleteTextView) this.findViewById(R.id.editText1);
		editText2 = (AutoCompleteTextView) this.findViewById(R.id.editText2);
		editText3 = (EditText) this.findViewById(R.id.editText3);
		editText3.setFocusable(true);
		editText3.setFocusableInTouchMode(true);
		editText3.requestFocusFromTouch();
		// View

		adapter = new ListAdapter(this, crpKey, null, null);

		final boolean pref_showIcon = pref_app.getBoolean("pref_showIcon", false);
		final boolean pref_showIconWear = pref_app.getBoolean("pref_showIconWear", false);
		if (pref_showIcon == true) {
			adapter.notificationShowIcon(pref_showIconWear);
		}

		// ///////////////////////////////////////////////////////////

		button1.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public final boolean onLongClick(final View v) {
				final int index = adapter.checkIndexFromPrefTwtr();
				screenName = pref_twtr.getString("screen_name_" + index, " - ");
				//try {
				//	if (last_edited_editText == null) {
				//		last_edited_editText = (EditText) UpdateTweetMultiple.this.findViewById(v.getId());
				//	}
				//} catch (final Exception e) {
				//}
				try {
					init_user_oauth(index);
					adapter.toast(getString(R.string.doing_tweet_multiple) + System.getProperty("line.separator") + System.getProperty("line.separator") + "[@" + screenName + "]");
					tweet(Integer.toString(index), screenName, editText1.getText().toString(), editText3.getText().toString(), editText2.getText().toString());
				} catch (final TwitterException e) {
				}
				for (int i = 100; i <= editText_index; i++) {
					WriteLog.write(UpdateTweetMultiple.this, "i: " + i);
					try {
						final EditText editText = (EditText) tableLayout1.findViewById(i);
						if (editText != null) {
							if (editText.getText().toString().equals("") == false) {
								tweet(Integer.toString(index), screenName, editText1.getText().toString(), editText.getText().toString(), editText2.getText().toString());
							}
						}
					} catch (final TwitterException e) {
					}
				}
				return true;
			}
		});

		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				add_editText();
			}
		});

		editText1.addTextChangedListener(new TextWatcher() {
			@Override
			public final void afterTextChanged(final Editable s) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}

			@Override
			public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}
		});

		editText1.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public final void onFocusChange(final View arg0, final boolean arg1) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}
		});

		editText2.addTextChangedListener(new TextWatcher() {
			@Override
			public final void afterTextChanged(final Editable s) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}

			@Override
			public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}
		});

		editText2.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public final void onFocusChange(final View arg0, final boolean arg1) {
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}
		});
		editText2.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public final boolean onLongClick(final View v) {
				pref_app = PreferenceManager.getDefaultSharedPreferences(UpdateTweetMultiple.this);
				int pref_timeout_connection = ListAdapter.getPrefInt(UpdateTweetMultiple.this, "pref_timeout_connection", ListAdapter.default_timeout_connection_string);
				int pref_timeout_so = ListAdapter.getPrefInt(UpdateTweetMultiple.this, "pref_timeout_so", ListAdapter.default_timeout_so_string);
				if (oauthToken.equals("")) {
					pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
					init_user(adapter.checkIndexFromPrefTwtr());
				}
				final String pref_cooperation_url = pref_app.getString("pref_cooperation_url", ListAdapter.default_cooperation_url);
				final String uri_taglist =
						pref_cooperation_url + "setting_list_shiobe.php?id=shiobe4a&tok=" + java.net.URLEncoder.encode(MyCrypt.encrypt(UpdateTweetMultiple.this, oauthToken, oauthToken)) + "&text="
								+ java.net.URLEncoder.encode(MyCrypt.encrypt(UpdateTweetMultiple.this, oauthToken, editText2.getText().toString()));
				final String result_taglist = HttpsClient.https2data(UpdateTweetMultiple.this, uri_taglist, pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset);
				toast( // "ハッシュタグリストを更新しました！"
				// + System.getProperty("line.separator")
				// + " 追加: " + editText3.getText().toString(),
				result_taglist);
				return true;
			}
		});

		editText3.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public final boolean onLongClick(final View v) {
				final int index = adapter.checkIndexFromPrefTwtr();
				screenName = pref_twtr.getString("screen_name_" + index, " - ");
				last_edited_editText = editText3;
				try {
					init_user_oauth(index);
					adapter.toast(getString(R.string.doing_send) + ": " + last_edited_editText.getText().toString() + System.getProperty("line.separator") + System.getProperty("line.separator")
							+ "[@" + screenName + "]");
					tweet(Integer.toString(index), screenName, editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString());
				} catch (final TwitterException e) {
				}
				return true;
			}
		});
		editText3.addTextChangedListener(new TextWatcher() {
			@Override
			public final void afterTextChanged(final Editable s) {
				last_edited_editText = editText3;
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}

			@Override
			public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				last_edited_editText = editText3;
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}
		});
		editText3.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public final void onFocusChange(final View arg0, final boolean arg1) {
				last_edited_editText = editText3;
				tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
			}
		});
		editText3.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public final boolean onKey(final View v, final int keyCode, final KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (( pre_key == KeyEvent.KEYCODE_SHIFT_LEFT ) || ( pre_key == KeyEvent.KEYCODE_SHIFT_RIGHT ) || ( pre_key == KeyEvent.KEYCODE_CTRL_LEFT )
							|| ( pre_key == KeyEvent.KEYCODE_CTRL_RIGHT )) {
						pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
						final int index = adapter.checkIndexFromPrefTwtr();
						screenName = pref_twtr.getString("screen_name_" + index, " - ");
						last_edited_editText = editText3;
						try {
							init_user_oauth(index);
							adapter.toast(getString(R.string.doing_send) + ": " + last_edited_editText.getText().toString() + System.getProperty("line.separator")
									+ System.getProperty("line.separator") + "[@" + screenName + "]");
							tweet(Integer.toString(index), screenName, editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString());
						} catch (final TwitterException e) {
						}
						tweetstrlength(editText1.getText().toString(), last_edited_editText.getText().toString(), editText2.getText().toString(), true);
						pre_key = 0;
						return true;
					}
				}
				if (( keyCode == KeyEvent.KEYCODE_SHIFT_LEFT ) || ( keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT ) || ( keyCode == KeyEvent.KEYCODE_CTRL_LEFT ) || ( keyCode == KeyEvent.KEYCODE_CTRL_RIGHT )) {
					pre_key = keyCode;
				} else {
					pre_key = 0;
				}
				return false;
			}
		});

		// ///////////////////////////////////////////////////////////

		check();

	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, R.string.changeaccount, 0, R.string.changeaccount);
		menu.add(0, R.string.allform, 0, getString(R.string.allform) + getString(R.string.clear));
		menu.add(0, R.string.load, 0, R.string.load);
		menu.add(0, R.string.save, 0, R.string.save);
		menu.add(0, R.string.back, 0, R.string.back);
		menu.add(0, R.string.deljustbefore, 0, R.string.deljustbefore);
		menu.add(0, R.string.settings, 0, R.string.settings);
		menu.add(0, R.string.copyright, 0, R.string.copyright);
		return true;
	}

	@Override
	public final boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		boolean ret = true;
		if (item.getItemId() == R.string.changeaccount) {
			int pref_user_index_size = ListAdapter.getPrefInt(this, "pref_user_index_size", Integer.toString(ListAdapter.default_user_index_size));
			if (pref_user_index_size < ListAdapter.default_user_index_size) {
				pref_user_index_size = ListAdapter.default_user_index_size;
			}

			pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
			final int index = adapter.checkIndexFromPrefTwtr();
			final String[] ITEM1 = new String[pref_user_index_size];
			final String current_name = pref_twtr.getString("screen_name_" + index, "");
			final String current_oauth_token = pref_twtr.getString("oauth_token_" + index, "");
			for (int i = 0; i < pref_user_index_size; i++) {
				final String itemname = pref_twtr.getString("screen_name_" + i, "");
				final String itemname2 = pref_twtr.getString("oauth_token_" + i, "");

				if (itemname.equals("")) {
					ITEM1[i] = "  - ";
				} else if (( itemname.equals(current_name) ) && ( itemname2.equals(current_oauth_token) )) {
					ITEM1[i] = "Current: @" + itemname;
				} else {
					ITEM1[i] = " @" + itemname;
				}
			}
			new AlertDialog.Builder(UpdateTweetMultiple.this).setTitle(R.string.changeaccount).setItems(ITEM1, new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(final DialogInterface dialog, final int which) {
					final SharedPreferences.Editor editor = pref_twtr.edit();
					editor.putString("index", Integer.toString(which));
					editor.commit();
					init_user(which);
					runOnUiThread(new Runnable() {
						@Override
						public final void run() {
							adapter.toast(getString(R.string.userinit) + " @" + screenName);
						}
					});
				}
			}).create().show();

		} else if (item.getItemId() == R.string.allform) {
			try {
				final ViewGroup layout = tableLayout1;
				for (int i = 0; i < layout.getChildCount(); i++) {
					final View childView = layout.getChildAt(i);
					if (childView.getClass().getName().equals("android.widget.TableRow")) {
						for (int j = 0; j < ( (ViewGroup) childView ).getChildCount(); j++) {
							final View childView2 = ( (ViewGroup) childView ).getChildAt(j);
							if (childView2.getClass().getName().equals("android.widget.EditText")) {
								EditText editTxt = (EditText) childView2;
								editTxt.setText("");
							} else if (childView2.getClass().getName().equals("android.widget.AutoCompleteTextView")) {
								AutoCompleteTextView autoCompleteTxtView = (AutoCompleteTextView) childView2;
								autoCompleteTxtView.setText("");
							}
						}
					} else if (childView.getClass().getName().equals("android.widget.EditText")) {
						final EditText editTxt = (EditText) childView;
						editTxt.setText("");
					} else if (childView.getClass().getName().equals("android.widget.AutoCompleteTextView")) {
						final EditText editTxt = (EditText) childView;
						editTxt.setText("");
					}
				}
			} catch (final Exception e) {
			}

		} else if (item.getItemId() == R.string.load) {
			WriteLog.write(this, "load");
			// File dir = new File(getFilesDir() + "/");
			final File dir = new File(Environment.getExternalStorageDirectory() + "/");
			final File[] files = dir.listFiles(new FileCheck(screenName));
			final String[] ITEM = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				WriteLog.write(this, "load files[" + Integer.toString(i) + "]: " + files[i].getName());
				ITEM[i] = ( ( files[i].getName() ).replace(screenName + "_multiplelist_", "") ).replace(".cgi", "");
				WriteLog.write(this, "load ITEM[" + Integer.toString(i) + "]: " + ITEM[i]);
			}

			new AlertDialog.Builder(UpdateTweetMultiple.this).setTitle(R.string.settings_import_export).setItems(ITEM, new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(final DialogInterface dialog, final int which) {
					try {
						multiple_filename = ITEM[which];
						WriteLog.write(UpdateTweetMultiple.this, "load multiple_filename: " + multiple_filename);
						final String filename = screenName + "_multiplelist_" + ITEM[which] + ".cgi";
						WriteLog.write(UpdateTweetMultiple.this, "load filename: " + filename);
						final InputStream is = new FileInputStream(Environment.getExternalStorageDirectory() + "/" + filename);
						final byte[] readBytes = new byte[is.available()];
						is.read(readBytes);
						is.close();
						final String multiple_draft = new String(readBytes);

						WriteLog.write(UpdateTweetMultiple.this, "load multiple_draft:" + multiple_draft);
						final String[] multiple_draft_array = multiple_draft.split("\n");
						if (multiple_draft_array.length > ( 2 + ( editText_index - 100 ) )) {
							for (int h = multiple_draft_array.length - 3; h > 0; h--) {
								WriteLog.write(UpdateTweetMultiple.this, "load add_editText() h:" + Integer.toString(h));
								add_editText();
							}
						}
						if (multiple_draft_array.length > 0) {

							final ArrayList<View> viewArray = new ArrayList<View>(10);
							final ViewGroup layout = tableLayout1;
							for (int i = 0; i < layout.getChildCount(); i++) {
								final View childView = layout.getChildAt(i);
								if (childView.getClass().getName().equals("android.widget.TableRow")) {
									for (int j = 0; j < ( (ViewGroup) childView ).getChildCount(); j++) {
										final View childView2 = ( (ViewGroup) childView ).getChildAt(j);
										if (( childView2.getClass().getName().equals("android.widget.EditText") ) || ( childView2.getClass().getName().equals("android.widget.AutoCompleteTextView") )) {
											viewArray.add(childView2);
										}
									}
								} else if (( childView.getClass().getName().equals("android.widget.EditText") ) || ( childView.getClass().getName().equals("android.widget.AutoCompleteTextView") )) {
									viewArray.add(childView);
								}
							}
							// Collections.sort(viewArray, new EdittextComparator());

							int i = 0;
							for (final View v : viewArray) {
								if (v.getClass().getName().equals("android.widget.EditText")) {
									( (EditText) v ).setText(multiple_draft_array[i]);
								} else {
									( (AutoCompleteTextView) v ).setText(multiple_draft_array[i]);
								}
								i++;
							}
						}
					} catch (final FileNotFoundException e) {
						WriteLog.write(UpdateTweetMultiple.this, e);
					} catch (final IOException e) {
						WriteLog.write(UpdateTweetMultiple.this, e);
					} catch (final Exception e) {
						WriteLog.write(UpdateTweetMultiple.this, e);
					}
				}
			}).create().show();

		} else if (item.getItemId() == R.string.save) {
			WriteLog.write(this, "save");
			final EditText editView1 = new EditText(UpdateTweetMultiple.this);
			editView1.setText(multiple_filename);
			new AlertDialog.Builder(UpdateTweetMultiple.this).setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.save_as).setView(editView1).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					final String filename_short = editView1.getText().toString();
					final String filename = screenName + "_multiplelist_" + editView1.getText().toString() + ".cgi";
					try {
						String writeString = "";

						final ViewGroup layout = tableLayout1;
						for (int i = 0; i < layout.getChildCount(); i++) {
							final View childView = layout.getChildAt(i);
							if (childView.getClass().getName().equals("android.widget.TableRow")) {
								for (int j = 0; j < ( (ViewGroup) childView ).getChildCount(); j++) {
									final View childView2 = ( (ViewGroup) childView ).getChildAt(j);
									if (childView2.getClass().getName().equals("android.widget.EditText")) {
										writeString += ( (EditText) childView2 ).getText() + "\n";
									} else if (childView2.getClass().getName().equals("android.widget.AutoCompleteTextView")) {
										writeString += ( (AutoCompleteTextView) childView2 ).getText() + "\n";
									}
								}
							} else if (childView.getClass().getName().equals("android.widget.EditText")) {
								writeString += ( (EditText) childView ).getText() + "\n";
							} else if (childView.getClass().getName().equals("android.widget.AutoCompleteTextView")) {
								writeString += ( (AutoCompleteTextView) childView ).getText() + "\n";
							}
						}

						if (( writeString.replaceAll("\\n", "") ).equals("")) {
							WriteLog.write(UpdateTweetMultiple.this, "save deleteFile(" + filename_short + ")");
							final File deleteFile = new File(Environment.getExternalStorageDirectory() + "/" + filename);
							if (deleteFile.exists()) {
								deleteFile.delete();
							}
							toast(getString(R.string.done_del) + ": " + filename_short);
							// deleteFile(filename);
						} else {
							writeString = writeString.replaceAll("\\n$", "");
							final OutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + filename);
							os.write(writeString.getBytes());
							os.close();

							WriteLog.write(UpdateTweetMultiple.this, "save filename: " + filename);
							toast(getString(R.string.done_save) + ": " + filename_short);
						}
					} catch (final FileNotFoundException e) {
						WriteLog.write(UpdateTweetMultiple.this, e);
					} catch (final IOException e) {
						WriteLog.write(UpdateTweetMultiple.this, e);
					} catch (final Exception e) {
						WriteLog.write(UpdateTweetMultiple.this, e);
					}
				}
			}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int whichButton) {
				}
			}).show();

		} else if (item.getItemId() == R.string.back) {
			finish();

		} else if (item.getItemId() == R.string.deljustbefore) {
			adapter.deljustbefore(-1);

		} else if (item.getItemId() == R.string.settings) {
			try {
				final Intent intent2 = new Intent();
				intent2.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.Preference");
				startActivity(intent2);
			} catch (final ActivityNotFoundException e) {
			} catch (final Exception e) {
			}

		} else if (item.getItemId() == R.string.copyright) {
			new Thread(new Runnable() {
				@Override
				public final void run() {
					try {
						final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
						toast(getString(R.string.app_name_short) + ": " + getString(R.string.version) + packageInfo.versionName + " (" + packageInfo.versionCode + ")");
					} catch (final NameNotFoundException e) {
					}

					toast(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(UpdateTweetMultiple.this));

					try {
						final Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(ListAdapter.app_uri_about));
						startActivity(intent);
					} catch (final Exception e) {
					}
				}
			}).start();

		}
		return ret;
	}

	@Override
	protected final void onResume() {
		super.onResume();
		( new CheckNetworkUtil(this) ).autoConnect(false);
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
	}

	private final void simpleauth() {
		// Password処理
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		final String pref_appPassword = pref_app.getString("pref_appPassword", "");
		if (pref_appPassword.equals("") == false) {
			WriteLog.write(this, "(pref_appPassword.equals(\"\") == false)");
			final EditText editView = new EditText(UpdateTweetMultiple.this);
			new AlertDialog.Builder(UpdateTweetMultiple.this).setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.enter_password).setView(editView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(final DialogInterface dialog, final int whichButton) {
					if (editView.getText().toString().equals(pref_appPassword) == false) {
						WriteLog.write(UpdateTweetMultiple.this, getString(R.string.wrong_password) + ": " + editView.getText().toString());
						finish();
					}
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(final DialogInterface dialog, final int whichButton) {
					WriteLog.write(UpdateTweetMultiple.this, getString(R.string.cancelled));
					finish();
				}
			}).show();
		}
	}

	private final void toast(final String text) {
		if (!isFinishing()) {
			if (currentThreadIsUiThread()) {
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public final void run() {
						Toast.makeText(UpdateTweetMultiple.this, text, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	private final void tweet(final String index, final String screenName, final String str1, final String str2, final String str3) throws TwitterException {

		last_edited_editText.setEnabled(false);

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		final String tweetstr = StringUtil.getTweetString(adapter.getTweetHeader(pref_app, str1), str2, adapter.getTweetfooter(pref_app, str3));
		if (tweetstr.equals("")) {
			adapter.toast(getString(R.string.empty_tweettext));
			return;
		}

		try {
			adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).updateStatus(new StatusUpdate(tweetstr));
			adapter.toast(getString(R.string.done_tweet) + ": " + tweetstr + System.getProperty("line.separator") + System.getProperty("line.separator") + "[@" + screenName + "]");
			last_edited_editText.setEnabled(true);

			final boolean pref_enable_ringtone_ontweet = pref_app.getBoolean("pref_enable_ringtone_ontweet", true);
			final String pref_ringtone_ontweet_updatetweetmultiple = pref_app.getString("pref_ringtone_ontweet_updatetweetmultiple", "");
			if (pref_enable_ringtone_ontweet && ( pref_ringtone_ontweet_updatetweetmultiple != null ) && ( pref_ringtone_ontweet_updatetweetmultiple.equals("") == false )) {
				final MediaPlayer mediaPlayer = MediaPlayer.create(UpdateTweetMultiple.this, Uri.parse(pref_ringtone_ontweet_updatetweetmultiple));
				mediaPlayer.setLooping(false);
				mediaPlayer.seekTo(0);
				mediaPlayer.start();
			}
		} catch (final TwitterException e) {
			if (e.exceededRateLimitation()) {
				try {
					adapter.getTwitter(adapter.checkIndexFromPrefTwtr(1), false).updateStatus(new StatusUpdate(tweetstr));
					adapter.toast(getString(R.string.done_tweet) + ": " + tweetstr + System.getProperty("line.separator") + System.getProperty("line.separator") + "[@" + screenName + "]");
					last_edited_editText.setEnabled(true);
				} catch (final TwitterException e1) {
				}
			}
			adapter.toast(getString(R.string.cannot_access_twitter));
		} catch (final Exception e) {
			adapter.toast(getString(R.string.exception));
		}
		return;
	}

	private final int tweetstrlength(final String str1, final String str2, final String str3, final boolean ui) {
		final String str = StringUtil.getTweetString(str1, str2, str3);
		final Integer strlength = tweetstrlength2(str).length();

		if (ui) {
			if (strlength > 0) {
				//				runOnUiThread(new Runnable() {
				//					@Override
				//					public final void run() {
				button1.setText(strlength.toString());
				editText1.setHint("");
				last_edited_editText.setHint("");
				editText2.setHint("");
				//					}
				//				});
			} else {
				//				runOnUiThread(new Runnable() {
				//					@Override
				//					public final void run() {
				button1.setText("0");
				editText1.setHint(R.string.prefix);
				editText2.setHint(R.string.suffix);
				//					}
				//				});
			}
		}
		return strlength;
	}

	private final String tweetstrlength2(final String str) {
		final Matcher matcher = convHTTPURLLinkPtn.matcher(str);
		final String str2 = matcher.replaceAll("01234567890123456789");
		final Matcher matcher2 = convHTTPSURLLinkPtn.matcher(str2);
		return matcher2.replaceAll("012345678901234567890");
	}
}
