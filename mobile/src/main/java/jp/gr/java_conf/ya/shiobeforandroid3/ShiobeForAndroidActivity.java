package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import jp.gr.java_conf.ya.shiobeforandroid3.util.CheckNetworkUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.HttpsClient;
import jp.gr.java_conf.ya.shiobeforandroid3.util.MyCrypt;
import jp.gr.java_conf.ya.shiobeforandroid3.util.StringUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ShiobeForAndroidActivity extends AppCompatActivity {
    private ListAdapter adapter;

    private boolean timeout = true;

    private final CheckNetworkUtil checkNetworkUtil = new CheckNetworkUtil(this);

    private int pref_timeout_connection = 0;
    private int pref_timeout_so = 0;
    private int pref_timeout_t4j_connection;
    private int pref_timeout_t4j_read;

    private OAuthAuthorization oAuthAuthorization;

    private RequestToken requestToken;

    private SharedPreferences pref_app;
    private SharedPreferences pref_twtr;

    private static final String CALLBACK_URL = "myapp://oauth";
    private static String consumerKey = "";
    private static String consumerSecret = "";
    private String crpKey = "";
    private String Status;

    private TextView textView1;

    private Twitter twitter;

    private WebView webView1;

    private final void connectTwitter() throws TwitterException {
        if (checkNetworkUtil.isConnected() == false) {
            adapter.toast(getString(R.string.cannot_access_internet));
            return;
        }

        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final String pref_consumerKey = pref_app.getString("pref_consumerkey", "");
        final String pref_consumerSecret = pref_app.getString("pref_consumersecret", "");
        final Boolean pref_enable_consumerKey = pref_app.getBoolean("pref_enable_consumerkey", false);
        if ((pref_enable_consumerKey == true) && (pref_consumerKey.equals("") == false) && (pref_consumerSecret.equals("") == false)) {
            consumerKey = pref_consumerKey.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
            consumerSecret = pref_consumerSecret.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
        } else {
            consumerKey = getString(R.string.default_consumerKey);
            consumerSecret = getString(R.string.default_consumerSecret);
            adapter.toast(getString(R.string.consumerkey_default));
        }

        if (pref_consumerKey.equals("") || pref_consumerSecret.equals("")) {
            adapter.toast(getString(R.string.consumerkey_orand_secret_is_empty));

            pref_app = PreferenceManager.getDefaultSharedPreferences(this);
            final EditText editText = new EditText(this);
            if ((pref_consumerKey.equals("") == false) && (pref_consumerSecret.equals("") == false)) {
                editText.setText(pref_consumerKey + " " + pref_consumerSecret);
            } else if ((pref_consumerKey.equals("") == false) && (pref_consumerSecret.equals(""))) {
                editText.setText(pref_consumerKey + " " + getString(R.string.default_consumerSecret));
            } else if ((pref_consumerKey.equals("")) && (pref_consumerSecret.equals("") == false)) {
                editText.setText(getString(R.string.default_consumerKey) + " " + pref_consumerSecret);
            } else {
                editText.setText(getString(R.string.default_consumerKey) + " " + getString(R.string.default_consumerSecret));
            }
            new AlertDialog.Builder(this).setTitle(R.string.consumerkey_orand_secret_is_empty).setMessage(R.string.enter_consumerkey_and_secret_ssv).setView(editText).setCancelable(true).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    final String[] consumerKeyAndSecret = (editText.getText().toString()).split(" ");
                    if (consumerKeyAndSecret.length == 2) {
                        final SharedPreferences.Editor editor = pref_app.edit();
                        editor.putBoolean("pref_enable_consumerkey", true);
                        editor.putString("pref_consumerkey", (consumerKeyAndSecret[0]));
                        editor.putString("pref_consumersecret", (consumerKeyAndSecret[1]));
                        editor.commit();

                        consumerKey = consumerKeyAndSecret[0];
                        consumerSecret = consumerKeyAndSecret[1];

                        try {
                            connectTwitter();
                        } catch (TwitterException e) {
                            WriteLog.write(ShiobeForAndroidActivity.this, e);
                        }
                    }
                }
            }).create().show();

        } else {

            pref_timeout_t4j_connection = ListAdapter.getPrefInt(this, "pref_timeout_t4j_connection", "20000");
            pref_timeout_t4j_read = ListAdapter.getPrefInt(this, "pref_timeout_t4j_read", "120000");

            final String pref_twitterlogin_mode = pref_app.getString("pref_twitterlogin_mode", "0");
            WriteLog.write(this, "pref_twitterlogin_mode: " + pref_twitterlogin_mode);

            if (pref_twitterlogin_mode.equals("1")) {
                final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
                confbuilder.setOAuthConsumerKey(consumerKey);
                confbuilder.setOAuthConsumerSecret(consumerSecret);
                final Configuration conf = confbuilder.build();
                oAuthAuthorization = new OAuthAuthorization(conf);
                oAuthAuthorization.setOAuthAccessToken(null);
                String authUrl = null;
                try {
                    authUrl = oAuthAuthorization.getOAuthRequestToken(CALLBACK_URL).getAuthorizationURL();
                } catch (final Exception e) {
                    return;
                }
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
                startActivity(intent);

                //			} else if (pref_twitterlogin_mode.equals("2")) {
                //				final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
                //				confbuilder.setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read);// .setUseSSL(true);
                //				twitter = new TwitterFactory(confbuilder.build()).getInstance();
                //				try {
                //					requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
                //				} catch (final TwitterException e) {
                //					WriteLog.write(this, e);
                //				} catch (final Exception e) {
                //					WriteLog.write(this, e);
                //				}
                //				if (requestToken != null) {
                //					String authorizationUrl = "";
                //					try {
                //						authorizationUrl = requestToken.getAuthorizationURL();
                //					} catch (final Exception e) {
                //					}
                //					if (authorizationUrl.equals("") == false) {
                //						final Intent intent = new Intent(this, TwitterLoginPin.class);
                //						intent.putExtra("auth_url", authorizationUrl);
                //						intent.putExtra("consumer_key", consumerKey);
                //						intent.putExtra("consumer_secret", consumerSecret);
                //						startActivityForResult(intent, 0);
                //					}
                //				}

            } else {
                ConfigurationBuilder confbuilder = new ConfigurationBuilder();
                confbuilder.setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read).setHttpRetryCount(3).setHttpRetryIntervalSeconds(10);// .setUseSSL(true);
                twitter = new TwitterFactory(confbuilder.build()).getInstance();
                try {
                    requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
                } catch (final TwitterException e) {
                    adapter.twitterException(e);
                } catch (final Exception e) {
                    WriteLog.write(this, e);
                }
                if (requestToken != null) {
                    String authorizationUrl = "";
                    try {
                        authorizationUrl = requestToken.getAuthorizationURL();
                    } catch (final Exception e) {
                        WriteLog.write(this, e);
                    }
                    if (authorizationUrl.equals("") == false) {
                        final Intent intent = new Intent(this, TwitterLogin.class);
                        intent.putExtra("auth_url", authorizationUrl);
                        this.startActivityForResult(intent, 0);
                    }
                }
            }
        }
    }

    private final void disconnectTwitter() {
        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
        final int index = Integer.parseInt(pref_twtr.getString("index", "-1"));

        if (index > -1) {
            final SharedPreferences.Editor editor = pref_twtr.edit();
            editor.remove("consumer_key_" + Integer.toString(index));
            editor.remove("consumer_secret_" + Integer.toString(index));
            editor.remove("oauth_token_" + Integer.toString(index));
            editor.remove("oauth_token_secret_" + Integer.toString(index));
            editor.remove("profile_image_url_" + Integer.toString(index));
            editor.remove("screen_name_" + Integer.toString(index));
            editor.remove("status_" + Integer.toString(index));
            editor.commit();
            // finish();
        }

        WriteLog.write(this, "disconnected.");
    }

    private final void download(final String apkurl) {
        if (checkNetworkUtil.isConnected() == false) {
            adapter.toast(getString(R.string.cannot_access_internet));
            return;
        }

        HttpURLConnection c;
        try {
            final URL url = new URL(apkurl);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
        } catch (final MalformedURLException e) {
            c = null;
        } catch (final IOException e) {
            c = null;
        }
        try {
            final String PATH = Environment.getExternalStorageDirectory() + "/";
            final File file = new File(PATH);
            file.mkdirs();
            final File outputFile = new File(file, "ShiobeForAndroid.apk");
            final FileOutputStream fos = new FileOutputStream(outputFile);
            final InputStream is = c.getInputStream();
            final byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();

            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH + "ShiobeForAndroid.apk")), "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (final MalformedURLException e) {
        } catch (final IOException e) {
        }
    }

    private final boolean isConnected(final String shiobeStatus) {
        if ((shiobeStatus != null) && shiobeStatus.equals("available")) {
            return true;
        } else {
            return false;
        }
    }

    private final void makeShortcuts() {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final String pref_shiobeforandroidactivity_make_shortcut_urls = pref_app.getString("pref_shiobeforandroidactivity_make_shortcut_urls", "");

        if (pref_shiobeforandroidactivity_make_shortcut_urls.equals("")) {
            adapter.toast(getString(R.string.shiobeforandroidactivity_make_shortcut_urls_is_empty));

            final String[] tlAutoCompleteStringArray = adapter.getTlAutoCompleteStringArray();
                    final AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(ShiobeForAndroidActivity.this);
                    autoCompleteTextView.setText("");
                    final ArrayAdapter<String> autoCompleteTextViewAdapter = new ArrayAdapter<String>(ShiobeForAndroidActivity.this, R.layout.list_item, tlAutoCompleteStringArray);
                    autoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
                    new AlertDialog.Builder(ShiobeForAndroidActivity.this)
                            .setView(autoCompleteTextView)
                            .setTitle(R.string.enter_tl_uri)
                            .setPositiveButton(R.string.load, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                            final String[] urls = (autoCompleteTextView.getText().toString()).split(",");
                            for (final String url : urls) {
                                if (url.startsWith(ListAdapter.TWITTER_BASE_URI)) {
                                    adapter.makeShortcutTl("", url, url.toLowerCase(ListAdapter.LOCALE).endsWith("(s)"));
                                } else {
                                    adapter.makeShortcutUri(url);
                                }
                            }
                        }
                    }).create().show();
        } else {
            final String[] urls = pref_shiobeforandroidactivity_make_shortcut_urls.split(",");
            for (final String url : urls) {
                if (url.startsWith(ListAdapter.TWITTER_BASE_URI)) {
                    adapter.makeShortcutTl("", url, url.toLowerCase(ListAdapter.LOCALE).endsWith("(s)"));
                } else {
                    adapter.makeShortcutUri(url);
                }
            }
        }
    }

    @Override
    protected final void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                if (checkNetworkUtil.isConnected() == false) {
                    adapter.toast(getString(R.string.cannot_access_internet));
                    return;
                }

                try {
                    pref_app = PreferenceManager.getDefaultSharedPreferences(this);
                    final String pref_consumerKey = pref_app.getString("pref_consumerkey", "");
                    final String pref_consumerSecret = pref_app.getString("pref_consumersecret", "");
                    final Boolean pref_enable_consumerKey = pref_app.getBoolean("pref_enable_consumerkey", false);
                    if ((pref_enable_consumerKey == true) && (pref_consumerKey.equals("") == false) && (pref_consumerSecret.equals("") == false)) {
                        consumerKey = pref_consumerKey.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
                        consumerSecret = pref_consumerSecret.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
                    } else {
                        consumerKey = getString(R.string.default_consumerKey);
                        consumerSecret = getString(R.string.default_consumerSecret);
                    }

                    final AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, intent.getExtras().getString("oauth_verifier"));
                    final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
                    confbuilder.setOAuthAccessToken(accessToken.getToken()).setOAuthAccessTokenSecret(accessToken.getTokenSecret()).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read).setHttpRetryCount(3).setHttpRetryIntervalSeconds(10);// .setUseSSL(true);
                    twitter = new TwitterFactory(confbuilder.build()).getInstance();
                    final User user = twitter.showUser(twitter.getScreenName());
                    final String profile_image_url = user.getProfileImageURL().toString();

                    pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
                    final int index = Integer.parseInt(pref_twtr.getString("index", "-1"));

                    if (index > -1) {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("consumer_key_" + Integer.toString(index), MyCrypt.encrypt(this, crpKey, consumerKey));
                        editor.putString("consumer_secret_" + Integer.toString(index), MyCrypt.encrypt(this, crpKey, consumerSecret));
                        editor.putString("oauth_token_" + Integer.toString(index), MyCrypt.encrypt(this, crpKey, accessToken.getToken()));
                        editor.putString("oauth_token_secret_" + Integer.toString(index), MyCrypt.encrypt(this, crpKey, accessToken.getTokenSecret()));
                        editor.putString("status_" + Integer.toString(index), "available");
                        editor.putString("screen_name_" + Integer.toString(index), twitter.getScreenName());
                        editor.putString("profile_image_url_" + Integer.toString(index), profile_image_url);
                        editor.commit();
                        Intent intent2 = new Intent(this, UpdateTweet.class);
                        this.startActivityForResult(intent2, 0);
                    }
                } catch (final TwitterException e) {
                }
            }
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
        final String pref_ringtone_onstart_shiobeforandroidactivity = pref_app.getString("pref_ringtone_onstart_shiobeforandroidactivity", "");
        if (pref_enable_ringtone_onstart && (pref_ringtone_onstart_shiobeforandroidactivity != null) && (pref_ringtone_onstart_shiobeforandroidactivity.equals("") == false)) {
            final MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(pref_ringtone_onstart_shiobeforandroidactivity));
            mediaPlayer.setLooping(false);
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }

        pref_timeout_connection = ListAdapter.getPrefInt(this, "pref_timeout_connection", Integer.toString(ListAdapter.default_timeout_connection));
        pref_timeout_so = ListAdapter.getPrefInt(this, "pref_timeout_so", Integer.toString(ListAdapter.default_timeout_so));
        pref_timeout_t4j_connection = ListAdapter.getPrefInt(this, "pref_timeout_t4j_connection", "20000");
        pref_timeout_t4j_read = ListAdapter.getPrefInt(this, "pref_timeout_t4j_read", "120000");

        if (checkNetworkUtil.isConnected() == false) {
            adapter.toast(getString(R.string.cannot_access_internet));
        } else {
            final Boolean pref_enable_update_check = pref_app.getBoolean("pref_enable_update_check", false);
            if (pref_enable_update_check) {
                adapter.toast(getString(R.string.update_check_developer_ver_only));

                String pref_update_check_url = pref_app.getString("pref_update_check_url", ListAdapter.default_update_check_url);
                pref_update_check_url += (pref_update_check_url.endsWith("/")) ? "" : "/";
                final String updateVerStr = HttpsClient.https2data(this, pref_update_check_url + "index.php?mode=updatecheck", pref_timeout_connection, pref_timeout_so, "UTF-8");
                if (updateVerStr.equals("") == false) {
                    long updateVer = 0;
                    try {
                        updateVer = Long.parseLong(updateVerStr);
                    } catch (final NumberFormatException e) {
                    }
                    try {
                        final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
                        if (updateVer > (packageInfo.lastUpdateTime / 1000L)) {
                            final File deleteFile = new File(Environment.getExternalStorageDirectory() + "/ShiobeForAndroid.apk");
                            if (deleteFile.exists()) {
                                deleteFile.delete();
                            }

                            download(HttpsClient.https2data(this, pref_update_check_url + "index.php?mode=updateuri", pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset));
                        }
                    } catch (NameNotFoundException e) {
                    }
                }
            }
        }

        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addAccount();
            }
        });

        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);

        int index = -1;
        try {
            index = Integer.parseInt(pref_twtr.getString("index", "0"));
        } catch (final Exception e) {
            index = 0;
        }
        Status = pref_twtr.getString("status_" + Integer.toString(index), "");

        final long pref_timeout_connection2 = pref_timeout_connection / 5;

        final String pref_useragent = pref_app.getString("pref_useragent", getString(R.string.useragent_ff));

        textView1 = (TextView) this.findViewById(R.id.textView1);
        webView1 = (WebView) this.findViewById(R.id.webView1);

        new Thread(new Runnable() {
            @Override
            public final void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        if (isConnected(Status)) {
                            textView1.setText(getString(R.string.welcome));
                        } else {
                            textView1.setText(getString(R.string.hello));
                        }

                        webView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        webView1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                        webView1.getSettings().setBuiltInZoomControls(false);
                        webView1.getSettings().setJavaScriptEnabled(true);
                        if (checkNetworkUtil.isConnected() == false) {
                            adapter.toast(getString(R.string.cannot_access_internet));

                            new Thread(new Runnable() {
                                @Override
                                public final void run() {
                                    runOnUiThread(new Runnable() {
                                        public final void run() {
                                            webView1.loadUrl(ListAdapter.app_uri_local);
                                            webView1.requestFocus(View.FOCUS_DOWN);
                                        }
                                    });
                                }
                            });
                        } else {
                            if (pref_useragent.equals("")) {
                                webView1.getSettings().setUserAgentString(getString(R.string.useragent_ff));
                            } else {
                                webView1.getSettings().setUserAgentString(pref_useragent);
                            }
                            webView1.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    timeout = false;
                                }

                                @Override
                                public final void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public final void run() {
                                            try {
                                                Thread.sleep(pref_timeout_connection2);
                                            } catch (final InterruptedException e) {
                                            }
                                            if (timeout) {
                                                WriteLog.write(ShiobeForAndroidActivity.this, getString(R.string.timeout));
                                                if (url.startsWith(ListAdapter.app_uri_about)) {
                                                    runOnUiThread(new Runnable() {
                                                        public final void run() {
                                                            webView1.stopLoading();
                                                            webView1.loadUrl(ListAdapter.app_uri_local);
                                                            webView1.requestFocus(View.FOCUS_DOWN);
                                                        }
                                                    });
                                                    //											} else if (url.equals(ListAdapter.app_uri_local) == false) {
                                                    //												adapter.toast(getString(R.string.timeout));
                                                }
                                            }
                                        }
                                    }).start();
                                }

                                @Override
                                public final void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                                    WriteLog.write(ShiobeForAndroidActivity.this, getString(R.string.description) + ": " + description);

                                    //								if (failingUrl.startsWith(ListAdapter.app_uri_about)) {
                                    //									webView1.stopLoading();
                                    //									webView1.loadUrl(ListAdapter.app_uri_local);
                                    //									webView1.requestFocus(View.FOCUS_DOWN);
                                    //								} else if (failingUrl.equals(ListAdapter.app_uri_local) == false) {
                                    //									WriteLog.write(ShiobeForAndroidActivity.this, "errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
                                    //									adapter.toast("errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
                                    //								}
                                }

                                @Override
                                public final void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
                                    handler.proceed();
                                }
                            });
                            webView1.loadUrl(ListAdapter.app_uri_local);
                            webView1.requestFocus(View.FOCUS_DOWN);
                        }
                    }
                });
            }
        }).start();
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
    public final boolean onCreateOptionsMenu(final Menu menu) {
        // // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        // return true;

        menu.add(0, R.string.addaccount, 0, R.string.addaccount).setIcon(android.R.drawable.ic_input_add);

        final SubMenu sub0 = menu.addSubMenu(getString(R.string.tweet) + "...").setIcon(android.R.drawable.ic_menu_edit);
        sub0.add(0, R.string.app_name_update, 0, R.string.app_name_update);
        sub0.add(0, R.string.app_name_updatedrive, 0, R.string.app_name_updatedrive);
        sub0.add(0, R.string.app_name_updatemultiple, 0, R.string.app_name_updatemultiple);

        final SubMenu sub1 = menu.addSubMenu(getString(R.string.timeline) + "...").setIcon(android.R.drawable.ic_menu_view);
        sub1.add(0, R.string.app_name_tabsactivity, 0, R.string.app_name_tabsactivity);
        sub1.add(0, R.string.home, 0, R.string.home);
        sub1.add(0, R.string.mention, 0, R.string.mention);
        sub1.add(0, R.string.user, 0, R.string.user);
        sub1.add(0, R.string.userfav, 0, R.string.userfav);

        final SubMenu sub2 = menu.addSubMenu(R.string.copyright).setIcon(android.R.drawable.ic_menu_info_details);
        sub2.add(0, R.string.check_ratelimit, 0, R.string.check_ratelimit).setIcon(android.R.drawable.stat_sys_download);
        sub2.add(0, R.string.check_apistatus, 0, R.string.check_apistatus).setIcon(android.R.drawable.stat_sys_download);
        sub2.add(0, R.string.make_shortcut, 0, R.string.make_shortcut).setIcon(android.R.drawable.ic_menu_add);

        menu.add(0, R.string.quit, 0, R.string.quit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    @Override
    public final boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView1.canGoBack()) {
                webView1.goBack();
                return true;
            } else {
                // this.moveTaskToBack(true);
                finish();
                return false;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public final void onNewIntent(final Intent intent) {
        if (checkNetworkUtil.isConnected() == false) {
            adapter.toast(getString(R.string.cannot_access_internet));
            return;
        }

        final Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
        final String verifier = uri.getQueryParameter("oauth_verifier");
        if (verifier.equals("") == false) {
            try {
                pref_app = PreferenceManager.getDefaultSharedPreferences(this);
                final String pref_consumerKey = pref_app.getString("pref_consumerkey", "");
                final String pref_consumerSecret = pref_app.getString("pref_consumersecret", "");
                final Boolean pref_enable_consumerKey = pref_app.getBoolean("pref_enable_consumerkey", false);
                if ((pref_enable_consumerKey == true) && (pref_consumerKey.equals("") == false) && (pref_consumerSecret.equals("") == false)) {
                    consumerKey = pref_consumerKey.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
                    consumerSecret = pref_consumerSecret.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
                } else {
                    consumerKey = getString(R.string.default_consumerKey);
                    consumerSecret = getString(R.string.default_consumerSecret);
                }
                pref_timeout_t4j_connection = ListAdapter.getPrefInt(this, "pref_timeout_t4j_connection", "20000");
                pref_timeout_t4j_read = ListAdapter.getPrefInt(this, "pref_timeout_t4j_read", "120000");

                AccessToken accessToken = null;
                try {
                    accessToken = oAuthAuthorization.getOAuthAccessToken(verifier);
                    final ConfigurationBuilder cbuilder = new ConfigurationBuilder();
                    cbuilder.setOAuthConsumerKey(consumerKey);
                    cbuilder.setOAuthConsumerSecret(consumerSecret);
                    cbuilder.setOAuthAccessToken(accessToken.getToken());
                    cbuilder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());
                    final Configuration configuration = cbuilder.build();
                    final TwitterFactory twitterFactory = new TwitterFactory(configuration);
                    twitter = twitterFactory.getInstance();
                } catch (final Exception e) {
                    WriteLog.write(this, e);
                }

                String profile_image_url = "";
                if (twitter != null) {
                    try {
                        final User user = twitter.showUser(twitter.getScreenName());
                        profile_image_url = user.getProfileImageURL().toString();
                    } catch (final IllegalStateException e) {
                        WriteLog.write(this, e);
                    } catch (final Exception e) {
                        WriteLog.write(this, e);
                    }
                }

                pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
                final int index = Integer.parseInt(pref_twtr.getString("index", "-1"));

                if (index > -1) {
                    if (accessToken != null) {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("consumer_key_" + Integer.toString(index), MyCrypt.encrypt(this, crpKey, consumerKey));
                        editor.putString("consumer_secret_" + Integer.toString(index), MyCrypt.encrypt(this, crpKey, consumerSecret));
                        editor.putString("oauth_token_" + Integer.toString(index), MyCrypt.encrypt(this, crpKey, accessToken.getToken()));
                        editor.putString("oauth_token_secret_" + Integer.toString(index), MyCrypt.encrypt(this, crpKey, accessToken.getTokenSecret()));
                        editor.putString("status_" + Integer.toString(index), "available");
                        editor.putString("screen_name_" + Integer.toString(index), twitter.getScreenName());
                        editor.putString("profile_image_url_" + Integer.toString(index), profile_image_url);
                        editor.commit();

                        final Intent intent2 = new Intent(this, UpdateTweet.class);
                        this.startActivityForResult(intent2, 0);
                    }
                }
            } catch (final TwitterException e) {
            }
        }
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        // // Handle action bar item clicks here. The action bar will
        // // automatically handle clicks on the Home/Up button, so long
        // // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();
        // //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //     return true;
        // }
        // return super.onOptionsItemSelected(item);

        boolean ret = true;

        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
        final int user_index_size = ListAdapter.getPrefInt(this, "pref_user_index_size", Integer.toString(ListAdapter.default_user_index_size));
        final String[] ITEM1 = new String[user_index_size + 1];
        final String[] ITEM2 = new String[user_index_size];
        int account_num = 0;
        for (int i = 0; i < user_index_size; i++) {
            final String itemname = pref_twtr.getString("screen_name_" + i, "");
            account_num += itemname.equals("") ? 0 : 1;
            ITEM1[i + 1] = itemname.equals("") ? " - " : "@" + itemname;
            ITEM2[i] = itemname.equals("") ? " - " : "@" + itemname;
        }
        ITEM1[0] = (account_num > 0) ? getString(R.string.current_account) : getString(R.string.addaccountplease);

        int idx = -1;
        try {
            idx = Integer.parseInt(pref_twtr.getString("index", "0"));
        } catch (final Exception e) {
            idx = 0;
        }
        final int index = idx;

        if (item.getItemId() == R.string.app_name_update) {
            new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        Status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(Status)) {
                            startUpdateTweet();
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        Status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(Status)) {
                            startUpdateTweet();
                        } else {
                            try {
                                connectTwitter();
                            } catch (final TwitterException e) {
                            }
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.app_name_updatedrive) {
            new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        Status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(Status)) {
                            startUpdateTweetDrive();
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        Status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(Status)) {
                            startUpdateTweetDrive();
                        } else {
                            try {
                                connectTwitter();
                            } catch (final TwitterException e) {
                            }
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.app_name_updatemultiple) {
            new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        Status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(Status)) {
                            startUpdateTweetMultiple();
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        Status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(Status)) {
                            startUpdateTweetMultiple();
                        } else {
                            try {
                                connectTwitter();
                            } catch (final TwitterException e) {
                            }
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.app_name_tabsactivity) {
            new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        Status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(Status)) {
                            final Intent intent1 = new Intent();
                            intent1.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.TabsActivity");
                            startActivity(intent1);
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        Status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(Status)) {
                            final Intent intent1 = new Intent();
                            intent1.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.TabsActivity");
                            startActivity(intent1);
                        } else {
                            try {
                                connectTwitter();
                            } catch (final TwitterException e) {
                            }
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.home) {
            new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        Status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(Status)) {
                            adapter.startTlHome(index);
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        Status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(Status)) {
                            adapter.startTlHome(which - 1);
                        } else {
                            try {
                                connectTwitter();
                            } catch (final TwitterException e) {
                            }
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.mention) {
            new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        Status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(Status)) {
                            adapter.startTlMention(index);
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        Status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(Status)) {
                            adapter.startTlMention(which - 1);
                        } else {
                            try {
                                connectTwitter();
                            } catch (final TwitterException e) {
                            }
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.user) {
            new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        Status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(Status)) {
                            adapter.startTlUser(index);
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        Status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(Status)) {
                            adapter.startTlUser(which - 1);
                        } else {
                            try {
                                connectTwitter();
                            } catch (final TwitterException e) {
                            }
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.userfav) {
            new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        Status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(Status)) {
                            adapter.startTlFavorite(index);
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        Status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(Status)) {
                            adapter.startTlFavorite(which - 1);
                        } else {
                            try {
                                connectTwitter();
                            } catch (final TwitterException e) {
                            }
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.addaccount) {
            addAccount();

        } else if (item.getItemId() == R.string.check_ratelimit) {
            adapter.showRateLimits(webView1);

        } else if (item.getItemId() == R.string.check_apistatus) {
            adapter.showApiStatuses(webView1);

        } else if (item.getItemId() == R.string.deljustbefore) {
            adapter.deljustbefore(-1);
        } else if (item.getItemId() == R.string.copyright) {
            try {
                final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
                adapter.toast(getString(R.string.app_name_short) + ": " + getString(R.string.version) + packageInfo.versionName + " (" + packageInfo.versionCode + ")");
            } catch (final NameNotFoundException e) {
            }

            try {
                webView1.loadUrl(ListAdapter.app_uri_about + "?id=" + StringUtil.join("_", ListAdapter.getPhoneIds()) + "&note=" + StringUtil.join("__", adapter.getOurScreenNames()));
                webView1.requestFocus(View.FOCUS_DOWN);
            } catch (final Exception e) {
            }

        } else if (item.getItemId() == R.string.make_shortcut) {
            makeShortcuts();

        } else if (item.getItemId() == R.string.quit) {
            adapter.cancelNotification(ListAdapter.NOTIFY_RUNNING);
            finish();

        }
        return ret;
    }

    private final void addAccount() {
        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
        final int user_index_size = ListAdapter.getPrefInt(this, "pref_user_index_size", Integer.toString(ListAdapter.default_user_index_size));
        final String[] ITEM2 = new String[user_index_size];
        for (int i = 0; i < user_index_size; i++) {
            final String itemname = pref_twtr.getString("screen_name_" + i, "");
            ITEM2[i] = itemname.equals("") ? " - " : "@" + itemname;
        }

        new AlertDialog.Builder(ShiobeForAndroidActivity.this).setTitle(R.string.addaccount2).setItems(ITEM2, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                try {
                    pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref_twtr.edit();
                    editor.putString("index", Integer.toString(which));
                    editor.commit();
                } catch (final Exception e) {
                }
                try {
                    disconnectTwitter();
                } catch (final Exception e) {
                }
                ITEM2[which] = " - ";
                try {
                    connectTwitter();
                } catch (final TwitterException e) {
                }

            }
        }).create().show();
    }

//    <string name="app_name_update">Shiobe3 投稿画面</string>
//    <string name="app_name_updatedrive">Shiobe3 現在地投稿</string>
//    <string name="app_name_updatemultiple">Shiobe3 連続投稿</string>

    private final void startUpdateTweet() {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.UpdateTweet");
        // intent.putExtra("str2", "hoge");
        startActivityForResult(intent, 0);
    }

    private final void startUpdateTweetDrive() {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.UpdateTweetDrive");
        // intent.putExtra("str2", "hoge");
        startActivityForResult(intent, 0);
    }

    private final void startUpdateTweetMultiple() {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.UpdateTweetMultiple");
        // intent.putExtra("str2", "hoge");
        startActivityForResult(intent, 0);
    }

}
