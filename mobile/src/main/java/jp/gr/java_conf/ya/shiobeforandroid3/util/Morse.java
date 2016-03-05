package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.Arrays;
import java.util.List;

public final class Morse {

	private final Character[] jchars = new Character[] { '゛', '゜', '（', '）', '、', '」', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'ア', 'イ', 'ウ', 'エ', 'オ', 'カ', 'キ', 'ク', 'ケ', 'コ', 'サ', 'シ',
			'ス', 'セ', 'ソ', 'タ', 'チ', 'ツ', 'テ', 'ト', 'ナ', 'ニ', 'ヌ', 'ネ', 'ノ', 'ハ', 'ヒ', 'フ', 'ヘ', 'ホ', 'マ', 'ミ', 'ム', 'メ', 'モ', 'ヤ', 'ユ', 'ヨ', 'ラ', 'リ', 'ル', 'レ', 'ロ', 'ワ', 'ヰ', 'ヱ', 'ヲ', 'ン', '訂',
			'ー' };

	private final String[] jstrs = new String[] { "..", "..__.", "_.__._", "._.._.", "._._._", "._._..", "_____", ".____", "..___", "...__", "...._", ".....", "_....", "__...", "___..", "____.",
			"__.__", "._", ".._", "_.___", "._...", "._..", "_._..", "..._", "_.__", "____", "_._._", "__._.", "___._", ".___.", "___.", "_.", ".._.", ".__.", "._.__", ".._..", "._.", "_._.", "....",
			"__._", "..__", "_...", "__.._", "__..", ".", "_..", "_.._", ".._._", "_", "_..._ ", "_.._.", ".__", "_..__", "__", "...", "__.", "_.__.", "___", "._._", "_._", "._.._", ".__..", ".___",
			"._._.", "..._.", ".__._" };

	private final Character[] echars = new Character[] { '\'', '-', '"', '(', ')', '*', ',', '.', '/', ':', '?', '@', '+', '=', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '訂' };

	private final String[] estrs = new String[] { ".----.", "-....-", ".-..-.", "-.--.", "-.--.-", "-..-", "--..--", ".-.-.-", "-..-.", "---...", "..--..", ".--.-.", ".-.-.", "-...-", "-----",
			".----", "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---",
			".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", "........" };

	static final String HiraganaToKatakana(final String str) {
		final StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < sb.length(); i++) {
			final char c = sb.charAt(i);
			if (c >= 'ぁ' && c <= 'ん') {
				sb.setCharAt(i, (char) ( c - 'ぁ' + 'ァ' ));
			}
		}
		return sb.toString();
	}

	static final String ZenkakuAlphabetToHankakuAlphabet(final String str) {
		final StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < sb.length(); i++) {
			final char c = sb.charAt(i);
			if (c >= 'ａ' && c <= 'ｚ') {
				sb.setCharAt(i, (char) ( c - 'ａ' + 'a' ));
			} else if (c >= 'Ａ' && c <= 'Ｚ') {
				sb.setCharAt(i, (char) ( c - 'Ａ' + 'A' ));
			}
		}
		return sb.toString();
	}

	static final String ZenkakuNumToHankakuNum(final String str) {
		final StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < sb.length(); i++) {
			final char c = sb.charAt(i);
			if (c >= '０' && c <= '９') {
				sb.setCharAt(i, (char) ( c - '０' + '0' ));
			}
		}
		return sb.toString();
	}

	private final String ECharToEMorse(final char charAt) {
		final List<Character> charsList = Arrays.asList(echars);
		final int index = charsList.indexOf(charAt);
		if (( index > -1 ) && ( index < echars.length )) {
			return estrs[index];
		} else {
			return " ";
		}
	}

	public final String EMorsesToEString(String str) {
		final StringBuilder stringBuilder = new StringBuilder(187); // 140 * 4/3

		str =
				str.replaceAll("-", "_").replaceAll("＿", "_").replaceAll("ー", "_").replaceAll("－", "_").replaceAll("‐", "_").replaceAll("―", "_").replaceAll("。", ".").replaceAll("・", ".").replaceAll("、", ".").replaceAll("，", ".").replaceAll(",", ".");

		final String[] strArray = str.split(" ");
		for (final String st : strArray) {
			stringBuilder.append(EMorseToEChar(st));
		}
		return stringBuilder.toString();
	}

	private final char EMorseToEChar(final String str) {
		final List<String> strsList = Arrays.asList(estrs);
		final int index = strsList.indexOf(str);
		if (( index > -1 ) && ( index < estrs.length )) {
			return echars[index];
		} else {
			return ' ';
		}
	}

	public final String EStringToEMorses(String str) {
		final int sbInitSize = 8 * str.length(); // 6 * str * 4/3
		final StringBuilder stringBuilder = new StringBuilder(sbInitSize);

		str = ZenkakuNumToHankakuNum(ZenkakuAlphabetToHankakuAlphabet(str));

		int i = 0;
		final int length = str.length();
		for (i = 0; i < length; i++) {
			stringBuilder.append(ECharToEMorse(str.charAt(i)) + " ");
		}
		return stringBuilder.toString();
	}

	private final String JCharToJMorse(final char charAt) {
		final List<Character> charsList = Arrays.asList(jchars);
		final int index = charsList.indexOf(charAt);
		if (( index > -1 ) && ( index < jchars.length )) {
			return jstrs[index];
		} else {
			return " ";
		}
	}

	public final String JMorsesToJString(String str) {
		final StringBuilder stringBuilder = new StringBuilder(187); // 140 * 4/3

		str =
				str.replaceAll("-", "_").replaceAll("＿", "_").replaceAll("ー", "_").replaceAll("－", "_").replaceAll("‐", "_").replaceAll("―", "_").replaceAll("。", ".").replaceAll("・", ".").replaceAll("、", ".").replaceAll("，", ".").replaceAll(",", ".");

		final String[] strArray = str.split(" ");
		for (final String st : strArray) {
			stringBuilder.append(JMorseToJChar(st));
		}
		return stringBuilder.toString();
	}

	private final char JMorseToJChar(final String str) {
		final List<String> strsList = Arrays.asList(jstrs);
		final int index = strsList.indexOf(str);
		if (( index > -1 ) && ( index < jstrs.length )) {
			return jchars[index];
		} else {
			return ' ';
		}
	}

	public final String JStringToJMorses(String str) {
		final int sbInitSize = 8 * str.length(); // 6 * str * 4/3
		final StringBuilder stringBuilder = new StringBuilder(sbInitSize);

		str = ZenkakuNumToHankakuNum(HiraganaToKatakana(str));

		int i = 0;
		final int length = str.length();
		for (i = 0; i < length; i++) {
			stringBuilder.append(JCharToJMorse(str.charAt(i)) + " ");
		}
		return stringBuilder.toString();
	}

}
