package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import android.widget.Toast;

import jp.gr.java_conf.ya.shiobeforandroid3.ListAdapter;

public final class StringUtil {
	static final int countMatches(final String target, final String str) {
		if (( target.equals("") ) || ( str.equals("") )) {
			return 0;
		}

		int count = 0;
		int pos = 0;
		while (( pos = target.indexOf(str, pos) ) > -1) {
			count++;
			pos += str.length();
		}
		return count;
	}

	public static final String getGeoUriString(final double lat, final double lng, final int zoom) {
		final StringBuilder geoUri = new StringBuilder();
		geoUri.append("geo:");
		geoUri.append(lat);
		geoUri.append(",");
		geoUri.append(lng);
		if (( zoom > 0 ) && ( zoom < 24 )) {
			geoUri.append("?z=");
			geoUri.append(zoom);
		}
		return geoUri.toString();
	}

	public static final String getTweetString(String str1, String str2) {
		if (str1 == null) {
			str1 = "";
		}
		if (str2 == null) {
			str2 = "";
		}
		String tweetstr = str1;
		if (( str1.length() > 0 ) && ( str2.length() > 0 ) && ( !str1.endsWith(" ") ) && ( !str2.startsWith(" ") )) {
			tweetstr += " ";
		}
		tweetstr += str2;
		return tweetstr;
	}

	public static final String getTweetString(String str1, String str2, String str3) {
		if (str1 == null) {
			str1 = "";
		}
		if (str2 == null) {
			str2 = "";
		}
		if (str3 == null) {
			str3 = "";
		}
		String tweetstr = "";
		tweetstr += str1;
		if (( str1.length() > 0 ) && ( str2.length() > 0 ) && ( !str1.endsWith(" ") ) && ( !str2.startsWith(" ") )) {
			tweetstr += ' ';
		}
		tweetstr += str2;
		if (( str2.length() > 0 ) && ( str3.length() > 0 ) && ( !str2.endsWith(" ") ) && ( !str3.startsWith(" ") )) {
			tweetstr += ' ';
		}
		tweetstr += str3;
		return tweetstr;
	}

	public static final String join(final String pre, final String[] array) {
		if (array == null) {
			return "";
		}

		if (array.length < 1) {
			return "";
		}

		final int sbInitSize = array.length * 40;
		// ( array.length * 30 ) * 4/3
		final StringBuilder builder = new StringBuilder(sbInitSize);

		for (final String str : array) {
			builder.append(pre).append(str).append(",");
		}

		return builder.substring(0, ( ( builder.length() > 1 ) ? ( builder.length() - 1 ) : 0 ));
	}

	public static final String uriStringToShortcutName(final String screenName, final String uriString) {
		String shortcutName = uriString.replace(ListAdapter.TWITTER_BASE_URI, "").replace("/lists/", "/");

		if (shortcutName.startsWith("@")) {
			;
		} else if (shortcutName.startsWith("search")) {
			shortcutName = shortcutName.replace("search?q=", "?").replace("search/", "?");
		} else {
			shortcutName = "@" + shortcutName;
		}

		shortcutName = ( screenName.equals("") ? "" : ( ( screenName.startsWith("@") ) ? screenName.substring(1, 2) : screenName.substring(0, 1) ) + ":" ) + shortcutName;
		return shortcutName;
	}

	public static final String uriStringToTag(final String uriString, final boolean addS) {
		final String uriStringLowercase = uriString.toLowerCase(ListAdapter.LOCALE).replace("http://", "https://");

		if (uriStringLowercase.equals("")) {
			return "home";
		} else if (( uriStringLowercase.equals("https://twitter.com") ) || ( uriStringLowercase.equals(ListAdapter.TWITTER_BASE_URI) ) || ( uriStringLowercase.contains("#h") )) {
			if (uriStringLowercase.endsWith("(s)")) {
				return "home" + ( addS ? "(s)" : "" );
			} else {
				return "home";
			}
		} else if (( uriStringLowercase.startsWith("https://twitter.com/mentions") ) || ( uriStringLowercase.contains("#m") )) {
			return "mention";
		} else if (( uriStringLowercase.startsWith("https://twitter.com/favorites") ) || ( uriStringLowercase.contains("#f") )) {
			return "userfav";
		} else if (uriStringLowercase.startsWith("https://twitter.com/search")) {
			if (uriStringLowercase.endsWith("(s)")) {
				return "search" + ( addS ? "(s)" : "" );
			} else {
				return "search";
			}
		} else if (uriStringLowercase.startsWith(ListAdapter.TWITTER_BASE_URI)) {
			if (uriStringLowercase.contains("/status/")) {
				return "status";
			} else if (( uriStringLowercase.contains("/lists/") )) {
				if (uriStringLowercase.endsWith("(s)")) {
					return "userlist" + ( addS ? "(s)" : "" );
				} else {
					return "userlist";
				}
			} else {
				final String uriStringLowercaseReplaced = uriStringLowercase.replace(ListAdapter.TWITTER_BASE_URI, "");
				if (( uriStringLowercaseReplaced.indexOf("/") > 0 ) && ( uriStringLowercaseReplaced.indexOf("/") < ( uriStringLowercaseReplaced.length() - 1 ) )) {
					return "userlist";
				} else if (uriStringLowercase.length() > 20) {
					// ListAdapter.TWITTER_BASE_URI.length() = 20
					return "user";
				}
			}
		}

		if (uriStringLowercase.contains("#h")) {
			if (uriStringLowercase.endsWith("(s)")) {
				return "home" + ( addS ? "(s)" : "" );
			} else {
				return "home";
			}
		} else if (uriStringLowercase.contains("#m")) {
			return "mention";
		} else if (uriStringLowercase.contains("#f")) {
			return "userfav";
		} else if (uriStringLowercase.contains("/")) {
			if (uriStringLowercase.endsWith("(s)")) {
				return "userlist" + ( addS ? "(s)" : "" );
			} else {
				return "userlist";
			}
		} else if (uriStringLowercase.contains("?")) {
			if (uriStringLowercase.endsWith("(s)")) {
				return "search" + ( addS ? "(s)" : "" );
			} else {
				return "search";
			}
		} else {
			return "user";
		}
	}
}
