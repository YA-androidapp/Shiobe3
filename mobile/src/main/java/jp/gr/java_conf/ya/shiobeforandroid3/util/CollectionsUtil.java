package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CollectionsUtil {
	// http://javasampleoitoku.blog.fc2.com/blog-entry-34.html
	// https://gist.github.com/tksmaru/5301447

	public static final <T> List<List<T>> devide(List<T> origin, int size) {
		if (origin == null || origin.isEmpty() || size <= 0) {
			return Collections.emptyList();
		}

		final int block = origin.size() / size + ( origin.size() % size > 0 ? 1 : 0 );

		List<List<T>> devidedList = new ArrayList<List<T>>(block);
		for (int i = 0; i < block; i++) {
			final int start = i * size;
			final int end = Math.min(start + size, origin.size());
			devidedList.add(new ArrayList<T>(origin.subList(start, end)));
		}
		return devidedList;
	}

	public static final long[] listLong2longarray(List<Long> list) {
		final long[] dst = new long[list.size()];
		int i = 0;
		for (Iterator<Long> iter = list.iterator(); iter.hasNext();) {
			dst[i++] = ( iter.next() ).longValue();
		}
		return dst;
	}

	// http://javasampleoitoku.blog.fc2.com/blog-entry-34.html
	public final <T> boolean containsDuplicate(Collection<T> col) {
		final Set<T> set = new HashSet<T>();
		for (final T o : col) {
			if (!set.add(o)) {
				return true;
			}
		}
		return false;
	}

	// http://javasampleoitoku.blog.fc2.com/blog-entry-34.html
	/**
	 * 指定したコレクション中の重複している要素を抽出したセットを返します。<br>
	 * セットから要素を取り出す場合は、重複を検出した順番が保証されます。
	 *
	 * @param col    抽出対象のコレクション
	 *
	 * @return 抽出結果のセット
	 *
	 * @throws NullPointerException    colがnullの場合
	 */
	public final <T> Set<T> extractDuplicate(Collection<T> col) {
		final Set<T> set = new HashSet<T>();
		final Set<T> result = new LinkedHashSet<T>();
		for (final T o : col) {
			if (!set.add(o)) {
				result.add(o);
			}
		}
		return result;
	}

	/**
	 * 指定したコレクション中の重複している要素をすべて削除します。
	 *
	 * @param col    削除対象のコレクション
	 */
	public final <T> void removeDuplicate(Collection<T> col) {
		if (col == null) {
			return;
		}

		final Set<T> set = new HashSet<T>();
		final List<T> newList = new ArrayList<T>(col.size());
		for (final T o : col) {
			if (set.add(o)) {
				newList.add(o);
			}
		}
		if (newList.size() != col.size()) {
			col.clear();
			col.addAll(newList);
		}
	}
}
