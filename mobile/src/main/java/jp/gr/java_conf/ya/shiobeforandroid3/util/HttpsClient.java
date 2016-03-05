package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

//http://www.glamenv-septzen.net/view/981 より

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public final class HttpsClient {
	// String charset = "UTF-8";
	// String charset = "Shift_JIS";
	// String charset = "EUC-JP";

	private final HttpClient httpclient = new DefaultHttpClient();
	private final HttpContext httpcontext = new BasicHttpContext();
	StringBuilder sb;

	HttpsClient(final Context context, final StringBuilder _sb, final int connectionTimeout, final int soTimeout) {
		try {
			final HttpParams par = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(par, connectionTimeout); // 接続のタイムアウト
			HttpConnectionParams.setSoTimeout(par, soTimeout); // データ取得のタイムアウト

			sb = _sb;
			final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			final SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			final Scheme https = new Scheme("https", sf, 443);
			httpclient.getConnectionManager().getSchemeRegistry().register(https);
			httpcontext.setAttribute(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			httpcontext.setAttribute(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			httpcontext.setAttribute(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
			httpcontext.setAttribute(CoreProtocolPNames.HTTP_ELEMENT_CHARSET, HTTP.UTF_8);
			httpcontext.setAttribute(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");

		} catch (final KeyManagementException e) {
			WriteLog.write(context, e);
		} catch (final UnrecoverableKeyException e) {
			WriteLog.write(context, e);
		} catch (final KeyStoreException e) {
			WriteLog.write(context, e);
		} catch (final NoSuchAlgorithmException e) {
			WriteLog.write(context, e);
		} catch (final CertificateException e) {
			WriteLog.write(context, e);
		} catch (final IOException e) {
			WriteLog.write(context, e);
		}
	}

	// HTTPS通信
	public static final String https2data(Context context, String Uri, int timeout_connection, int timeout_so, String charset) {
		final int sbInitSize = 1000000;
		final StringBuilder sb = new StringBuilder(sbInitSize);
		try {
			new HttpsClient(context, sb, timeout_connection, timeout_so).getdata(Uri, charset);
			return sb.toString();
		} catch (final Exception e) {
			WriteLog.write(context, e);
			return "";
		}
	}

	final HttpsClient getdata(String url, String charset) throws Exception {
		HttpEntity entity;
		try {
			final HttpGet httpget = new HttpGet(url);
			final HttpResponse response = httpclient.execute(httpget, httpcontext);
			entity = response.getEntity();
		} catch (final IllegalArgumentException e) {
			entity = null;
			Log.v("S4A HttpsClient", "getdata() IllegalArgumentException: " + e.getMessage());
		} catch (final Exception e) {
			entity = null;
			Log.v("S4A HttpsClient", "getdata() Exception: " + e.getMessage());
		}
		if (entity != null) {
			final String data = EntityUtils.toString(entity, charset);
			sb.append(data);
		}
		return this;
	}
}
