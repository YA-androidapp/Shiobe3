package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.UnsupportedEncodingException;

public final class StringValidator {
	private static final String EUCJP = "EUC_JP";
	private static final String SJIS = "SJIS";
	private static final String UTF8 = "UTF8";
	private static final String JIS_AUTODETECT = "JISAutoDetect";

	private static final boolean checkCharacterCode(final String str, final String encoding) {
		try {
			final byte[] bytes = str.getBytes(encoding);
			return str.equals(new String(bytes, encoding));
		} catch (UnsupportedEncodingException e) {
		}
		return false;
	}

	public static final String detectEncode(final String str) {
		if (str == null) {
			return "";
		} else if (str.equals("")) {
			return "";
		} else {

			if (isUTF8(str)) {
				return UTF8;
			} else if (isSJIS(str)) {
				return SJIS;
			} else if (isEUC(str)) {
				return EUCJP;
			} else {
				return JIS_AUTODETECT;
			}

		}
	}

	private static final boolean isEUC(final String str) {
		return checkCharacterCode(str, EUCJP);
	}

	private static final boolean isSJIS(final String str) {
		return checkCharacterCode(str, SJIS);
	}

	private static final boolean isUTF8(final String str) {
		return checkCharacterCode(str, UTF8);
	}

}
