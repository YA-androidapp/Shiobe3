package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public final class TwitterLoginPin extends Activity {

	public static final String EXTRA_CONSUMER_KEY = "";
	public static final String EXTRA_CONSUMER_SECRET = "";

	private RequestToken mRequestToken;
	private final AsyncTwitterFactory asyncTwitterFactory = new AsyncTwitterFactory();
	private final AsyncTwitter asyncTwitter = asyncTwitterFactory.getInstance();
	private final TwitterListener twitterListener = new TwitterAdapter() {
		@Override
		public final void gotOAuthRequestToken(final RequestToken token) {
			WriteLog.write(TwitterLoginPin.this, "gotOAuthRequestToken(): " + token.getToken());
			mRequestToken = token;
		}

		@Override
		public final void gotOAuthAccessToken(final AccessToken token) {
			final Intent intent = new Intent();
			WriteLog.write(TwitterLoginPin.this, "token.getToken(): " + token.getToken());
			intent.putExtra("oauth_token", token.getToken());
			WriteLog.write(TwitterLoginPin.this, "token.getTokenSecret(): " + token.getTokenSecret());
			intent.putExtra("oauth_verifier", token.getTokenSecret());
			WriteLog.write(TwitterLoginPin.this, "intent: " + intent.toString());
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};

	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WriteLog.write(this, "super.onCreate()");

		setContentView(R.layout.twitterlogin_pin);
		WriteLog.write(this, "setContentView()");

		final Button button1 = (Button) findViewById(R.id.button1);
		final EditText editText1 = (EditText) findViewById(R.id.editText1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public final void onClick(final View v) {
				final String pin = editText1.getText().toString();
				if (pin.equals("") == false) {
					asyncTwitter.getOAuthAccessTokenAsync(mRequestToken, pin);
				}
			}
		});

		final Intent intent = getIntent();
		WriteLog.write(this, "getIntent(): " + getIntent().toString());

		final String auth_url = intent.getStringExtra("auth_url");
		WriteLog.write(this, "auth_url: " + auth_url);
		final String consumer_key = intent.getStringExtra("consumer_key");
		WriteLog.write(this, "consumer_key: " + consumer_key);
		final String consumer_secret = intent.getStringExtra("consumer_secret");
		WriteLog.write(this, "consumer_secret: " + consumer_secret);

		asyncTwitter.addListener(twitterListener);
		asyncTwitter.setOAuthConsumer(consumer_key, consumer_secret);
		asyncTwitter.getOAuthRequestTokenAsync();

		final Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(auth_url));
		WriteLog.write(this, "auth_url: " + auth_url);
		startActivity(intent2);
	}
}
