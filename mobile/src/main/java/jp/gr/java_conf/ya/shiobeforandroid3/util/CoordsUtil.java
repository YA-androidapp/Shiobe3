package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import jp.gr.java_conf.ya.shiobeforandroid3.ListAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.EditText;

public final class CoordsUtil {
	// Copyright © 2007-2012 やまだらけ http://yamadarake.jp/trdi/report000001.html
	public static final double BESSEL_A = 6377397.155;
	public static final double BESSEL_E2 = 0.00667436061028297;
	public static final double BESSEL_MNUM = 6334832.10663254;

	public static final double GRS80_A = 6378137.000;
	public static final double GRS80_E2 = 0.00669438002301188;
	public static final double GRS80_MNUM = 6335439.32708317;

	public static final double WGS84_A = 6378137.000;
	public static final double WGS84_E2 = 0.00669437999019758;
	public static final double WGS84_MNUM = 6335439.32729246;

	public static final int BESSEL = 0;
	public static final int GRS80 = 1;
	public static final int WGS84 = 2;

	public static final double calcDistHubeny(double lat1, double lng1, double lat2, double lng2) {
		return calcDistHubeny(lat1, lng1, lat2, lng2, WGS84_A, WGS84_E2, WGS84_MNUM);
	}

	public static final double calcDistHubeny(double lat1, double lng1, double lat2, double lng2, double a, double e2, double mnum) {
		final double my = deg2rad(( lat1 + lat2 ) / 2.0);
		final double dy = deg2rad(lat1 - lat2);
		final double dx = deg2rad(lng1 - lng2);

		final double sin = Math.sin(my);
		final double w = Math.sqrt(1.0 - ( e2 * sin * sin ));
		final double m = mnum / ( w * w * w );
		final double n = a / w;

		final double dym = dy * m;
		final double dxncos = dx * n * Math.cos(my);

		return Math.sqrt(( dym * dym ) + ( dxncos * dxncos ));
	}

	public static final double calcDistHubery(double lat1, double lng1, double lat2, double lng2, int type) {
		switch (type) {
		case BESSEL:
			return calcDistHubeny(lat1, lng1, lat2, lng2, BESSEL_A, BESSEL_E2, BESSEL_MNUM);
		case GRS80:
			return calcDistHubeny(lat1, lng1, lat2, lng2, GRS80_A, GRS80_E2, GRS80_MNUM);
		default:
			return calcDistHubeny(lat1, lng1, lat2, lng2, WGS84_A, WGS84_E2, WGS84_MNUM);
		}
	}

	public static final double deg2rad(double deg) {
		return ( deg * Math.PI ) / 180.0;
	}

	// System.out.println("Distance = " + calcDistHubeny(lat1, lng1, lat2, lng2) + " m");

	public static final boolean checkLocationinfoException(final Context context, final String la, final String ln, final String pref_tl_fontcolor_text_updatetweet,
			final String pref_tl_fontcolor_text_updatetweet_over, final EditText editText4, final EditText editText5) {
		if (( la.equals("") == false ) && ( ln.equals("") == false )) {
			SharedPreferences pref_app = PreferenceManager.getDefaultSharedPreferences(context);

			final boolean pref_enable_locationinfo_exception = pref_app.getBoolean("pref_enable_locationinfo_exception", false);

			if (pref_enable_locationinfo_exception) {
				boolean flagLocationinfoException = false;

				try {
					if (( Math.abs(Double.parseDouble(la)) <= 90.0 ) && ( Math.abs(Double.parseDouble(ln)) <= 180.0 )) {

						final double lat = Double.parseDouble(la);
						final double lng = Double.parseDouble(ln);

						final double pref_locationinfo_exception_radius =
								Double.parseDouble(pref_app.getString("pref_locationinfo_exception_radius", ListAdapter.default_locationinfo_exception_radius));

						final String pref_locationinfo_exception_latlng = pref_app.getString("pref_locationinfo_exception_latlng", ListAdapter.default_locationinfo_exception_latlng);
						final String[] pref_locationinfo_exception_latlngs = pref_locationinfo_exception_latlng.split(" ");

						for (final String pref_locationinfo_exception_latlngs_part : pref_locationinfo_exception_latlngs) {
							final double pref_locationinfo_exception_lat = Double.parseDouble(( pref_locationinfo_exception_latlngs_part.split(",") )[0]);
							final double pref_locationinfo_exception_lng = Double.parseDouble(( pref_locationinfo_exception_latlngs_part.split(",") )[1]);

							if (CoordsUtil.calcDistHubeny(pref_locationinfo_exception_lat, pref_locationinfo_exception_lng, lat, lng) < pref_locationinfo_exception_radius) {
								flagLocationinfoException = true;
								break;
							}
						}

						if (flagLocationinfoException) {
							setTextColor(context, pref_tl_fontcolor_text_updatetweet_over, editText4, editText5);
							return true;
						} else {
							setTextColor(context, pref_tl_fontcolor_text_updatetweet, editText4, editText5);
							return false;
						}
					}

				} catch (final NumberFormatException e) {
					WriteLog.write(context, e);
				} catch (final Exception e) {
					WriteLog.write(context, e);
				}
			} else {
				setTextColor(context, pref_tl_fontcolor_text_updatetweet, editText4, editText5);
				return false;
			}
		}
		setTextColor(context, pref_tl_fontcolor_text_updatetweet_over, editText4, editText5);
		return true;
	}

	private static final void setTextColor(final Context context, final String fontcolor, final EditText editText4, final EditText editText5) {
		if (( editText4 != null ) && ( editText5 != null )) {
			try {
				( (Activity) context ).runOnUiThread(new Runnable() {
					@Override
					public final void run() {
						editText4.setTextColor(Color.parseColor(fontcolor));
						editText5.setTextColor(Color.parseColor(fontcolor));
					}
				});
			} catch (Exception e) {
			}
		}
	}
}