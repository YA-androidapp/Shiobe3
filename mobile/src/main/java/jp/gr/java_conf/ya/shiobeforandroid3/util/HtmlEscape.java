package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

public final class HtmlEscape {
	public static final String escape(String str) {
		final StringBuffer escapeStr = new StringBuffer(140);

		for (int i = 0; i < str.length(); i++) {
			final char c = str.charAt(i);

			if (c == '<') {
				escapeStr.append("&lt;");
			} else if (c == '>') {
				escapeStr.append("&gt;");
			} else if (c == '&') {
				escapeStr.append("&amp;");
			} else if (c == '"') {
				escapeStr.append("&quot;");
			} else if (c == '\'') {
				escapeStr.append("&apos;");
			} else {
				escapeStr.append(c);
			}
		}
		return escapeStr.toString();
	}
}
