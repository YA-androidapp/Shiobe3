package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.Locale;

public final class Kana {
	private static final String[][] tb = { { "", "あ", "い", "う", "え", "お" }, { "k", "か", "き", "く", "け", "こ" }, { "s", "さ", "し", "す", "せ", "そ" }, { "t", "た", "ち", "つ", "て", "と" },
			{ "n", "な", "に", "ぬ", "ね", "の" }, { "h", "は", "ひ", "ふ", "へ", "ほ" }, { "m", "ま", "み", "む", "め", "も" }, { "y", "や", "い", "ゆ", "いぇ", "よ" }, { "r", "ら", "り", "る", "れ", "ろ" },
			{ "w", "わ", "うぃ", "う", "うぇ", "を" }, { "g", "が", "ぎ", "ぐ", "げ", "ご" }, { "z", "ざ", "じ", "ず", "ぜ", "ぞ" }, { "j", "じゃ", "じ", "じゅ", "じぇ", "じょ" }, { "d", "だ", "ぢ", "づ", "で", "ど" },
			{ "b", "ば", "び", "ぶ", "べ", "ぼ" }, { "p", "ぱ", "ぴ", "ぷ", "ぺ", "ぽ" }, { "gy", "ぎゃ", "ぎぃ", "ぎゅ", "ぎぇ", "ぎょ" }, { "zy", "じゃ", "じぃ", "じゅ", "じぇ", "じょ" }, { "jy", "じゃ", "じぃ", "じゅ", "じぇ", "じょ" },
			{ "dy", "ぢゃ", "ぢぃ", "ぢゅ", "ぢぇ", "ぢょ" }, { "by", "びゃ", "びぃ", "びゅ", "びぇ", "びょ" }, { "py", "ぴゃ", "ぴぃ", "ぴゅ", "ぴぇ", "ぴょ" }, { "l", "ぁ", "ぃ", "ぅ", "ぇ", "ぉ" },
			{ "v", "ヴぁ", "ヴぃ", "ヴ", "ヴぇ", "ヴぉ" }, { "sh", "しゃ", "し", "しゅ", "しぇ", "しょ" }, { "sy", "しゃ", "し", "しゅ", "しぇ", "しょ" }, { "ch", "ちゃ", "ち", "ちゅ", "ちぇ", "ちょ" },
			{ "cy", "ちゃ", "ち", "ちゅ", "ちぇ", "ちょ" }, { "f", "ふぁ", "ふぃ", "ふ", "ふぇ", "ふぉ" }, { "q", "くぁ", "くぃ", "く", "くぇ", "くぉ" }, { "ky", "きゃ", "きぃ", "きゅ", "きぇ", "きょ" },
			{ "ty", "ちゃ", "ちぃ", "ちゅ", "ちぇ", "ちょ" }, { "ny", "にゃ", "にぃ", "にゅ", "にぇ", "にょ" }, { "hy", "ひゃ", "ひぃ", "ひゅ", "ひぇ", "ひょ" }, { "my", "みゃ", "みぃ", "みゅ", "みぇ", "みょ" },
			{ "ry", "りゃ", "りぃ", "りゅ", "りぇ", "りょ" }, { "ly", "ゃ", "ぃ", "ゅ", "ぇ", "ょ" }, { "lt", "た", "ち", "っ", "て", "と" }, };

	private static final String R2K(final String s, final int n) {
		if (n < 5) {
			for (int i = 0; i < 38; i++) {
				if (s.equals(tb[i][0])) {
					return tb[i][n + 1];
				}
			}
			return s + tb[0][n + 1];
		} else if (n == 5) {
			return "ん";
		} else {
			return "っ";
		}
	}

	public static final String Rome2Kana(final String s) {
		String buf = "";
		String result = "";
		final String source = s.toLowerCase(Locale.ENGLISH);
		String tmp = "";

		for (int i = 0; i < source.length(); i++) {
			tmp = source.substring(i, i + 1);

			if (tmp.equals("a")) {
				result = result + R2K(buf, 0);
				buf = "";
			} else if (tmp.equals("i")) {
				result = result + R2K(buf, 1);
				buf = "";
			} else if (tmp.equals("u")) {
				result = result + R2K(buf, 2);
				buf = "";
			} else if (tmp.equals("e")) {
				result = result + R2K(buf, 3);
				buf = "";
			} else if (tmp.equals("o")) {
				result = result + R2K(buf, 4);
				buf = "";
			} else {
				if (buf.equals("n")) {
					if (!( tmp.equals("y") )) {
						result = result + R2K(buf, 5);
						buf = "";
						if (tmp.equals("n"))
							continue;
					}
				}

				if (java.lang.Character.isLetter(tmp.charAt(0))) {
					if (buf.equals(tmp)) {
						result = result + R2K(buf, 6);
						buf = tmp;
					} else {
						buf = buf + tmp;
					}
				} else {
					result = result + buf + tmp;
					buf = "";
				}
			}
		}
		return result + buf;
	}
}
