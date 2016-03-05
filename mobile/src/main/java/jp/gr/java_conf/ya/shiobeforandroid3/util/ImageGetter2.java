package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

// http://stackoverflow.com/questions/7424512/android-html-imagegetter-as-asynctask/7442725#7442725

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.View;

public final class ImageGetter2 implements ImageGetter {
	private Context context;

	private float pref_tl_imagesize = 56.0f;

	View container;

	private final class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
		URLDrawable urlDrawable;

		public ImageGetterAsyncTask(final URLDrawable d) {
			this.urlDrawable = d;
		}

		@Override
		protected Drawable doInBackground(final String... params) {
			final String source = params[0];
			return fetchDrawable(source);
		}

		/***
		 * Get the Drawable from URL
		 * @param urlString
		 * @return
		 */
		private final Drawable fetchDrawable(final String source) {
			if (( source.equals("favorite") ) || ( source.equals("favorite_hover") ) || ( source.equals("favorite_on") ) || ( source.equals("reply") ) || ( source.equals("reply_hover") )
					|| ( source.equals("retweet") ) || ( source.equals("retweet_hover") ) || ( source.equals("retweet_on") )) {
				int id;
				try {
					id = context.getResources().getIdentifier(source, "drawable", context.getPackageName());
				} catch (final Exception e) {
					return context.getResources().getDrawable(android.R.drawable.ic_delete);
				}
				final Drawable drawable = context.getResources().getDrawable(id);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				return drawable;
			} else {
				try {
					final Drawable drawable = getDrawableFromWeb(source);
					if (drawable != null) {
						final float m = drawable.getIntrinsicHeight() / pref_tl_imagesize;
						final float w = drawable.getIntrinsicWidth() / m;
						drawable.setBounds(0, 0, (int) Math.ceil(w), (int) Math.ceil(pref_tl_imagesize));
						return drawable;
					}
				} catch (final OutOfMemoryError e) {
					WriteLog.write(context, e);
				} catch (final Exception e) {
					WriteLog.write(context, e);
				}
				return context.getResources().getDrawable(android.R.drawable.ic_delete);
			}
		}

		private final Drawable getDrawableFromWeb(String url) {
			try {
				final DefaultHttpClient client = new DefaultHttpClient();
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

		@Override
		protected void onPostExecute(final Drawable result) {
			urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight());
			urlDrawable.drawable = result;

			ImageGetter2.this.container.invalidate();
		}
	}

	private final class URLDrawable extends BitmapDrawable {
		protected Drawable drawable = context.getResources().getDrawable(android.R.drawable.ic_delete);

		@Override
		public void draw(final Canvas canvas) {
			if (drawable != null) {
				drawable.draw(canvas);
			}
		}
	}

	/***
	 * Construct the URLImageParser which will execute AsyncTask and refresh the container
	 * @param t
	 * @param c
	 */
	public ImageGetter2(final View t, final Context c, final float pref_tl_imagesize) {
		this.context = c;
		this.container = t;
		this.pref_tl_imagesize = pref_tl_imagesize;
	}

	public final Drawable getDrawable(final String source) {
		final URLDrawable urlDrawable = new URLDrawable();
		final ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);
		asyncTask.execute(source);

		return urlDrawable;
	}
}
