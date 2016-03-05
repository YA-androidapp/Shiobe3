package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.List;

import twitter4j.Status;
import twitter4j.User;

public final class BinarySearchUtil {
	public static final int binary_search(final long needle, final List<Status> tweets, final boolean mode) {
		int low = 0;
		int high = tweets.size() - 1;
		int mid = ( low + high ) / 2;

		while (low <= high) {
			mid = ( low + high ) / 2;
			if (needle == tweets.get(mid).getId()) {
				return mid;
			} else if (needle > tweets.get(mid).getId()) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		if (mode) {
			return mid;
		} else {
			return -1;
		}
	}

	public static final int binary_search_users(final long needle, final List<User> users) {
		int low = 0;
		int high = users.size() - 1;
		int mid = ( low + high ) / 2;

		while (low <= high) {
			mid = ( low + high ) / 2;
			if (needle == users.get(mid).getId()) {
				return mid;
			} else if (needle > users.get(mid).getId()) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		return mid;
	}
}
