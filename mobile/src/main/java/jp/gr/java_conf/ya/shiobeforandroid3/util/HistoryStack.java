package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public final class HistoryStack<T> {
	// http://zawapro.com/?p=1065

	private final Deque<T> undoDeque = new LinkedList<T>();
	private final Deque<T> redoDeque = new LinkedList<T>();

	/**
	 * 履歴の追加
	 * @param history
	 */
	public final void add(final T history) {
		try {
			undoDeque.push(history);
		} catch (final Exception e) {
		}
		try {
			redoDeque.clear();
		} catch (final Exception e) {
		}
	}

	/**
	 * 配列
	 * @return
	 */
	public final ArrayList<T> getArray() {
		final ArrayList<T> result = new ArrayList<T>();
		for (final T i : iterateRedo()) {
			result.add(i);
		}
		for (final T i : iterateUndo()) {
			result.add(i);
		}

		return result;
	}

	/**
	 * リドゥの列挙
	 * @return
	 */
	public final Iterable<T> iterateRedo() {
		return redoDeque;
	}

	/**
	 * アンドゥの列挙
	 * @return
	 */
	public final Iterable<T> iterateUndo() {
		return undoDeque;
	}

	/**
	 * リドゥ
	 * @return
	 */
	public final T redo() {

		T result = null;
		if (!redoDeque.isEmpty()) {
			try {
				result = redoDeque.pop();
				undoDeque.push(result);
			} catch (final Exception e) {
			}
		}

		return result;
	}

	/**
	 * アンドゥ
	 * @return
	 */
	public final T undo() {

		T result = null;
		if (!undoDeque.isEmpty()) {
			try {
				result = undoDeque.pop();
				redoDeque.push(result);
			} catch (final Exception e) {
			}
		}

		return result;
	}
}