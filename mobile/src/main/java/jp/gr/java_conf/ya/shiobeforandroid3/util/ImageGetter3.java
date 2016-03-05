package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

// http://stackoverflow.com/questions/7424512/android-html-imagegetter-as-asynctask/7442725#7442725

import java.io.InputStream;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.view.View;

public final class ImageGetter3 implements ImageGetter {
	private Context context;

	private float pref_tl_imagesize = 56.0f;

	private int pref_timeout_connection_imgtag = 60000;
	private int pref_timeout_so_imgtag = 60000;

	private LightingColorFilter lightingColorFilterItemInlineimg;

	/***
	 * Construct the URLImageParser which will execute AsyncTask and refresh the container
	 * @param t
	 * @param c
	 */
	public ImageGetter3(final View t, final Context c, final float pref_tl_imagesize, final LightingColorFilter lightingColorFilterItemInlineimg, final int pref_timeout_connection_imgtag,
			final int pref_timeout_so_imgtag) {
		this.context = c;
		this.pref_tl_imagesize = pref_tl_imagesize;
		this.pref_timeout_connection_imgtag = pref_timeout_connection_imgtag;
		this.pref_timeout_so_imgtag = pref_timeout_so_imgtag;
		this.lightingColorFilterItemInlineimg = lightingColorFilterItemInlineimg;
	}

	public final Drawable getDrawable(final String source) {
		if (( source.equals("favorite") ) || ( source.equals("favorite_hover") ) || ( source.equals("favorite_on") ) || ( source.equals("reply") ) || ( source.equals("reply_hover") )
				|| ( source.equals("retweet") ) || ( source.equals("retweet_hover") ) || ( source.equals("retweet_on") )) {
			try {
				final int id = context.getResources().getIdentifier(source, "drawable", context.getPackageName());
				final Drawable drawable = context.getResources().getDrawable(id);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				return drawable;
			} catch (final Exception e) {
				WriteLog.write(context, e);
			}
			return context.getResources().getDrawable(android.R.drawable.ic_delete);
		} else {
			try {
				Drawable drawable = getDrawableFromWeb(source);
				if (drawable != null) {
					final float m = drawable.getIntrinsicHeight() / pref_tl_imagesize;
					final float w = drawable.getIntrinsicWidth() / m;
					drawable.setBounds(0, 0, (int) Math.ceil(w), (int) Math.ceil(pref_tl_imagesize));
					if (lightingColorFilterItemInlineimg != null) {
						try {
							drawable.setColorFilter(lightingColorFilterItemInlineimg);
						} catch (final Exception e) {
						}
					}
					return drawable;
				}
			} catch (OutOfMemoryError e) {
				WriteLog.write(context, e);
			} catch (final Exception e) {
				WriteLog.write(context, e);
			}
			return context.getResources().getDrawable(android.R.drawable.ic_delete);
		}
	}

	private final Drawable getDrawableFromWeb(String url) {
		// Timeout
		DefaultHttpClient client;
		try {
			final SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			final HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(httpParams, pref_timeout_connection_imgtag);
			HttpConnectionParams.setSoTimeout(httpParams, pref_timeout_so_imgtag);
			client = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schReg), httpParams);
		} catch (final Exception e) {
			WriteLog.write(context, e);
			client = new DefaultHttpClient();
		}

		try {
			final HttpGet method = new HttpGet(url);
			method.setHeader("Connection", "Keep-Alive");
			final HttpResponse response = client.execute(method);
			final int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				final InputStream is = response.getEntity().getContent();
				final Drawable drawable = Drawable.createFromStream(is, "");
				is.close();
				return drawable;
			}
		} catch (final OutOfMemoryError e) {
			WriteLog.write(context, e);
		} catch (final Exception e) {
			WriteLog.write(context, e);
		}
		return null;
	}
}
