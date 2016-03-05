package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Looper;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public final class TwitterLogin extends Activity {
	private static final String CALLBACK_URL = "myapp://oauth";

	@Override
	protected final void onCreate(final Bundle bundle) {
		super.onCreate(bundle);

		final WebView webView1 = new WebView(this);
		setContentView(webView1);
		final WebSettings webSettings = webView1.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView1.setWebViewClient(new WebViewClient() {

			@Override
			public final void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
				if (errorCode != WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
					WriteLog.write(TwitterLogin.this, "description: " + description);
					toast(getString(R.string.network_error) + ": " + description);
					webView1.stopLoading();
				}
				webView1.clearView();
			}

			@Override
			public final void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
				WriteLog.write(TwitterLogin.this, "error: " + error.toString());
				handler.proceed();
			}

			@Override
			public final void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);

				if (( url != null ) && url.startsWith(CALLBACK_URL)) {
					webView1.clearView();
					final String[] urlParameters = url.split("\\?")[1].split("&");

					String oauthToken = "";
					String oauthVerifier = "";

					if (urlParameters.length < 2) {
						toast(getString(R.string.network_error));
						return;
					}

					if (urlParameters[0].startsWith("oauth_token")) {
						oauthToken = urlParameters[0].split("=")[1];
					} else if (urlParameters[1].startsWith("oauth_token")) {
						oauthToken = urlParameters[1].split("=")[1];
					}

					if (urlParameters[0].startsWith("oauth_verifier")) {
						oauthVerifier = urlParameters[0].split("=")[1];
					} else if (urlParameters[1].startsWith("oauth_verifier")) {
						oauthVerifier = urlParameters[1].split("=")[1];
					}

					final Intent intent = getIntent();
					intent.putExtra("oauth_token", oauthToken);
					intent.putExtra("oauth_verifier", oauthVerifier);

					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});

		webView1.loadUrl(this.getIntent().getExtras().getString("auth_url"));
	}

	private final void toast(final String text) {
		if (!isFinishing()) {
			if (currentThreadIsUiThread()) {
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public final void run() {
						Toast.makeText(TwitterLogin.this, text, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	private static final boolean currentThreadIsUiThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}
}
