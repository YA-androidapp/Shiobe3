package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.Comparator;
import java.util.Locale;

import twitter4j.UserList;

public final class ListNameComparator implements Comparator<UserList> {
	//比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
	@Override
	public final int compare(final UserList a, final UserList b) {
		final String name1 = ( a.getFullName() ).toLowerCase(Locale.ENGLISH);
		final String name2 = ( b.getFullName() ).toLowerCase(Locale.ENGLISH);
		if (name1.compareTo(name2) > 0) {
			return 1;
		} else if (name1.equals(name2)) {
			return 0;
		} else {
			return -1;
		}
	}
}