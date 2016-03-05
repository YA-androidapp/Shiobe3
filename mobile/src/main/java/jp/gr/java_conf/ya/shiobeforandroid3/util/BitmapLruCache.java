package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {
	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) ( Runtime.getRuntime().maxMemory() / 1024 );
		final int cacheSize = maxMemory / 8;
		return cacheSize;
	}

	public BitmapLruCache() {
		this(getDefaultLruCacheSize());
	}

	public BitmapLruCache(final int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	public final boolean containsKey(final String url) {
		return ( ( getBitmap(url) == null ) ? false : true );
	}

	@Override
	public Bitmap getBitmap(final String url) {
		return get(url);
	}

	@Override
	public void putBitmap(final String url, final Bitmap bitmap) {
		put(url, bitmap);
	}

	@Override
	protected int sizeOf(final String key, final Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}
}